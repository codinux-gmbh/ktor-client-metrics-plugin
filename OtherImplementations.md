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