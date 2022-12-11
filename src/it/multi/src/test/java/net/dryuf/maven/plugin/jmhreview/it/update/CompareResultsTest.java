package net.dryuf.maven.plugin.jmhreview.it.update;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;


public class CompareResultsTest
{
	@Test
	public void compareResults() throws IOException
	{
		Assert.assertTrue(
			FileUtils.contentEquals(new File("benchmarks.md"), new File("src/test/expected/benchmarks.md")),
			"updated benchmarks.md does not match src/test/expected/benchmarks.md");
	}
}
