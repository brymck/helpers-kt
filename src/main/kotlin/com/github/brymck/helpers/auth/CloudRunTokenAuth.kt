package com.github.brymck.helpers.auth

import com.auth0.jwt.JWT
import io.grpc.CallCredentials
import io.grpc.Metadata
import io.grpc.Status.UNAUTHENTICATED
import java.time.Instant
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit.MILLISECONDS
import mu.KLogging
import okhttp3.OkHttpClient
import okhttp3.Request

internal class CloudRunTokenAuth(host: String) : CallCredentials() {
    companion object : KLogging() {
        private val authorization = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)
        private const val METADATA_HOST = "http://metadata.google.internal"
        private const val IDENTITY_PATH = "$METADATA_HOST/instance/service-accounts/default/identity"
    }

    private val ok = OkHttpClient.Builder()
        .readTimeout(500, MILLISECONDS)
        .writeTimeout(500, MILLISECONDS)
        .build()

    private val tokenUrl = "$IDENTITY_PATH?audience=https://$host"

    private var token = ""

    private var renewAt = Instant.EPOCH

    override fun applyRequestMetadata(requestInfo: RequestInfo?, appExecutor: Executor?, applier: MetadataApplier?) {
        applier as MetadataApplier
        val metadata = Metadata()
        if (renewAt < Instant.now()) {
            val request = Request.Builder()
                .url(tokenUrl)
                .addHeader("Metadata-Flavor", "Google")
                .get()
                .build()
            logger.debug { "requesting new token from $tokenUrl" }
            val body = ok.newCall(request).execute().body
            if (body == null) {
                applier.fail(UNAUTHENTICATED.withDescription("no body received when requesting token from metadata"))
                return
            }
            token = body.string()
            logger.debug { "received a new token: ${token.take(8)}..." }
            renewAt = JWT.decode(token).expiresAt.toInstant().plusSeconds(30)
            logger.debug { "new token expires at $renewAt" }
        }
        if (token == "") {
            applier.fail(UNAUTHENTICATED.withDescription("token is empty"))
        } else {
            metadata.put(authorization, "Bearer $token")
            applier.apply(metadata)
        }
    }

    override fun thisUsesUnstableApi() {}
}
