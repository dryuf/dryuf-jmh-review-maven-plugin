package net.dryuf.maven.plugin.jmhreview.processor;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class TestCommon
{
	private static final Path TEST_DATA_ROOT = Paths.get("src", "test", "data");

	private static final Path TEST_RUN_ROOT = Paths.get("target", "test");

	private final Processor processor = new Processor();

	public void fullRun(Configuration configuration, List<Path> inputs, Path output) throws Exception
	{
		Path outputRun = TEST_RUN_ROOT.resolve(output);

		Files.createDirectories(outputRun.getParent());

		Files.copy(TEST_DATA_ROOT.resolve(output), outputRun, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

		configuration.setOutputFile(outputRun);
		configuration.setInputFiles(Objects.requireNonNullElseGet(inputs, () -> Collections.singletonList(output)).stream()
				.map(TEST_DATA_ROOT::resolve)
				.collect(Collectors.toList()));

		processor.execute(configuration);

		Path outputExpected = TEST_DATA_ROOT.resolve(output).getParent().resolve("expected").resolve(output.getFileName());
		Assert.assertTrue(
			FileUtils.contentEquals(outputRun.toFile(), outputExpected.toFile()),
			"Files differ: " + outputRun + " " + outputExpected);
	}
}
