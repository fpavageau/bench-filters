/*
 * Copyright 2014 Frank Pavageau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ekino.jmh;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(3)
@Warmup(iterations = 3)
@Measurement(iterations = 3)
@State(Scope.Benchmark)
public class FilteringBenchmark {
    private final List<String> toFilter = ImmutableList.of("A", "BB", "CCC");
    private final Predicate<String> notEmptyPredicate = new Predicate<String>() {
        @Override
        public boolean apply(String s) {
            return s != null && !s.isEmpty();
        }
    };
    private final Predicate<String> inPredicate = Predicates.in(ImmutableSet.of("BB", "CCC"));

    @Benchmark
    public Object baseline() {
        return FluentIterable.from(toFilter)
                .first();
    }

    @Benchmark
    public Object chainedFiltersAlwaysMatch() {
        return FluentIterable.from(toFilter)
                .filter(notEmptyPredicate)
                .filter(notEmptyPredicate)
                .first();
    }

    @Benchmark
    public Object andedFiltersAlwaysMatch() {
        return FluentIterable.from(toFilter)
                .filter(Predicates.and(notEmptyPredicate, notEmptyPredicate))
                .first();
    }

    @Benchmark
    public Object chainedFiltersPartialMatchFirst() {
        return FluentIterable.from(toFilter)
                .filter(inPredicate)
                .filter(notEmptyPredicate)
                .first();
    }

    @Benchmark
    public Object andedFiltersPartialMatchFirst() {
        return FluentIterable.from(toFilter)
                .filter(Predicates.and(inPredicate, notEmptyPredicate))
                .first();
    }

    @Benchmark
    public Object chainedFiltersPartialMatchLast() {
        return FluentIterable.from(toFilter)
                .filter(notEmptyPredicate)
                .filter(inPredicate)
                .first();
    }

    @Benchmark
    public Object andedFiltersPartialMatchLast() {
        return FluentIterable.from(toFilter)
                .filter(Predicates.and(notEmptyPredicate, inPredicate))
                .first();
    }

    @Benchmark
    public Object chained3FiltersPartialMatchLast() {
        return FluentIterable.from(toFilter)
                .filter(notEmptyPredicate)
                .filter(Predicates.alwaysTrue())
                .filter(inPredicate)
                .first();
    }

    @Benchmark
    public Object anded3FiltersPartialMatchLast() {
        return FluentIterable.from(toFilter)
                .filter(Predicates.and(notEmptyPredicate, Predicates.alwaysTrue(), inPredicate))
                .first();
    }
}
