helpers
=======

[![CircleCI](https://circleci.com/gh/brymck/helpers-kt.svg?style=shield)](https://circleci.com/gh/brymck/helpers-kt)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

This Kotlin library provides helpful classes and functions for use with my cloud projects.

Usage
-----

Include this in your POM:

```xml
<dependency>
    <groupId>com.github.brymck</groupId>
    <artifactId>helpers</artifactId>
    <version>0.0.6-SNAPSHOT</version>
</dependency>
```

### APIs

Firstly, you would add a dependency on `brymck-securities` in your POM:

```xml
<dependency>
    <groupId>com.github.brymck</groupId>
    <artifactId>brymck-securities</artifactId>
    <version>${brymck-securities.version}</version>
</dependency>
```

If the service is already running in the `brymck.io` Google Cloud project, no authorization configuration is needed;
this library will handle requesting tokens from the Google Cloud Platform metadata server. If running locally, you will
need to set the environment variable `BRYMCK_IO_API_KEY` to a valid API key.

Then, in the app's initialization, you would create a `BrymckApi` containing the name of the service and how a "stub"
(a.k.a. a client) should be instantiated. When `BrymckApi` is created, it opens a long-lived channel, while stubs can
be created on demand. For instance, the below could be in a Spring Boot configuration:

```kotlin
@Configuration
class MyConfiguration {
    // ...
    @Bean
    fun securitiesApi() =
        BrymckApi("securities-service") { SecuritiesAPIGrpc.newBlockingStub(it) }
    // ...
}
```

and then it could be used in a Spring Boot service as

```kotlin
@Service
class NameService(securitiesApi: BrymckApi<SecuritiesAPIBlockingStub>) {
    // ...
    fun getSecurityName(id: Long): String {
        val request = GetSecurityRequest.newBuilder().setId(1).build()
        val response = service.stub().getSecurity(request)
        return response.security.name
    }
    // ...
}
```
