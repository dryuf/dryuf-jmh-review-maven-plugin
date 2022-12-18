package net.dryuf.maven.plugin.jmhreview.processor;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import java.nio.file.Paths;


public class MultiplyTest
{
	private TestCommon testCommon = new TestCommon();

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
}
