package com.github.brymck.helpers.auth

import io.grpc.CallCredentials
import io.grpc.Metadata
import java.util.concurrent.Executor

internal class NoAuth : CallCredentials() {
    override fun applyRequestMetadata(requestInfo: RequestInfo?, appExecutor: Executor?, applier: MetadataApplier?) {
        applier as MetadataApplier
        applier.apply(Metadata())
    }

    override fun thisUsesUnstableApi() {}
}
