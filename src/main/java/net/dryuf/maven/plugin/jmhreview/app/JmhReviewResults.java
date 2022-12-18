package net.dryuf.maven.plugin.jmhreview.app;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import net.dryuf.cmdline.app.AppContext;
import net.dryuf.cmdline.app.BeanFactory;
import net.dryuf.cmdline.app.CommonAppContext;
import net.dryuf.cmdline.app.guice.GuiceBeanFactoryModule;
import net.dryuf.cmdline.command.AbstractCommand;
import net.dryuf.cmdline.command.CommandContext;
import net.dryuf.cmdline.command.RootCommandContext;
import net.dryuf.maven.plugin.jmhreview.processor.Configuration;
import net.dryuf.maven.plugin.jmhreview.processor.Processor;

import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.ListIterator;
import java.util.Map;


public class JmhReviewResults extends AbstractCommand
{
	private Configuration options;

	public static void main(String[] args) throws Exception
	{
		AppContext appContext = new CommonAppContext(Guice.createInjector(new GuiceBeanFactoryModule()).getInstance(BeanFactory.class));
		System.exit(new JmhReviewResults().run(
			new RootCommandContext(appContext).createChild(null, "jmh-review-results", null),
			Arrays.asList(args)
		));
	}

	@Override
	public int execute() throws Exception
	{
		new Processor().execute(options);
		return 0;
	}
	@Override
	protected Map<String, String> configOptionsDescription(CommandContext context)
	{
		return ImmutableMap.of(
			"-e true/false", "fatal fail on errors (default true)",
			"-d dataset", "dataset to update",
			"-i input-file", "path to input file, can be multiple",
			"-o output-file", "path to update to results to",
			"-p", "output all datasets to stdout",
			"-c key=value", "configuration option for stdout mode"
		);
	}

	@Override
	protected boolean parseOption(CommandContext context, String arg, ListIterator<String> args) throws Exception
	{
		switch (arg) {
		case "-e":
			options.setErrorsFatal(Boolean.parseBoolean(needArgsParam(null, args)));
			return true;

		case "-d":
			options.getDatasets().add(needArgsParam(null, args));
			return true;

		case "-i":
			options.getInputFiles().add(Paths.get(needArgsParam(null, args)));
			return true;

		case "-o":
			options.setOutputFile(Paths.get(needArgsParam(options.getOutputFile(), args)));
			return true;

		case "-p":
			options.setOutputWriter(new OutputStreamWriter(System.out));
			return true;

		case "-c":
			String option[] = needArgsParam(null, args).split("=", 2);
			if (option.length != 2) {
				throw new IllegalArgumentException("-c requires key=value pair");
			}
			options.getOptions().add(new AbstractMap.SimpleImmutableEntry<>(option[0], option[1]));
			return true;

		default:
			return super.parseOption(context, arg, args);
		}
	}

	@Override
	protected int validateOptions(CommandContext context, ListIterator<String> args) throws Exception
	{
		if (options.getDatasets().isEmpty()) {
			return usage(context, "Option -d not specified");
		}
		if ((options.getOutputFile() == null) == (options.getOutputWriter() == null)) {
			return usage(context, "Option -o or -p must be specified");
		}
		if (options.getInputFiles().isEmpty()) {
			if (options.getOutputFile() == null) {
				return usage(context, "Option -i not specified");
			}
			options.setInputFiles(Collections.singletonList(options.getOutputFile()));
		}
		return EXIT_CONTINUE;
	}

	@Override
	protected void createOptions(CommandContext context)
	{
		this.options = new Configuration();
		this.options.setDatasets(new LinkedHashSet<>());
		this.options.setInputFiles(new ArrayList<>());
		this.options.setOptions(new ArrayList<>());
	}
}
