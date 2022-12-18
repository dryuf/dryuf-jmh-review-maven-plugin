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


import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;
import net.dryuf.maven.plugin.jmhreview.processor.Configuration;
import net.dryuf.maven.plugin.jmhreview.processor.model.BenchmarkSet;
import net.dryuf.maven.plugin.jmhreview.processor.model.FunctionBenchmark;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Log4j2
public class InputProcessor
{
	private static final Pattern DATA_PATTERN = Pattern.compile("^\\W+benchmark:data:([^:]+):([^:]+):(.*)$");

	private static final Pattern EMPTY_OR_CODE_PATTERN = Pattern.compile("^(|```)\\s*$");

	private static final Pattern WORD_WITH_SPACES_PATTERN = Pattern.compile("(\\w+)(\\s+|$)\\b");

	private final Configuration configuration;

	public InputProcessor(Configuration configuration)
	{
		this.configuration = configuration;
	}

	public Map<String, Map<String, BenchmarkSet>> parseInput(Path inputFile)
	{
		LinkedHashMap<String, Map<String, BenchmarkSet>> benchmarks = new LinkedHashMap<>();
		try (BufferedReader reader = Files.newBufferedReader(inputFile)) {
			for (;;) {
				String line = reader.readLine();
				if (line == null)
					break;
				Matcher dataMatcher = DATA_PATTERN.matcher(line);
				if (dataMatcher.matches()) {
					String dataset = dataMatcher.group(1);
					String measure = dataMatcher.group(2);
					@SuppressWarnings("unused")
					String config = dataMatcher.group(3);
					try {
						BenchmarkSet set = BenchmarkSet.builder()
							.dataSet(dataset)
							.measure(measure)
							.results(parseBenchmarkRows(reader))
							.build();
						benchmarks.computeIfAbsent(dataset, (key) -> new LinkedHashMap<>())
							.compute(measure, (key, old) -> {
								if (old != null) {
									throw new IllegalArgumentException("Benchmark set specified twice: set=" + key);
								}
								return set;
							});
					}
					catch (IllegalArgumentException ex) {
						log.error("Error processing benchmark set: file={} measure={}", inputFile, measure);
						if (configuration.isErrorsFatal()) {
							throw new IOException(String.format("Error processing benchmark set: file=%s measure=%s", inputFile, measure), ex);
						}
					}
				}
			}
		}
		catch (IOException ex) {
			throw new UncheckedIOException("Failed to parse file: file=" + inputFile, ex);
		}
		return benchmarks;
	}

	private Map<String, FunctionBenchmark> parseBenchmarkRows(BufferedReader reader) throws IOException
	{
		Map<String, Pair<Integer, Integer>> headers = parseBenchmarkHeader(reader);
		String line = reader.readLine();
		if (Strings.isNullOrEmpty(line) || !Character.isAlphabetic(line.codePointAt(0))) {
			Collections.emptyMap();
		}
		ImmutableMap.Builder<String, FunctionBenchmark> benchmarks = ImmutableMap.builder();
		do {
			final String line0 = line;
			FunctionBenchmark.Builder benchmark = FunctionBenchmark.builder();
			Optional.ofNullable(headers.get("Benchmark"))
				.ifPresentOrElse(
					p -> benchmark.name(extractValue(p, line0)),
					() -> { throw new IllegalArgumentException("Missing Benchmark in header"); }
				);
			Optional.ofNullable(headers.get("Mode"))
				.ifPresentOrElse(
					p -> benchmark.mode(extractValue(p, line0)),
					() -> { throw new IllegalArgumentException("Missing Score in header"); }
				);
			Optional.ofNullable(headers.get("Score"))
				.ifPresentOrElse(
					p -> benchmark.score(extractValue(p, line0)),
					() -> { throw new IllegalArgumentException("Missing Score in header"); }
				);
			Optional.ofNullable(headers.get("Units"))
				.ifPresentOrElse(
					p -> benchmark.units(extractValue(p, line0)),
					() -> { throw new IllegalArgumentException("Missing Units in header"); }
				);
			FunctionBenchmark benchmarkb = benchmark.build();
			benchmarks.put(benchmarkb.getName(), benchmarkb);
		} while (!Strings.isNullOrEmpty(line = reader.readLine()) && Character.isAlphabetic(line.codePointAt(0)));
		return benchmarks.build();
	}

	private Map<String, Pair<Integer, Integer>> parseBenchmarkHeader(BufferedReader reader) throws IOException
	{
		ImmutableMap.Builder<String, Pair<Integer, Integer>> headers = ImmutableMap.builder();

		String header = reader.readLine();
		for (;;) {
			if (header == null)
				throw new IllegalArgumentException("Hit end of file when searching for header");
			Matcher m = EMPTY_OR_CODE_PATTERN.matcher(header);
			if (!m.matches())
				break;
			header = reader.readLine();
		}
		Matcher m = WORD_WITH_SPACES_PATTERN.matcher(header);
		if (!m.find(0)) {
			throw new IllegalArgumentException("Unable to parse header: " + header);
		}
		do {
			headers.put(m.group(1), Pair.of(m.start(1), m.end(1)));
		} while (m.find());
		return headers.build();
	}

	private String extractValue(Pair<Integer, Integer> positions, String line)
	{
		int begin = positions.getLeft(), end = positions.getRight();
		if (Character.isWhitespace(line.charAt(begin))) {
			for (; begin < end && Character.isWhitespace(line.charAt(begin)); ++begin) ;
		}
		else {
			for (; begin > 0 && !Character.isWhitespace(line.charAt(begin-1)); --begin) ;
		}
		if (Character.isWhitespace(line.charAt(end-1))) {
			for (; end > begin && Character.isWhitespace(line.charAt(end-1)); --end) ;
		}
		else {
			for (; end < line.length() && !Character.isWhitespace(line.charAt(end)); ++end) ;
		}
		return line.substring(begin, end);
	}
}
