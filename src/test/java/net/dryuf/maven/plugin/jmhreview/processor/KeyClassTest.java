package net.dryuf.maven.plugin.jmhreview.processor;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import java.nio.file.Paths;


public class KeyClassTest
{
	private TestCommon testCommon = new TestCommon();

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
}
