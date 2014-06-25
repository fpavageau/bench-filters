Applying multiple filters with Guava
======

This is a micro-benchmark using [JMH](http://openjdk.java.net/projects/code-tools/jmh/) to compare the time it takes to apply 2 consecutive filters to a collection, using either `Predicates.and()` in a single `filter()` call or several `filter()` calls.

## What does it do?

The baseline just returns the first element of the collection using `FluentIterable.first()`. There are then 4 different configurations of filters tested, using either method:

  1. Both filters match in all cases
  1. The first filter only matches some of the elements
  1. The last filter only matches some of the elements
  1. The last filter only matches some of the elements, and a third (always true) filter is inserted between the existing 2, to illustrate the impact of a megamorphic method call (at least 3 implementations known by the JVM)

## How to compile

The project uses Maven 2+, so just do: 

        mvn clean package

## How to run

        java -jar target/benchmarks.jar

## Results

On my i7-2600 CPU @ 3.40GHz running Ubuntu Saucy and a 64-bit Oracle JDK (1.7u40), I get the following results (reordered to match the description of the configurations):

<pre>
Benchmark                                                    Mode   Samples        Score  Score error    Units
c.e.j.FilteringBenchmark.baseline                            avgt         9       15.405        0.481    ns/op
c.e.j.FilteringBenchmark.andedFiltersAlwaysMatch             avgt         9       45.335        1.078    ns/op
c.e.j.FilteringBenchmark.chainedFiltersAlwaysMatch           avgt         9       55.216        2.071    ns/op
c.e.j.FilteringBenchmark.andedFiltersPartialMatchFirst       avgt         9       77.658        1.827    ns/op
c.e.j.FilteringBenchmark.chainedFiltersPartialMatchFirst     avgt         9       74.538        4.436    ns/op
c.e.j.FilteringBenchmark.andedFiltersPartialMatchLast        avgt         9       79.416        2.858    ns/op
c.e.j.FilteringBenchmark.chainedFiltersPartialMatchLast      avgt         9       85.599        4.519    ns/op
c.e.j.FilteringBenchmark.anded3FiltersPartialMatchLast       avgt         9      152.213       10.560    ns/op
c.e.j.FilteringBenchmark.chained3FiltersPartialMatchLast     avgt         9      127.748        8.172    ns/op
</pre>

As expected, the 2 methods are mostly equivalent, though the megamorphic method call does exhibit a significant difference between them.

------

Licensed under the Apache License, Version 2.0 
