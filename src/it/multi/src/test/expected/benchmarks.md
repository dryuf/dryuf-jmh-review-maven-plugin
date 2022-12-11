[//]: # ( vim: set tw=120: )

### Comparison

[//]: # benchmark:table:jarfile:java 20 concurrent-jar:

|Benchmark                                                   |Mode|Units|    java 19|java 20 concurrent-jar|
|:-----------------------------------------------------------|:---|:----|----------:|---------------------:|
|BigCompressedConcurrentThreadBenchmark.benchmark0File       |avgt|ns/op|2678276.855|                      |
|BigCompressedConcurrentThreadBenchmark.benchmark1Jar        |avgt|ns/op|8609564.705|                      |
|BigCompressedConcurrentThreadBenchmark.benchmark2ClassLoader|avgt|ns/op|8892208.541|           9362036.799|
|BigCompressedConcurrentThreadBenchmark.benchmark3Class      |avgt|ns/op|8924515.866|          10773556.495|
|BigCompressedSingleThreadBenchmark.benchmark0File           |avgt|ns/op| 545174.357|            543784.247|
|BigCompressedSingleThreadBenchmark.benchmark1Jar            |avgt|ns/op|4371955.586|           4281239.514|
|BigCompressedSingleThreadBenchmark.benchmark2ClassLoader    |avgt|ns/op|           |           4245898.114|
|BigCompressedSingleThreadBenchmark.benchmark3Class          |avgt|ns/op|           |           4191390.999|
