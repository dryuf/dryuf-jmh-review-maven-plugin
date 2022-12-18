<!--- ( vim: set tw=120: ) --->

### Comparison

<!--- benchmark:table:test:key=class&compare=Class1: --->

|Benchmark|Mode|Units|     Class1|     Class2|     Class3|Class1%|Class2%|Class3%|
|:--------|:---|:----|----------:|----------:|----------:|------:|------:|------:|
|execute  |avgt|ns/op|1678276.855|2678276.855|0678276.855|     +0|    +59|    -59|
|run      |avgt|ns/op|1609564.705|2609564.705|0609564.705|     +0|    +62|    -62|

### all

<!--- benchmark:data:test:all:: --->
```
Benchmark                                                       Mode  Cnt        Score   Error  Units
Class1.execute                                                  avgt    2  1678276.855          ns/op
Class1.run                                                      avgt    2  1609564.705          ns/op
Class2.execute                                                  avgt    2  2678276.855          ns/op
Class2.run                                                      avgt    2  2609564.705          ns/op
Class3.execute                                                  avgt    2  0678276.855          ns/op
Class3.run                                                      avgt    2  0609564.705          ns/op
```
