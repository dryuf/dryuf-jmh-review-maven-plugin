[//]: # ( vim: set tw=120: )

### Comparison

[//]: # benchmark:table:jarfile:java 20 concurrent-jar:

|Benchmark                                                   |Mode|Cnt|     java 19|
|------------------------------------------------------------|----|---|------------|
|BigCompressedConcurrentThreadBenchmark.benchmark2ClassLoader|avgt|  2| 9362036.799|
|BigCompressedConcurrentThreadBenchmark.benchmark3Class      |avgt|  2|10773556.495|
|BigCompressedSingleThreadBenchmark.benchmark0File           |avgt|  2|  543784.247|
|BigCompressedSingleThreadBenchmark.benchmark1Jar            |avgt|  2| 4281239.514|
|BigCompressedSingleThreadBenchmark.benchmark2ClassLoader    |avgt|  2| 4245898.114|
|BigCompressedSingleThreadBenchmark.benchmark3Class          |avgt|  2| 4191390.999|
