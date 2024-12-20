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

4. Use run collection (`happy-case-round-robbin-api.postman_collection`) in `Postman` to call API with 6 requests and verify the message log in `routing-api` , It will display message in order 

```  
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #1
Sending request to simple api host: http://localhost:8083 with payload ... -> request number #2
Sending request to simple api host: http://localhost:8081 with payload ... -> request number #3
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #4
Sending request to simple api host: http://localhost:8083 with payload ... -> request number #5
Sending request to simple api host: http://localhost:8081 with payload ... -> request number #6 
```

### How to test with a case down one instance

1. You can follow `happy case` first. Next kill one of `simple-api` in this case I will kill an application that run on port `8083`
2. After kill an application, `routing-api` will have a schedule function that run every 10 seconds to check the `lastHealthCheckTime` of each instance If the instance is not send request 
to update `lastHealthCheckTime` within 5 seconds then it will remove this instance from register list.
3. Next the log will display

```
Instance id: 8083, Host: localhost8083 is not healthy.
Remove instance: 8083, lastHealthCheckTime: 2024-12-21T05:11:44.656329,  current time: 2024-12-21T05:11:50.856223 because  it is not healthy
```

4. For next schedule check it will display this log

```
Found 2 instances and start check health
```

5. Use run collection (`happy-case-round-robbin-api.postman_collection`) in `Postman` to call API with 6 requests and verify the message log in `routing-api` , It will display message in order like this
but you will see the instance `8083` will be removed from this api list 

```  
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #1
Sending request to simple api host: http://localhost:8081 with payload ... -> request number #2
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #3
Sending request to simple api host: http://localhost:8081 with payload ... -> request number #4
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #5
Sending request to simple api host: http://localhost:8081 with payload ... -> request number #6 
```

### How to test with a case slow API response time

1. You can start all application by follow `happy case` step first.
2. Use run collection (`slow-api-case-round-robbin-api`) in `Postman` to call API with 9 requests
3. In the request number #3 it is a slow request. When we call API with first 3 request it will forward request
to 3 instance and mark the instance that has slow response time (#3) as slow instance. For the request number #4 - #9 it will forward to 
only 2 instance without slow instance  then it will display this message log

```  
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #1
Sending request to simple api host: http://localhost:8083 with payload ... -> request number #2
Sending request to simple api host: http://localhost:8081 with payload ... -> request number #3

Instance: 8081 has slow response time -> log that mark this instance is slow

Sending request to simple api host: http://localhost:8083 with payload ... -> request number #4
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #5
Sending request to simple api host: http://localhost:8083 with payload ... -> request number #6
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #7
Sending request to simple api host: http://localhost:8083 with payload ... -> request number #8
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #9
```
4. Next we will wait 10 seconds and use run collection (`happy-case-round-robbin-api.postman_collection`) to test again. 
The expectation result should be the same as `happy case` since it has logic to recovery slow instance. In next 10 seconds after 
the instance was marked as slow. we will remove it from slow instance list.

```
Recovery slow instance: 8081 , startSlowTime: 2024-12-21T06:04:40.820396, current time: 2024-12-21T06:05:35.275447 -> log message for recovery slow instance

Sending request to simple api host: http://localhost:8082 with payload ... -> request number #1
Sending request to simple api host: http://localhost:8083 with payload ... -> request number #2
Sending request to simple api host: http://localhost:8081 with payload ... -> request number #3
Sending request to simple api host: http://localhost:8082 with payload ... -> request number #4
Sending request to simple api host: http://localhost:8083 with payload ... -> request number #5
Sending request to simple api host: http://localhost:8081 with payload ... -> request number #6
```