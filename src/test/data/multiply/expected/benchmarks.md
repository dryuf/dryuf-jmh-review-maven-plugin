<!--- ( vim: set tw=120: ) --->

### Comparison

<!--- benchmark:table:test:key=class&multiply=0.001: --->

|Benchmark|Mode|Units|  Class1|  Class2|
|:--------|:---|:----|-------:|-------:|
|execute  |avgt|ns/op|1678.277|2678.277|
|run      |avgt|ns/op|1609.565|2609.565|

### java 19

<!--- benchmark:data:test:all:: --->
```
Benchmark                                                       Mode  Cnt        Score   Error  Units
Class1.execute                                                  avgt    2  1678276.855          ns/op
Class1.run                                                      avgt    2  1609564.705          ns/op
Class2.execute                                                  avgt    2  2678276.855          ns/op
Class2.run                                                      avgt    2  2609564.705          ns/op
```
