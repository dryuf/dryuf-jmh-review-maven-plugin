<!--- ( vim: set tw=120: ) --->

### Comparison

<!--- benchmark:table:key-class:key=class: --->

|Benchmark|Mode|Units|     Class1|     Class2|
|:--------|:---|:----|----------:|----------:|
|execute  |avgt|ns/op|1678276.855|2678276.855|
|run      |avgt|ns/op|1609564.705|2609564.705|

### java 19

<!--- benchmark:data:key-class:all:: --->
```
Benchmark                                                       Mode  Cnt        Score   Error  Units
Class1.execute                                                  avgt    2  1678276.855          ns/op
Class1.run                                                      avgt    2  1609564.705          ns/op
Class2.execute                                                  avgt    2  2678276.855          ns/op
Class2.run                                                      avgt    2  2609564.705          ns/op
```

