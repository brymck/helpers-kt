package com.github.brymck.helpers.tokens

import io.grpc.CallCredentials
import io.grpc.Metadata
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import io.grpc.Status.UNAUTHENTICATED
import java.util.concurrent.Executor

internal class LocalToken : CallCredentials() {
    companion object {
        private val authorization = Metadata.Key.of("Authorization", ASCII_STRING_MARSHALLER)
    }

    override fun applyRequestMetadata(requestInfo: RequestInfo?, appExecutor: Executor?, applier: MetadataApplier?) {
        applier as MetadataApplier
        val metadata = Metadata()
        val token = System.getenv("BRYMCK_IO_TOKEN")
        if (token == null) {
            applier.fail(UNAUTHENTICATED.withDescription("environment variable BRYMCK_IO_TOKEN not set"))
        } else {
            metadata.put(authorization, "Bearer $token")
            applier.apply(metadata)
        }
    }

    override fun thisUsesUnstableApi() {}
}
