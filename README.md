# dryuf-jmh-review-maven-plugin

Utility to process JMH benchmarks results and format them into single comparison table, driven by the same benchmark and
providing values for different testing equipment (hardware, language, etc).

The package provides both Maven plugin and standalone application.  They can update either existing markdown file or
print the results table into standard output.

## Example

### Results

|Benchmark                                      |Mode|Units|    java 19| java 20 par|
|:----------------------------------------------|:---|:----|----------:|-----------:|
|ConcurrentThreadBenchmark.benchmark2ClassLoader|avgt|ns/op|8892208.541| 9362036.799|
|ConcurrentThreadBenchmark.benchmark3Class      |avgt|ns/op|8924515.866|10773556.495|
|SingleThreadBenchmark.benchmark0File           |avgt|ns/op| 545174.357|  543784.247|
|SingleThreadBenchmark.benchmark1Jar            |avgt|ns/op|4371955.586| 4281239.514|

|Benchmark|Mode|Units|     Class1|     Class2|
|:--------|:---|:----|----------:|----------:|
|execute  |avgt|ns/op|1678276.855|2678276.855|
|run      |avgt|ns/op|1609564.705|2609564.705|

### Usage

Typical usage is as follows:

`pom.xml` example:
```
    <plugin>
        <groupId>net.dryuf.maven.plugin</groupId>
        <artifactId>dryuf-jmh-review-maven-plugin</artifactId>
        <version>1.2.0</version>
        <executions>
            <execution>
                <phase>test-compile</phase>
                <goals>
                    <goal>update-benchmarks</goal>
                </goals>
                <configuration>
                    <datasets>
                        <dataset>jarfile</dataset>
                    </datasets>
                    <output>benchmarks.md</output>
                </configuration>
            </execution>
        </executions>
    </plugin>
```

`benchmarks.md` example:

```
## Results
<!--- benchmark:table:jarfile:: --->

|Benchmark                                      |Mode|Units|    java 19| java 20 par|
|:----------------------------------------------|:---|:----|----------:|-----------:|
|ConcurrentThreadBenchmark.benchmark2ClassLoader|avgt|ns/op|8892208.541| 9362036.799|
|ConcurrentThreadBenchmark.benchmark3Class      |avgt|ns/op|8924515.866|10773556.495|
|SingleThreadBenchmark.benchmark0File           |avgt|ns/op| 545174.357|  543784.247|
|SingleThreadBenchmark.benchmark1Jar            |avgt|ns/op|4371955.586| 4281239.514|

## Runs

### java 19

<!--- benchmark:data:jarfile:java 19: --->
-```
Benchmark                                                       Mode  Cnt        Score   Error  Units
ConcurrentThreadBenchmark.benchmark2ClassLoader    avgt    2  8892208.541          ns/op
ConcurrentThreadBenchmark.benchmark3Class          avgt    2  8924515.866          ns/op
SingleThreadBenchmark.benchmark0File               avgt    2   545174.357          ns/op
-```

### java 20 concurrent-jar

<!--- benchmark:data:jarfile:java 20 par: --->
-```
Benchmark                                                       Mode  Cnt         Score   Error  Units
ConcurrentThreadBenchmark.benchmark3Class          avgt    2  10773556.495          ns/op
SingleThreadBenchmark.benchmark0File               avgt    2    543784.247          ns/op
SingleThreadBenchmark.benchmark1Jar                avgt    2   4281239.514          ns/op
-```
```

### Other options

Options are provided as the last section in table statement and are provided in a form of HTTP query string.  Some
options can be specified multiple times.

```
<!--- benchmark:table:test:key=class&multiply=1000&multiply=0.1: --->
```

#### Option key

`key=class` config parameter organizes benchmarks by class name instead of measure set.

`key=method-benchmark_run` config parameter organizes benchmarks by method, the method part before `_` being the type
and after `_` actual execution. 

```
<!--- benchmark:table:key-class:key=class: --->

|Benchmark|Mode|Units|     Class1|     Class2|
|:--------|:---|:----|----------:|----------:|
|execute  |avgt|ns/op|1678276.855|2678276.855|
|run      |avgt|ns/op|1609564.705|2609564.705|

<!--- benchmark:table:key-method:key=method-benchmark_run: --->

|Benchmark   |Mode|Units|    HashMap|LinkedHashMap|
|:-----------|:---|:----|----------:|------------:|
|remove      |avgt|ns/op|1678276.855|  2678276.855|

### java 19

<!--- benchmark:data:key-class:all:: --->
-```
Benchmark                                                       Mode  Cnt        Score   Error  Units
Class1.execute                                                  avgt    2  1678276.855          ns/op
Class1.run                                                      avgt    2  1609564.705          ns/op
Class2.execute                                                  avgt    2  2678276.855          ns/op
Class2.run                                                      avgt    2  2609564.705          ns/op
-```

<!--- benchmark:data:key-class:all:: --->
-```
Benchmark                                                       Mode  Cnt        Score   Error  Units
MapBench.remove_HashMap                                         avgt    2  1678276.855          ns/op
MapBench.remove_LinkedHashMap                                   avgt    2  2678276.855          ns/op
-```
```

#### Option multiply

`multiply=value` config parameter multiplies score by the value (float).  This is useful when benchmark run in batches.

```
<!--- makes the times divided by 1000: --->
<!--- benchmark:table:key-class:multiply=0.001: --->
```

#### Option order

`order=value` config parameter specifies order of benchmarks.  Not listed will be at the end, keeping original order.

```
<!--- makes the benchmarks appear in order of Class2, Class1 and any other not specified: --->
<!--- benchmark:table:key-class:key=class&order=Class2&order=Class1: --->
```

#### Option compare

`compare=value` specifies base benchmark to compare to.  It generates % comparison for each dataset.

```
<!--- benchmark:table:key-class:key=class&compare=Class1: --->

|Benchmark|Mode|Units|     Class1|     Class2|    Class3|Class1%|Class2%|Class3%|
|:--------|:---|:----|----------:|----------:|---------:|------:|------:|------:|
|execute  |avgt|ns/op|1678276.855|2678276.855|678276.855|     +0|    +59|    -59|
|run      |avgt|ns/op|1609564.705|2609564.705|609564.705|     +0|    +62|    -62|
```

#### Option filter

`filter=value` specifies regexp filter to include subset of benchmarks

```
<!--- benchmark:table:filtering:filter=Class1&key=class: --->

|Benchmark|Mode|Units|     Class1|
|:--------|:---|:----|----------:|
|execute  |avgt|ns/op|1678276.855|
|run      |avgt|ns/op|1609564.705|
```

## Usage app

```
Usage: jmh-review-results options... 

Options:
-e true/false     fatal fail on errors (default true)
-d dataset        dataset to update
-i input-file     path to input file, can be multiple
-o output-file    path to update to results to
-p                output all datasets to stdout
-c key=value      configuration option for stdout mode
```

```
./target/jmh-review-results -e false -d jarfile -i src/test/data/compare/benchmarks.md -p -c key=class -c compare=Class1
```

## License

The code is released under version 2.0 of the [Apache License][].

## Stay in Touch

Feel free to contact me at kvr000@gmail.com or http://github.com/kvr000 .

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
