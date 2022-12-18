<!--- ( vim: set tw=120: ) --->

### Comparison

<!--- benchmark:table:test:key=class&order=Class2&order=Class1: --->

|Benchmark|Mode|Units|     Class2|     Class1|     Class3|
|:--------|:---|:----|----------:|----------:|----------:|
|execute  |avgt|ns/op|2678276.855|1678276.855|3678276.855|
|run      |avgt|ns/op|2609564.705|1609564.705|3609564.705|

### all

<!--- benchmark:data:test:all:: --->
```
Benchmark                                                       Mode  Cnt        Score   Error  Units
Class1.execute                                                  avgt    2  1678276.855          ns/op
Class1.run                                                      avgt    2  1609564.705          ns/op
Class2.execute                                                  avgt    2  2678276.855          ns/op
Class2.run                                                      avgt    2  2609564.705          ns/op
Class3.execute                                                  avgt    2  3678276.855          ns/op
Class3.run                                                      avgt    2  3609564.705          ns/op
```
