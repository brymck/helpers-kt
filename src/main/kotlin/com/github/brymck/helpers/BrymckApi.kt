package com.github.brymck.helpers

import io.grpc.Channel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.AbstractStub

class BrymckApi<T : AbstractStub<T>>(name: String, val stubber: (Channel) -> T) {
    private val channel = ManagedChannelBuilder
        .forAddress("$name-4tt23pryoq-an.a.run.app", 443)
        .useTransportSecurity()
        .build()

    private val credentials = Token()

    fun stub(): T {
        return stubber(channel).withCallCredentials(credentials)
    }
}
