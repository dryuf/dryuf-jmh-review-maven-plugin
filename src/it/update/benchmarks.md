<!--- ( vim: set tw=120: ) --->

### Comparison

<!--- benchmark:table:jarfile::basic test: --->

|Benchmark                                                   |Mode|Cnt|     java 19|
|------------------------------------------------------------|----|---|------------|
|BigCompressedConcurrentThreadBenchmark.benchmark2ClassLoader|avgt|  2| 9362036.799|
|BigCompressedConcurrentThreadBenchmark.benchmark3Class      |avgt|  2|10773556.495|
|BigCompressedSingleThreadBenchmark.benchmark0File           |avgt|  2|  543784.247|
|BigCompressedSingleThreadBenchmark.benchmark1Jar            |avgt|  2| 4281239.514|
|BigCompressedSingleThreadBenchmark.benchmark2ClassLoader    |avgt|  2| 4245898.114|
|BigCompressedSingleThreadBenchmark.benchmark3Class          |avgt|  2| 4191390.999|

<!--- benchmark:table:jarfile:filter=^BigCompressedConcurrentThreadBenchmark\..*:filter test: --->

|Benchmark                                                   |Mode|Cnt|     java 19|
|------------------------------------------------------------|----|---|------------|


### java 19

<!--- benchmark:data:jarfile:java 19: --->
```
Benchmark                                                       Mode  Cnt        Score   Error  Units
BigCompressedConcurrentThreadBenchmark.benchmark0File           avgt    2  2678276.855          ns/op
BigCompressedConcurrentThreadBenchmark.benchmark1Jar            avgt    2  8609564.705          ns/op
BigCompressedConcurrentThreadBenchmark.benchmark2ClassLoader    avgt    2  8892208.541          ns/op
BigCompressedConcurrentThreadBenchmark.benchmark3Class          avgt    2  8924515.866          ns/op
BigCompressedSingleThreadBenchmark.benchmark0File               avgt    2   545174.357          ns/op
BigCompressedSingleThreadBenchmark.benchmark1Jar                avgt    2  4371955.586          ns/op
```

### java 20 concurrent-jar

<!--- benchmark:data:jarfile:java 20 concurrent-jar: --->
```
Benchmark                                                       Mode  Cnt         Score   Error  Units
BigCompressedConcurrentThreadBenchmark.benchmark2ClassLoader    avgt    2   9362036.799          ns/op
BigCompressedConcurrentThreadBenchmark.benchmark3Class          avgt    2  10773556.495          ns/op
BigCompressedSingleThreadBenchmark.benchmark0File               avgt    2    543784.247          ns/op
BigCompressedSingleThreadBenchmark.benchmark1Jar                avgt    2   4281239.514          ns/op
BigCompressedSingleThreadBenchmark.benchmark2ClassLoader        avgt    2   4245898.114          ns/op
BigCompressedSingleThreadBenchmark.benchmark3Class              avgt    2   4191390.999          ns/op
```
