package com.github.rmannibucau.maven.landslide;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.python.core.PyList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mojo(name = LandSlideMojo.NAME, defaultPhase = LifecyclePhase.PRE_SITE)
public class LandSlideMojo extends AbstractMojo {
    protected static final String NAME = "landslide";

    @Parameter(property = NAME + ".theme")
    private File theme;

    @Parameter(property = NAME + ".source", required = true)
    private File source;

    @Parameter(property = NAME + ".destination", defaultValue = "${project.build.directory}/" + NAME + "/presentation.html")
    private File destination;

    @Parameter
    private String extensions;

    @Parameter(property = NAME + ".embed", defaultValue = "false")
    private boolean embed;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        mkdirs(destination.getParentFile());
        runLandSlide(findThemeDirectory());
        getLog().info("Rendered " + destination.getPath());
    }

    private List<String> buildArgs(final File themeDir) {
        final List<String> args = new ArrayList<String>(Arrays.asList(source.getAbsolutePath(), "-d", destination.getAbsolutePath(), "-t", themeDir.getAbsolutePath()));
        if (extensions != null && !extensions.isEmpty()) {
            args.add("-x");
            args.add(extensions);
        }
        if (embed) {
            args.add("-i");
        }
        return args;
    }

    private void runLandSlide(final File themeDir) throws MojoFailureException {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName("python");
        try {
            final SimpleBindings bindings = new SimpleBindings() {{
                put("args", new PyList(buildArgs(themeDir)));
            }};

            engine.eval(new InputStreamReader(LandSlideMojo.class.getResourceAsStream("/runner.py")), bindings);

            if (bindings.containsKey("e")) { // there was an exception, just throw it to make the build fail
                throw new MojoFailureException(bindings.get("e").toString());
            }
        } catch (final ScriptException e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private File findThemeDirectory() throws LandSlidePluginException {
        final File themeDir;
        if (theme == null) {
            themeDir = extractDefaultThemes(new File("target/landslide-work/theme"));
        } else {
            themeDir = theme;
            if (!themeDir.exists()) {
                throw new LandSlidePluginException("Theme folder " + theme + " doesn't exist");
            }
        }
        return themeDir;
    }

    private static File extractDefaultThemes(final File base) {
        mkdirs(base);
        for (final String s : Arrays.asList("css/print.css", "css/screen.css", "js/slides.js", "base.html")) {
            final File copyTo = new File(base, s);
            mkdirs(copyTo.getParentFile());

            InputStream is = null;
            FileOutputStream out = null;
            try {
                is = LandSlideMojo.class.getResourceAsStream("/landslide/themes/default/" + s);
                out = new FileOutputStream(copyTo);
                IOUtils.copy(is, out);
            } catch (final IOException e) {
                // no-op
            } finally {
                IOUtils.closeQuietly(out);
                IOUtils.closeQuietly(is);
            }
        }

        return base;
    }

    private static void mkdirs(final File parentFile) {
        try {
            FileUtils.forceMkdir(parentFile);
        } catch (IOException e) {
            // no-op
        }
    }
}
