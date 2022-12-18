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

package net.dryuf.maven.plugin.jmhreview.processor;

import com.google.common.collect.ImmutableMap;
import lombok.extern.log4j.Log4j2;
import net.dryuf.maven.plugin.jmhreview.processor.impl.InputProcessor;
import net.dryuf.maven.plugin.jmhreview.processor.impl.OutputProcessor;
import net.dryuf.maven.plugin.jmhreview.processor.model.BenchmarkSet;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * Main generator class.
 */
@Log4j2
public class Processor
{
	public void execute(Configuration configuration) throws IOException
	{
		InputProcessor inputProcessor = new InputProcessor(configuration);

		Map<String, Map<String, BenchmarkSet>> benchmarks = configuration.getInputFiles().stream()
			.map(inputProcessor::parseInput)
			.flatMap(m -> m.entrySet().stream())
			.collect(ImmutableMap.toImmutableMap(
				Map.Entry::getKey, Map.Entry::getValue,
				(a, b) -> ImmutableMap.<String, BenchmarkSet>builder()
					.putAll(a)
					.putAll(b)
					.build()
			));

		OutputProcessor outputProcessor = new OutputProcessor(configuration, benchmarks);
		if (configuration.getOutputFile() != null) {
			Set<String> processed = outputProcessor.replaceOutput(configuration.getOutputFile());
			if (!processed.containsAll(configuration.getDatasets())) {
				throw new IOException("Failed to process some of datasets: "+
					configuration.getDatasets().stream()
						.filter(s -> !processed.contains(s))
						.collect(Collectors.toList())
					);
			}
		}
		else {
			outputProcessor.replaceOutput(configuration.getOutputWriter());
		}
	}
}
