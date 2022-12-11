# dryuf-jmh-review-maven-plugin

Utility to process JMH benchmarks results and format them into single comparison table, driven by the same benchmark and
providing values for different testing equipment (hardware, language, etc).

The package provides both Maven plugin and standalone application.  They can update either existing markdown file or
print the results table into standard output.

## Example

### Results

|Benchmark                                                   |Mode|Units|    java 19| java 20 par|
|:-----------------------------------------------------------|:---|:----|----------:|-----------:|
|ConcurrentThreadBenchmark.benchmark2ClassLoader|avgt|ns/op|8892208.541| 9362036.799|
|ConcurrentThreadBenchmark.benchmark3Class      |avgt|ns/op|8924515.866|10773556.495|
|SingleThreadBenchmark.benchmark0File           |avgt|ns/op| 545174.357|  543784.247|
|SingleThreadBenchmark.benchmark1Jar            |avgt|ns/op|4371955.586| 4281239.514|

### Usage POM

Typical usage is as follows:

`pom.xml` example:
```
    <plugin>
        <groupId>net.dryuf.maven.plugin</groupId>
        <artifactId>dryuf-jmh-review-maven-plugin</artifactId>
        <version>${dryuf-jmh-review-maven-plugin.version}</version>
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

|Benchmark                                                   |Mode|Units|    java 19| java 20 par|
|:-----------------------------------------------------------|:---|:----|----------:|-----------:|
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

## Usage app

```
Usage: jmh-review-results options... 

Options:
-e true/false     fatal fail on errors (default true)
-d dataset        dataset to update
-i input-file     path to input file, can be multiple
-o output-file    path to update to results to
-p                output all datasets to stdout
```

```
./target/jmh-review-results -e false -d jarfile -i target/it/update/benchmarks.md -p
```

## License

The code is released under version 2.0 of the [Apache License][].

## Stay in Touch

Feel free to contact me at kvr000@gmail.com or http://github.com/kvr000 .

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
