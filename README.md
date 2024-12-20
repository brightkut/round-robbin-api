# Round Robin API

### How to test with a happy case (round robbin)
1. Start Application `routing-api`(as loadbalancer) default it run on port `8080` 
2. Start Application `simple-api` with 3 instance on port `8081`, `8082` and `8083`
3. It will display this log message for `routing-api` with message then application is registered to loadbalancer successfully.  

```
Register instance: 8081 with this information: ...
Register instance: 8082 with this information: ...
Register instance: 8083 with this information: ... 
```

4. Use runner in `Postman` to call API with 6 requests and verify the message log in `routing-api` , It will display message in order 


```
--Example--  
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #1
Sending request to simple api host: http://localhost:8083 with payload ... -> request number #2
Sending request to simple api host: http://localhost:8081 with payload ... -> request number #3
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #4
Sending request to simple api host: http://localhost:8083 with payload ... -> request number #5
Sending request to simple api host: http://localhost:8081 with payload ... -> request number #6 
```