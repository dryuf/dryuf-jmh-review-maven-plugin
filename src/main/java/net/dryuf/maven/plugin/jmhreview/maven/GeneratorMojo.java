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

package net.dryuf.maven.plugin.jmhreview.maven;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import net.dryuf.maven.plugin.jmhreview.processor.Configuration;
import net.dryuf.maven.plugin.jmhreview.processor.Processor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;


/**
 * JMH benchmarks processor wrapper.
 */
@Mojo(name = "update-benchmarks", defaultPhase = LifecyclePhase.PACKAGE)
public class GeneratorMojo extends AbstractMojo
{
	@SneakyThrows
	@SuppressWarnings("unchecked")
	@Override
	public void			execute()
	{
		Processor generator = new Processor();
		Configuration configuration = Configuration.builder()
			.datasets(datasets)
			.errorsFatal(errorsFatal)
			.inputFiles(Optional.ofNullable(inputs).orElseGet(() -> Collections.singletonList(output)).stream()
				.map(File::toPath)
				.collect(ImmutableList.toImmutableList()))
			.outputFile(output.toPath())
			.build();
		try {
			generator.execute(configuration);
		}
		catch (IOException e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	/** Names of the benchmark datasets. */
	@Parameter(required = true)
	protected Set<String>		datasets;

	/** Indicates whether errors parsing the file are fatal. */
	@Parameter(required = false)
	protected boolean		errorsFatal = true;

	/** Input files, can possibly overlap with outputFile.  Default is outputFile. */
	@Parameter(required = false)
	protected List<File>		inputs;

	/** Output file. */
	@Parameter(required = true)
	protected File			output;
}
