# key=class

<!--- benchmark:table:ClassData:key=class&multiply=0.001&order=JavaExecutorBenchmark&order=ClosingExecutorBenchmark&compare=JavaExecutorBenchmark: --->

|Benchmark |Mode|Units|JavaExecutorBenchmark|ClosingExecutorBenchmark|JavaExecutorBenchmark%|ClosingExecutorBenchmark%|
|:---------|:---|:----|--------------------:|-----------------------:|---------------------:|------------------------:|


# key=method-benchmark_run

<!--- benchmark:table:KeyData:key=method-benchmark_run&multiply=0.001&compare=one: --->

|Benchmark |Mode|Units|JavaExecutorBenchmark|ClosingExecutorBenchmark|JavaExecutorBenchmark%|ClosingExecutorBenchmark%|
|:---------|:---|:----|--------------------:|-----------------------:|---------------------:|------------------------:|


# Raw data

<!--- benchmark:data:ClassData:all:: --->

```
Benchmark                            Mode  Cnt       Score   Error  Units
ClosingExecutorBenchmark.b0_execute  avgt    2   78107.811          ns/op
ClosingExecutorBenchmark.b0_submit   avgt    2  128351.491          ns/op
JavaExecutorBenchmark.b0_execute     avgt    2   66697.839          ns/op
JavaExecutorBenchmark.b0_submit      avgt    2  117384.907          ns/op
```

<!--- benchmark:data:KeyData:all:: --->

```
Benchmark                            Mode  Cnt       Score   Error  Units
MyBenchmark.test_one                 avgt    2   78107.811          ns/op
MyBenchmark.test_two                 avgt    2   88107.811          ns/op
MyBenchmark.test_three               avgt    2   98107.811          ns/op
```
