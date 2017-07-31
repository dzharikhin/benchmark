Jersey+Spring benchmark stand
=============
To measure performance I recommend to use [Apache Bench](https://httpd.apache.org/docs/2.4/programs/ab.html)

###By default:
* jersey 1 resource is deployed on localhost:8081/rest/v1 - V1App
* jersey 2 resource is deployed on localhost:8082/rest/v2 - V2App
* spring mvc resource is deployed on localhost:8083/rest/mvc - MvcApp

There are several cases to measure:
+ *testGet* - method returning xml of generated entity
```bash
ab -n 10000 -c 4 'localhost:808[n]/rest/[v1|v2|mvc]/testGet'
```
+ *testGetJson* - method returning json
```bash
ab -n 10000 -c 4 'localhost:808[n]/rest/[v1|v2|mvc]/testGetJson'
```
>jersey uses different marshaller code, so json comparison may be unfair, so I provide testGetJackson
+ *testGetJackson* - method returning json using Jackson directly - same version jersey1 and jersey2
```bash
ab -n 10000 -c 4 'localhost:808[n]/rest/[v1|v2|mvc]/testGetJson'
```
+ *testBuild* - method building entity from input parameters(@QueryParam+@HeaderParam+@FormParam)
```bash
ab -n 10000 -c 4 -H 'anInt: 1'  -p form-data -T 'application/x-www-form-urlencoded' 
'localhost:808[n]/rest/[v1|v2|mvc]/testBuild?string=stringA&string=stringB'
```
+ *testBeanParam(**V2 ONLY**)* - method building entity via @BeanParam from input parameters(query_param+header_param+form_param)
```bash
ab -n 10000 -c 4 -H 'anInt: 1'  -p form-data -T 'application/x-www-form-urlencoded' 
'localhost:8082/rest/v2/testBeanParam?string=stringA&string=stringB'
```
