# Ktor client metrics plugin

A plugin for Ktor client to provide HTTP call metrics.

The base is a platform independent Kotlin Multiplatform library for all supported targets.

However, the only implemented metrics registry is for Micrometer and only available for the JVM. If you want to support other tools like Crashlytics, simply implement the [MeterRegistry](ktor-client-metrics/src/commonMain/kotlin/net/codinux/web/ktor/client/metrics/MeterRegistry.kt) interface.

Examples for both variants:

## Micrometer

### Setup

#### Gradle

```
implementation("net.codinux.web:ktor-client-metrics-micrometer:1.0.0")
```

#### Maven:

Maven does not support automatic platform resolution as Gradle does, therefore the specific platform must be specified here:

```
<dependency>
   <groupId>net.codinux.web</groupId>
   <artifactId>ktor-client-metrics-micrometer</artifactId>
   <version>1.0.0</version>
</dependency>
```

### Usage

```kotlin
val micrometerRegistry = ... // get io.micrometer.core.instrument.MeterRegistry, e.g. via dependency injection in Spring or Quarkus

val underTest = HttpClient(/*Set engine*/) {
    install(MicrometerMetrics) {
        this.meterRegistry = micrometerRegistry
    }
}
```

Then these metrics get created (here from Prometheus endpoint):
```text
# HELP http_client_requests_seconds  
# TYPE http_client_requests_seconds summary
http_client_requests_seconds_count{exception="none",host="example.com",method="GET",outcome="SUCCESS",status="200",uri="/user",} 1.0
http_client_requests_seconds_sum{exception="none",host="example.com",method="GET",outcome="SUCCESS",status="200",uri="/user",} 0.001449067
# HELP http_client_requests_seconds_max  
# TYPE http_client_requests_seconds_max gauge
http_client_requests_seconds_max{exception="none",host="example.com",method="GET",outcome="SUCCESS",status="200",uri="/user",} 0.001449067
# HELP http_client_requests_active  
# TYPE http_client_requests_active gauge
http_client_requests_active 0.0
```

### Configuration

You can configure additional tags, adjust the uri tag or configure the tags freely:

```kotlin
val client = HttpClient(engine) {
    install(MicrometerMetrics) {
        this.meterRegistry = micrometerRegistry

        // specify additional tags that should get added to all metrics
        this.additionalTags = mapOf(
            "client" to "Android-App"
        )

        // by default url's path is used. If it doesn't meet your needs, adjust uri tag value:
        this.getUriTag = { url ->
            URLDecoder.decode(url.encodedPathAndQuery, Charsets.UTF_8) // URLDecoder is only available on the JVM
        }

        // the default tags - host, uri, method, status, outcome and exception - are passed via standardTags.
        // You can freely adjust them, e.g. via response values passed by response parameter: httpMethod, url, status, attributes and exception (if any)
        this.configureTags = { standardTags, response ->
            standardTags.remove("exception")
            standardTags["error-message"] = response.exception?.message ?: "none"
        }
    }
}
```


## Multiplatform

### Setup

#### Gradle

```
implementation("net.codinux.web:ktor-client-metrics:1.0.0")
```

### Usage

Create an implementation of net.codinux.web.ktor.client.metrics.MeterRegistry interface:

```kotlin
private val registry = object : MeterRegistry {

    override fun startingRequest(request: HttpRequestBuilder): Any? {
        // you can return a context object, that is then passed to responseRetrieved() method as first parameter,
        // e.g. for Micrometer we start and return a Timer.Sample object here
        return null
    }

    override fun responseRetrieved(context: Any?, response: ResponseData, tags: Map<String, String>) {
        val durationMillis = response.responseTime.timestamp - response.requestTime.timestamp
        val status = response.httpStatusCode
        val url = response.url.toString()
        
        // handle metrics, e.g. send to a backend
    }

}
```

Use that MeterRegistry implementation in Metrics plugin:

```kotlin
val client = HttpClient(engine) {
    install(Metrics) {
        this.meterRegistry = registry
    }
}
```

### Configuration

For Multiplatform the same configuration options are available as for Micrometer, therefor see above how to configure the metrics plugin.


## License

    Copyright 2024 codinux GmbH & Co. KG

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.