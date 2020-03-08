package com.github.brymck.helpers.auth

import io.grpc.CallCredentials
import io.grpc.Metadata
import io.grpc.Metadata.ASCII_STRING_MARSHALLER
import java.util.concurrent.Executor

internal class ApiKeyAuth : CallCredentials() {
    companion object {
        private val metadata = run {
            val apiKey = System.getenv("BRYMCK_IO_API_KEY")
            check(apiKey != "") { "environment variable BRYMCK_IO_API_KEY not set" }
            val authorization = Metadata.Key.of("x-api-key", ASCII_STRING_MARSHALLER)
            val metadata = Metadata()
            metadata.put(authorization, apiKey)
            metadata
        }
    }

    override fun applyRequestMetadata(requestInfo: RequestInfo?, appExecutor: Executor?, applier: MetadataApplier?) {
        applier as MetadataApplier
        applier.apply(metadata)
    }

    override fun thisUsesUnstableApi() {}
}
