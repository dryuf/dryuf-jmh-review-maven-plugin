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
import lombok.extern.log4j.Log4j2;
import net.dryuf.maven.plugin.jmhreview.processor.Configuration;
import net.dryuf.maven.plugin.jmhreview.processor.io.MarkdownTableWriter;
import net.dryuf.maven.plugin.jmhreview.processor.io.TextFileUpdater;
import net.dryuf.maven.plugin.jmhreview.processor.model.BenchmarkSet;
import net.dryuf.maven.plugin.jmhreview.processor.model.FunctionBenchmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Log4j2
public class OutputProcessor
{
	private static final Pattern TABLE_PATTERN = Pattern.compile("^\\W+benchmark:table:([^:]+):(.*)$");

	private final Configuration configuration;

	final Map<String, Map<String, BenchmarkSet>> benchmarks;

	public OutputProcessor(Configuration configuration, Map<String, Map<String, BenchmarkSet>> benchmarks)
	{
		this.configuration = configuration;
		this.benchmarks = benchmarks;
	}

	public void replaceOutput(Path outputFile) throws IOException
	{
		try (TextFileUpdater updater = new TextFileUpdater(outputFile)) {
			for (;;) {
				String line = updater.readLine();
				if (line == null)
					break;
				Matcher m = TABLE_PATTERN.matcher(line);
				if (m.matches() && configuration.getDatasets().contains(m.group(1))) {
					updater.println(line);
					processTable(updater, m.group(1));
				}
				else {
					updater.println(line);
				}
			}
			updater.update();
		}
	}

	public void replaceOutput(Writer writer) throws IOException
	{
		PipedWriter pipeWriter = new PipedWriter();
		try (TextFileUpdater updater = new TextFileUpdater(new BufferedReader(new PipedReader(pipeWriter)), writer)) {
			for (String dataset: configuration.getDatasets()) {
				pipeWriter.write("|\n\n");
				updater.write("<!--- benchmark:table:"+dataset+":: --->\n\n");
				processTable(updater, dataset);
				updater.println("");
			}
			updater.update();
		}
	}

	private void processTable(TextFileUpdater updater, String dataset) throws IOException
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

		Map<String, BenchmarkSet> set = Optional.ofNullable(benchmarks.get(dataset))
			.orElseGet(Collections::emptyMap);
		LinkedHashSet<String> functions = new LinkedHashSet<>();

		for (Map.Entry<String, BenchmarkSet> entry: set.entrySet()) {
			for (Map.Entry<String, FunctionBenchmark> functionEntry: entry.getValue().getResults().entrySet()) {
				functions.add(functionEntry.getKey());
			}
		}

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
					.map(bs -> bs.getResults().get(function))
					.filter(Objects::nonNull)
					.map(FunctionBenchmark::getMode),
					() -> new IOException("Multiple modes for benchmark: benchmark=" + function));
				String units = getSingleValue(set.values().stream()
						.map(bs -> bs.getResults().get(function))
						.filter(Objects::nonNull)
						.map(FunctionBenchmark::getUnits),
					() -> new IOException("Multiple modes for benchmark: benchmark=" + function));
				writer.writeRow(ImmutableList.<String>builder()
					.add(function)
					.add(mode)
					.add(units)
					.addAll(set.values().stream()
						.map(bs -> Optional.ofNullable(bs.getResults().get(function))
							.map(FunctionBenchmark::getScore)
							.orElse("")
						)
						.iterator())
					.build()
				);
			}
		}
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
