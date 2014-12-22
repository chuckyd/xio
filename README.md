xio
===

High performance Multithreaded non-blocking Async I/O for Java 8

`Simplicity Leads to Purity - Jiro`

## Obligatory ascii art

### [Depricated] -> Moving Blocking Read work to master

# Gatekeeper

* Network Acceptor
* IP Filter
* Rate Limiter
* Any other Filter that can be applied to a Network Connection

```
+--------------------------------------+
|                                      |
|   GateKeeper                         |
|                                      |
|                                      |
|                                      |
|                                      |
|               +------------------+   |
|               |                  |   |
|               |    Acceptor      |   |
|               |                  |   |
|               +---------+--------+   |
|                         |            |
|                         |            |
|               +---------+--------+   |
|               |                  |   |
|               |    IP Filter     |   |
|               |                  |   |
|               +---------+--------+   |
|                         |            |
|                         |            |
|               +---------+--------+   |
|               |                  |   |
|               |   Rate Limiter   |   |
|               |                  |   |
|               +------------------+   |
|                                      |
|                                      |
+--------------------------------------+
```

# Terminator

* SSL/TLS wrapped Network Connection

```
+--------------------------------------+
|                                      |
|   Terminator                         |
|                                      |
|                                      |
|                                      |
|                                      |
|               +------------------+   |
|               |                  |   |
|               |    Connection    |   |
|               |                  |   |
|               +---------+--------+   |
|                         |            |
|                         |            |
|               +---------+--------+   |
|               |                  |   |
|               |    Crypto Engine |   |
|               |                  |   |
|               +------------------+   |
|                                      |
|                                      |
|                                      |
|                                      |
|                                      |
|                                      |
|                                      |
|                                      |
|                                      |
+--------------------------------------+
```

## Server Quickstart


####Server
```java
Server server = new Server();
server.addKey("resourcse/my.key");
server.addCsr("resources/my.csr");
server.announce("/my/server/v1/, ("zk://localhost:2181", "zk://localhost:2182", "zk://localhost:2183"));
server.port(443);
server.addRoute("/", rootService);
server.addRoute("/health", heathService);
server.addRoute("/admin", adminService);
server.await();
```

####Service
```java
Service rootService = new Service();
rootService.proto(HTTP/2);
rootService.addFilter(timeoutFilert)
            .andThen(ratelimitFilter)
            .andThen(oAuthService);
```

####Filter
```java
// RateLimitFilter is specified as # of Connections, to what, over what period
Function<Channel, Filter> ratelimitFilter = new RateLimitFilter(200, perHost, perSecond)
ratelimitFilter.hosts("/my/server/v1/, ("zk://localhost:2181", "zk://localhost:2182", "zk://localhost:2183"));
```


## Client Quickstart
```java
Client xioClient =  new XioClient("get", "https://github.com/users");
xioClient.response    // will return an int of the returncode i.e 200
xioClient.headers     // will dump the headers as a HashMap
xioClient.body        // will dump the body of the HTTP response as a string
xioClient.body.toJson // will parse the body of the response as json

Client xioRestClient = new XioClient();
xioRestClient.method(post);
xioRestClient.url("https://github.com/users");
xioRestClient.addHeader("X-Auth: My Voice is My Password");
Future<Response> response = xioRestClient.send;
response.onFailure(new XioClientException);
response.onSuccess((r) -> System.out.println(r.body.toString));

Client xioFancyClient = new xioClient();
xioFancyClient.proto(HTTP/2);
xioFancyClient.auth("resources/myAuth.key");
xioFancyClient.secret("mySecret");
xioFancyClient.hosts("/my/server/v1/, ("zk://localhost:2181", "zk://localhost:2182", "zk://localhost:2183"));
xioFancyClient.lb(roundRobin, 3, false) // load-balancer type, number of retries before ejection, auto rejoing to cluster
xioFancyClient.method(get);
Future<Response> response = xioFancyClient.send;
response.onFailure(new XioClientException);
response.onSuccess((r) -> System.out.println(r.body.toString));
```
