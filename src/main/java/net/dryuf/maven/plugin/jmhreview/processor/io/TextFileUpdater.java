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

package net.dryuf.maven.plugin.jmhreview.processor.io;

import com.google.common.io.Closeables;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;


public class TextFileUpdater implements Closeable
{
	private final Path file;

	private final BufferedReader reader;

	private final Writer writer;

	private final StringWriter output = new StringWriter();

	private String lastLine;

	public TextFileUpdater(Path file)
	{
		this.file = file;
		try {
			this.reader = Files.newBufferedReader(file);
			this.writer = null;
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public TextFileUpdater(BufferedReader reader, Writer writer)
	{
		this.file = null;
		this.reader = reader;
		this.writer = writer;
	}

	public String readLine()
	{
		if (lastLine != null) {
			try {
				return lastLine;
			}
			finally {
				lastLine = null;
			}
		}
		try {
			return reader.readLine();
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public String skipBlank()
	{
		for (;;) {
			String line = readLine();
			if (line == null) {
				return null;
			}
			if (!line.isBlank()) {
				return line;
			}
			println(line);
		}
	}

	public void undoLine(String line)
	{
		if (lastLine != null) {
			throw new UncheckedIOException(new IOException("Double undoLine unsupported"));
		}
		lastLine = line;
	}

	public Writer getWriter()
	{
		return new FilterWriter(output) {
			@Override
			public void close() throws IOException
			{
			}
		};
	}

	public void write(String s)
	{
		output.write(s);
	}

	public void println(String line)
	{
		write(line);
		write("\n");
	}

	public void update()
	{
		try {
			if (file != null) {
				this.reader.close();
				Files.writeString(this.file, this.output.toString());
			}
			else {
				this.reader.close();
				this.writer.write(this.output.toString());
				this.writer.flush();
			}
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public void close() throws IOException
	{
		Closeables.closeQuietly(reader);
	}
}
