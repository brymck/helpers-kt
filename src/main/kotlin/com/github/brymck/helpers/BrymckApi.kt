package com.github.brymck.helpers

import com.github.brymck.helpers.tokens.CloudRunToken
import com.github.brymck.helpers.tokens.LocalToken
import io.grpc.CallCredentials
import io.grpc.Channel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.AbstractStub

class BrymckApi<T : AbstractStub<T>>(name: String, val stubber: (Channel) -> T) {
    companion object {
        private val isOnCloudRun = (System.getenv("K_SERVICE") != null)
        private const val HTTPS_PORT = 443
    }

    private val channel: ManagedChannel

    private val credentials: CallCredentials

    init {
        val host = "$name-4tt23pryoq-an.a.run.app"
        channel = ManagedChannelBuilder
            .forAddress(host, HTTPS_PORT)
            .useTransportSecurity()
            .build()
        credentials = if (isOnCloudRun) CloudRunToken(host) else LocalToken()
    }

    fun stub(): T {
        return stubber(channel).withCallCredentials(credentials)
    }
}
