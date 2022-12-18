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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.Writer;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;


/**
 * Plugin configuration.
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Configuration
{
	/** Names of the data sets. */
	@Parameter(required = true)
	protected Set<String> 		datasets;

	/** Indicates whether errors parsing the file are fatal. */
	@lombok.Builder.Default
	@Parameter(required = false)
	protected boolean		errorsFatal = true;

	/** Input files, can possibly overlap with outputFile.  Default is outputFile. */
	@Parameter(required = true)
	protected List<Path>		inputFiles;

	/** Output file. */
	@Parameter(required = true)
	protected Path			outputFile;

	/** Output writer, in case stream is required. */
	protected Writer		outputWriter;
}
