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

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
public class MarkdownTableWriter implements Closeable
{
	private final Writer writer;

	private Map<String, Integer> header;

	private List<List<String>> rows = new ArrayList<>();

	public void writeHeader(Map<String, Integer> header) throws IOException
	{
		this.header = header;
	}

	public void writeRow(List<String> values) throws IOException
	{
		rows.add(values);
	}

	@Override
	public void close() throws IOException
	{
		Map<String, Integer> lengths = new LinkedHashMap<>();
		Iterator<Map.Entry<String, Integer>> it = header.entrySet().iterator();
		for (int i = 0; it.hasNext(); ++i) {
			int i0 = i;
			Map.Entry<String, Integer> name = it.next();
			lengths.put(name.getKey(), rows.stream()
				.map(r -> r.get(i0)).mapToInt(s -> escapeTable(s).length())
				.reduce(Math.max(name.getKey().length(), name.getValue() == 0 ? 3 : 2), Math::max)
			);
		}
		for (Map.Entry<String, Integer> len: lengths.entrySet()) {
			writer.write("|");
			int align = header.get(len.getKey());
			writeAligned(align, len.getKey(), len.getValue(), ' ');
		}
		writer.write("|\n");
		for (Map.Entry<String, Integer> len: lengths.entrySet()) {
			writer.write("|");
			int align = header.get(len.getKey());
			if (align != 0) {
				writeAligned(align, ":", len.getValue(), '-');
			}
			else {
				writer.write(":" + Strings.repeat("-", Math.max(0, len.getValue()-2)) + ":");
			}
		}
		writer.write("|\n");
		for (List<String> row: rows) {
			Iterator<Integer> alignIt = header.values().iterator();
			Iterator<Integer> lengthIt = lengths.values().iterator();
			for (String value: row) {
				int align = alignIt.next();
				int length = lengthIt.next();
				String escaped = escapeTable(value);
				writer.write("|");
				if (align < 0) {
					writeLeftAligned(escaped, length, ' ');
				}
				else if (align > 0) {
					writeRightAligned(escaped, length, ' ');
				}
				else {
					writeCenterAligned(escaped, length, ' ');
				}
			}
			writer.write("|\n");
		}
	}

	private String escapeTable(String s)
	{
		return s.replace("|", "&#124;");
	}

	private void writeAligned(int align, String s, int length, char padding) throws IOException
	{
		if (align < 0) {
			writeLeftAligned(s, length, padding);
		}
		else if (align > 0) {
			writeRightAligned(s, length, padding);
		}
		else {
			writeCenterAligned(s, length, padding);
		}
	}

	private void writeLeftAligned(String s, int length, char padding) throws IOException
	{
		writer.write(s);
		writer.write(Strings.repeat(String.valueOf(padding), length - s.length()));
	}

	private void writeRightAligned(String s, int length, char padding) throws IOException
	{
		writer.write(Strings.repeat(String.valueOf(padding), length - s.length()));
		writer.write(s);
	}

	private void writeCenterAligned(String s, int length, char padding) throws IOException
	{
		writer.write(Strings.repeat(String.valueOf(padding), (length - s.length()) / 2));
		writer.write(s);
		writer.write(Strings.repeat(String.valueOf(padding), (length - s.length() + 1) / 2));
	}
}
