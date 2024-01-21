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