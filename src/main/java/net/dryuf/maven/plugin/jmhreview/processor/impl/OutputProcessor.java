/*
 * Copyright 2022 Zbynek Vyskovsky mailto:kvr000@gmail.com https://github.com/kvr000/ https://www.linkedin.com/in/zbynek-vyskovsky/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dryuf.maven.plugin.jmhreview.processor.impl;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lombok.extern.log4j.Log4j2;
import net.dryuf.maven.plugin.jmhreview.processor.Configuration;
import net.dryuf.maven.plugin.jmhreview.processor.io.MarkdownTableWriter;
import net.dryuf.maven.plugin.jmhreview.processor.io.TextFileUpdater;
import net.dryuf.maven.plugin.jmhreview.processor.model.BenchmarkSet;
import net.dryuf.maven.plugin.jmhreview.processor.model.FunctionBenchmark;
import net.dryuf.maven.plugin.jmhreview.processor.util.ExtCollectors;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Log4j2
public class OutputProcessor
{
	private static final Pattern TABLE_PATTERN = Pattern.compile("^\\W+benchmark:table:([^:]+):([^:]*):.*$");

	private final Configuration configuration;

	final Map<String, Map<String, BenchmarkSet>> benchmarks;

	public OutputProcessor(Configuration configuration, Map<String, Map<String, BenchmarkSet>> benchmarks)
	{
		this.configuration = configuration;
		this.benchmarks = benchmarks;
	}

	public Set<String> replaceOutput(Path outputFile) throws IOException
	{
		LinkedHashSet<String> processed = new LinkedHashSet<>();
		try (TextFileUpdater updater = new TextFileUpdater(outputFile)) {
			for (;;) {
				String line = updater.readLine();
				if (line == null)
					break;
				Matcher m = TABLE_PATTERN.matcher(line);
				if (m.matches() && configuration.getDatasets().contains(m.group(1))) {
					processed.add(m.group(1));
					updater.println(line);
					processTable(updater, m.group(1), parseConfig(m.group(2)));
				}
				else {
					updater.println(line);
				}
			}
			updater.update();
		}
		return processed;
	}

	public void replaceOutput(Writer writer) throws IOException
	{
		PipedWriter pipeWriter = new PipedWriter();
		try (TextFileUpdater updater = new TextFileUpdater(new BufferedReader(new PipedReader(pipeWriter)), writer)) {
			for (String dataset: configuration.getDatasets()) {
				pipeWriter.write("|\n\n");
				updater.write("<!--- benchmark:table:"+dataset+":: --->\n\n");
				processTable(updater, dataset, Collections.emptyMap());
				updater.println("");
			}
			updater.update();
		}
	}

	private Map<String, List<String>> parseConfig(String config)
	{
		return URLEncodedUtils.parse(config, StandardCharsets.UTF_8).stream()
			.collect(Collectors.groupingBy(
				NameValuePair::getName,
				LinkedHashMap::new,
				Collectors.mapping(NameValuePair::getValue, Collectors.toList())
			));
	}

	private void processTable(TextFileUpdater updater, String dataset, Map<String, List<String>> config) throws IOException
	{
		String line = updater.skipBlank();
		if (line == null)
			throw new IllegalArgumentException("Failed to find table start");
		if (!line.startsWith("|"))
			throw new IllegalArgumentException("Failed to find table start");
		for (;;) {
			line = updater.readLine();
			if (line == null || !line.startsWith("|")) {
				if (line != null) {
					updater.undoLine(line);
				}
				break;
			}
		}

		Map<String, Map<String, FunctionBenchmark>> set = prepareData(dataset, config);

		Set<String> functions = set.values().stream()
			.flatMap(v -> v.keySet().stream())
			.collect(ImmutableSet.toImmutableSet());

		try (MarkdownTableWriter writer = new MarkdownTableWriter(updater.getWriter())) {
			writer.writeHeader(ImmutableMap.<String, Integer>builder()
				.put("Benchmark", -1)
				.put("Mode", -1)
				.put("Units", -1)
				.putAll(set.keySet().stream().map(e -> new AbstractMap.SimpleImmutableEntry<>(e, 1))
					.collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue)))
				.build()
			);
			for (String function: functions) {
				String mode = getSingleValue(set.values().stream()
					.map(fbs -> fbs.get(function))
					.filter(Objects::nonNull)
					.map(FunctionBenchmark::getMode),
					() -> new IOException("Multiple modes for benchmark: benchmark=" + function));
				String units = getSingleValue(set.values().stream()
						.map(fbs -> fbs.get(function))
						.filter(Objects::nonNull)
						.map(FunctionBenchmark::getUnits),
					() -> new IOException("Multiple modes for benchmark: benchmark=" + function));
				writer.writeRow(ImmutableList.<String>builder()
					.add(function)
					.add(mode)
					.add(units)
					.addAll(set.values().stream()
						.map(fbs -> Optional.ofNullable(fbs.get(function))
							.map(FunctionBenchmark::getScore)
							.orElse("")
						)
						.iterator())
					.build()
				);
			}
		}
	}

	private Map<String, Map<String, FunctionBenchmark>> prepareData(String dataset, Map<String, List<String>> config)
	{
		Map<String, BenchmarkSet> sources = Optional.ofNullable(benchmarks.get(dataset))
			.orElseGet(Collections::emptyMap);
		LinkedHashSet<String> functions = new LinkedHashSet<>();

		BiFunction<BenchmarkSet, FunctionBenchmark, String> keyFunction;
		BiFunction<BenchmarkSet, FunctionBenchmark, String> nameFunction;
		Function<FunctionBenchmark, FunctionBenchmark> modifyFunction = Function.identity();
		{
			String key = Optional.ofNullable(config.get("key")).map(l -> l.get(0)).orElse(null);
			if (key == null) {
				keyFunction = (bs, fb) -> bs.getMeasure();
				nameFunction = (bs, fb) -> fb.getName();
			}
			else if (key.equals("class")) {
				keyFunction = (bs, fb) -> fb.getName().split("\\.")[0];
				nameFunction = (bs, fb) -> fb.getName().split("\\.")[1];
			}
			else {
				throw new UnsupportedOperationException("Unknown config value for key (only empty and class are supported): key="+key);
			}
		}
		modifyFunction = config.getOrDefault("multiply", Collections.emptyList()).stream()
			.reduce(
				modifyFunction,
				(old, value) -> {
					double valueD = Double.parseDouble(value);
					return (fb) -> {
						FunctionBenchmark previous = old.apply(fb);
						return previous.toBuilder()
							.score(String.format("%.3f", Double.parseDouble(previous.getScore())*valueD))
							.build();
					};
				},
				(a, b) -> { throw new IllegalStateException(); }
			);
		MutableInt orderCounter = new MutableInt();
		Map<String, Integer> order = config.getOrDefault("order", Collections.emptyList()).stream()
			.collect(Collectors.toMap(Function.identity(), v -> orderCounter.getAndAdd(1)));
		return sources.values().stream()
			.flatMap(bs -> bs.getResults().values().stream().map(fb -> Pair.of(bs, fb)))
			.collect(ExtCollectors.toStableSorted(Comparator.comparingInt(p ->
				order.getOrDefault(keyFunction.apply(p.getLeft(), p.getRight()), Integer.MAX_VALUE))))
			.collect(Collectors.groupingBy(
				(Pair<BenchmarkSet, FunctionBenchmark> p) -> keyFunction.apply(p.getLeft(),
					p.getRight()),
				LinkedHashMap::new,
				Collectors.mapping(
					p -> p.getRight().toBuilder()
						.name(nameFunction.apply(p.getLeft(), p.getRight()))
						.build(),
					Collectors.toMap(
						FunctionBenchmark::getName,
						modifyFunction,
						(a, b) -> {
							throw new IllegalStateException("Merging same elements: "+a+" "
								+b);
						},
						LinkedHashMap::new
					)
				)
			));
	}

	private <V, X extends Throwable> V getSingleValue(Stream<V> values, Supplier<X> exception) throws X
	{
		Set<V> reduced = values.collect(Collectors.toSet());
		Iterator<V> it = reduced.iterator();
		V v = it.next();
		if (it.hasNext())
			throw exception.get();
		return v;
	}
}
