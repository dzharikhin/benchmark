Rest impls benchmark stand
=============
To measure performance I recommend to use [Apache Bench](https://httpd.apache.org/docs/2.4/programs/ab.html)

###By default:
Modules can be run via main classes
* jersey 1 resource is deployed on localhost:8081/rest/v1 - V1App
* jersey 2 resource is deployed on localhost:8082/rest/v2 - V2App
* spring mvc resource is deployed on localhost:8083/rest/mvc - MvcApp
* restEasy resource is deployed on localhost:8083/rest/resteasy - MvcApp

or it can be run via
```bash
java -jar [module-name]-jar-with-dependencies.jar
```

There's Client benchmark for **RUDE** comparison between jersey versions

There are several cases to measure:
+ *string* - returns constant [v1|v2|mvc|resteasy] as string
+ *stringResponse(**Jersey & restEasy**)* - returns constant [v1|v2|resteasy] as string wrapped in response
+ *byteArray(**Jersey & restEasy**)* - returns constant [v1|v2|resteasy] as byte[]
+ *byteArrayResponse(**Jersey & restEasy**)* - returns constant [v1|v2|resteasy] as byte[] wrapped in response
+ *streamingOutput(**Jersey & restEasy**)* - returns constant [v1|v2|resteasy] as StreamingOutput
+ *streamingOutputResponse(**Jersey & restEasy**)* - returns constant [v1|v2|resteasy] as StreamingOutput wrapped in response

+ *testGet* - method returning xml of generated entity
```bash
ab -n 10000 -c 4 'localhost:808[n]/rest/[v1|v2|mvc|resteasy]/testGet'
```
+ *testGetJson* - method returning json
```bash
ab -n 10000 -c 4 'localhost:808[n]/rest/[v1|v2|mvc|resteasy]/testGetJson'
```
>jersey uses different marshaller code, so json comparison may be unfair, so I provide testGetJackson
+ *testGetJackson* - method returning json using Jackson directly - same version jersey1 and jersey2
```bash
ab -n 10000 -c 4 'localhost:808[n]/rest/[v1|v2|mvc|resteasy]/testGetJson'
```
+ *testBuild* - method building entity from input parameters(@QueryParam+@HeaderParam+@FormParam)
```bash
ab -n 10000 -c 4 -H 'anInt: 1'  -p form-data -T 'application/x-www-form-urlencoded' 
'localhost:808[n]/rest/[v1|v2|mvc|resteasy]/testBuild?string=stringA&string=stringB'
```
+ *testBeanParam(**Jersey V2 & restEasy ONLY**)* - method building entity via @BeanParam from input parameters(query_param+header_param+form_param)
```bash
ab -n 10000 -c 4 -H 'anInt: 1'  -p form-data -T 'application/x-www-form-urlencoded' 'localhost:808[n]/rest/[v2|resteasy]/testBeanParam?string=stringA&string=stringB'
```
