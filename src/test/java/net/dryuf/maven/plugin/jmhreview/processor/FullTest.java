package net.dryuf.maven.plugin.jmhreview.processor;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import java.nio.file.Paths;


public class FullTest
{
	private final TestCommon testCommon = new TestCommon();

	@Test
	public void testKeyClass() throws Exception
	{
		testCommon.fullRun(
			Configuration.builder()
				.datasets(ImmutableSet.of("key-class"))
				.build(),
			null,
			Paths.get("key-class", "benchmarks.md")
		);
	}

	@Test
	public void testMultiply() throws Exception
	{
		testCommon.fullRun(
			Configuration.builder()
				.datasets(ImmutableSet.of("test"))
				.build(),
			null,
			Paths.get("multiply", "benchmarks.md")
		);
	}

	@Test
	public void testOrder() throws Exception
	{
		testCommon.fullRun(
			Configuration.builder()
				.datasets(ImmutableSet.of("test"))
				.build(),
			null,
			Paths.get("order", "benchmarks.md")
		);
	}

	@Test
	public void testCompare() throws Exception
	{
		testCommon.fullRun(
			Configuration.builder()
				.datasets(ImmutableSet.of("test"))
				.build(),
			null,
			Paths.get("compare", "benchmarks.md")
		);
	}
}
