package com.github.brymck.helpers

import com.github.brymck.helpers.auth.ApiKeyAuth
import com.github.brymck.helpers.auth.CloudRunTokenAuth
import com.github.brymck.helpers.auth.LocalTokenAuth
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
        private val apiKeyIsSet = if (isOnCloudRun) {
            logger.debug { "ignoring whether API key is set due to Cloud Run environment" }
            false
        } else {
            if (System.getenv("BRYMCK_IO_API_KEY") == null) {
                logger.debug { "API key is not set in environment variable BRYMCK_IO_API_KEY" }
                false
            } else {
                logger.debug { "API key found in environment variable BRYMCK_IO_API_KEY" }
                true
            }
        }
        private const val HTTPS_PORT = 443
    }

    private val channel: ManagedChannel

    private val credentials: CallCredentials

    init {
        val host = if (apiKeyIsSet) "gateway-4tt23pryoq-an.a.run.app" else "$name-4tt23pryoq-an.a.run.app"
        channel = ManagedChannelBuilder
            .forAddress(host, HTTPS_PORT)
            .useTransportSecurity()
            .build()
        logger.debug { "opened channel to $host" }
        credentials = when {
            isOnCloudRun -> CloudRunTokenAuth(host)
            apiKeyIsSet -> ApiKeyAuth()
            else -> LocalTokenAuth()
        }
    }

    fun stub(): T {
        return stubber(channel).withCallCredentials(credentials)
    }
}
