# key=class

<!--- benchmark:table:ClassData:key=class&multiply=0.001&order=JavaExecutorBenchmark&order=ClosingExecutorBenchmark&compare=JavaExecutorBenchmark: --->

|Benchmark |Mode|Units|JavaExecutorBenchmark|ClosingExecutorBenchmark|JavaExecutorBenchmark%|ClosingExecutorBenchmark%|
|:---------|:---|:----|--------------------:|-----------------------:|---------------------:|------------------------:|
|b0_execute|avgt|ns/op|               66.698|                  78.108|                    +0|                      +17|
|b0_submit |avgt|ns/op|              117.385|                 128.351|                    +0|                       +9|


# key=method-benchmark_run

<!--- benchmark:table:KeyData:key=method-benchmark_run&multiply=0.001&compare=one: --->

|Benchmark|Mode|Units|   one|   two| three|one%|two%|three%|
|:--------|:---|:----|-----:|-----:|-----:|---:|---:|-----:|
|test     |avgt|ns/op|78.108|88.108|98.108|  +0| +12|   +25|


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
