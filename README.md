Jersey+Spring benchmark stand
=============
To measure performance I recommend to use [Apache Bench](https://httpd.apache.org/docs/2.4/programs/ab.html)

###By default:
* jersey 1 resource is deployed on localhost:8081/rest/v1
* jersey 2 resource is deployed on localhost:8082/rest/v2

There are several cases to measure:
+ *testGet* - method returning xml of generated entity
```bash
ab -n 10000 -c 4 'localhost:8082/rest/v2/testGet'
```
+ *testGetJson* - method returning json
```bash
ab -n 10000 -c 4 'localhost:8082/rest/v2/testGetJson'
```
+ *testGetJackson* - method returning json using Jackson directly - same version jersey1 and jersey2
```bash
ab -n 10000 -c 4 'localhost:8082/rest/v2/testGetJson'
```
>jersey uses different marshaller code, so json comparison may be unfair
+ *testBuild* - method building entity from input parameters(@QueryParam+@HeaderParam+@FormParam)
```bash
ab -n 10000 -c 4 -H 'anInt: 1'  -p form-data -T 'application/x-www-form-urlencoded' 
'localhost:8082/rest/v2/testBuild?string=stringA&string=stringB'
```
+ *testBeanParam(**V2 ONLY**)* - method building entity via @BeanParam from input parameters(query_param+header_param+form_param)
```bash
ab -n 10000 -c 4 -H 'anInt: 1'  -p form-data -T 'application/x-www-form-urlencoded' 
'localhost:8082/rest/v2/testBeanParam?string=stringA&string=stringB'
```
