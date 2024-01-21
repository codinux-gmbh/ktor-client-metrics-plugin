States the created metrics of other HTTP client metrics implementations:

## RESTeasy / Quarkus

```text
# HELP http_client_requests_seconds  
# TYPE http_client_requests_seconds summary
http_client_requests_seconds_count{clientName="v2.jokeapi.dev",method="GET",outcome="SUCCESS",status="200",uri="root",} 1.0
http_client_requests_seconds_sum{clientName="v2.jokeapi.dev",method="GET",outcome="SUCCESS",status="200",uri="root",} 0.195557799
# HELP http_client_requests_seconds_max  
# TYPE http_client_requests_seconds_max gauge
http_client_requests_seconds_max{clientName="v2.jokeapi.dev",method="GET",outcome="SUCCESS",status="200",uri="root",} 0.195557799
```

## Spring RestClient

Spring [org.springframework.web.client.RestClient](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/client/RestClient.html) doesn't seem to expose metrics as of Spring 6.1.3 / Spring Boot 3.2.2

## Spring WebClient

```text
# HELP http_client_requests_seconds  
# TYPE http_client_requests_seconds summary
http_client_requests_seconds_count{client_name="v2.jokeapi.dev",error="none",exception="none",method="GET",outcome="SUCCESS",status="200",uri="none",} 1.0
http_client_requests_seconds_sum{client_name="v2.jokeapi.dev",error="none",exception="none",method="GET",outcome="SUCCESS",status="200",uri="none",} 0.435592584
# HELP http_client_requests_seconds_max  
# TYPE http_client_requests_seconds_max gauge
http_client_requests_seconds_max{client_name="v2.jokeapi.dev",error="none",exception="none",method="GET",outcome="SUCCESS",status="200",uri="none",} 0.435592584

# HELP http_client_requests_active_seconds  
# TYPE http_client_requests_active_seconds summary
http_client_requests_active_seconds_active_count{client_name="v2.jokeapi.dev",exception="none",method="GET",outcome="UNKNOWN",status="CLIENT_ERROR",uri="none",} 0.0
http_client_requests_active_seconds_duration_sum{client_name="v2.jokeapi.dev",exception="none",method="GET",outcome="UNKNOWN",status="CLIENT_ERROR",uri="none",} 0.0
# HELP http_client_requests_active_seconds_max  
# TYPE http_client_requests_active_seconds_max gauge
http_client_requests_active_seconds_max{client_name="v2.jokeapi.dev",exception="none",method="GET",outcome="UNKNOWN",status="CLIENT_ERROR",uri="none",} 0.0
```


## Micrometer

### OkHttp

```text
# HELP OkHttpClient_seconds Timer of OkHttp operation
# TYPE OkHttpClient_seconds summary
OkHttpClient_seconds_count{host="v2.jokeapi.dev",method="GET",outcome="SUCCESS",status="200",target_host="v2.jokeapi.dev",target_port="443",target_scheme="https",uri="none",} 1.0
OkHttpClient_seconds_sum{host="v2.jokeapi.dev",method="GET",outcome="SUCCESS",status="200",target_host="v2.jokeapi.dev",target_port="443",target_scheme="https",uri="none",} 0.357936119
# HELP OkHttpClient_seconds_max Timer of OkHttp operation
# TYPE OkHttpClient_seconds_max gauge
OkHttpClient_seconds_max{host="v2.jokeapi.dev",method="GET",outcome="SUCCESS",status="200",target_host="v2.jokeapi.dev",target_port="443",target_scheme="https",uri="none",} 0.357936119
```

### Java HttpClient

```text
# HELP http_client_requests_seconds Timer for JDK's HttpClient
# TYPE http_client_requests_seconds summary
http_client_requests_seconds_count{method="GET",outcome="SUCCESS",status="200",uri="UNKNOWN",} 1.0
http_client_requests_seconds_sum{method="GET",outcome="SUCCESS",status="200",uri="UNKNOWN",} 0.25779498
# HELP http_client_requests_seconds_max Timer for JDK's HttpClient
# TYPE http_client_requests_seconds_max gauge
http_client_requests_seconds_max{method="GET",outcome="SUCCESS",status="200",uri="UNKNOWN",} 0.25779498
```