package com.github.brymck.helpers

import com.github.brymck.helpers.tokens.CloudRunToken
import com.github.brymck.helpers.tokens.LocalToken
import io.grpc.CallCredentials
import io.grpc.Channel
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.AbstractStub
import mu.KLogging

class BrymckApi<T : AbstractStub<T>>(name: String, val stubber: (Channel) -> T) {
    companion object : KLogging() {
        private val isOnCloudRun = if (System.getenv("K_SERVICE") == null) {
            logger.debug { "assuming no Cloud Run environment because environment variable K_SERVICE is not set" }
            false
        } else {
            logger.debug { "assuming Cloud Run environment because environment variable K_SERVICE is set" }
            true
        }
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
        logger.debug { "opened channel to $host" }
        credentials = if (isOnCloudRun) CloudRunToken(host) else LocalToken()
    }

    fun stub(): T {
        return stubber(channel).withCallCredentials(credentials)
    }
}
