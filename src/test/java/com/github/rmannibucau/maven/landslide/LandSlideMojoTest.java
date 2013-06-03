package com.github.rmannibucau.maven.landslide;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.ReflectionUtils;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class LandSlideMojoTest {
    @Test
    public void simpleGeneration() throws Exception {

        final File input = new File("target/simple/in.md");
        FileUtils.deleteDirectory(input.getParentFile());
        FileUtils.forceMkdir(input.getParentFile());
        FileUtils.write(input, "# Slide 1\n\n* item 1\n* item 2\n");

        final File output = new File("target/simple/out.html");

        final LandSlideMojo mojo = new LandSlideMojo();
        ReflectionUtils.setVariableValueInObject(mojo, "source", input);
        ReflectionUtils.setVariableValueInObject(mojo, "destination", output);
        ReflectionUtils.setVariableValueInObject(mojo, "watch", -1);
        mojo.execute();

        assertTrue(output.exists());
        final String content = FileUtils.readFileToString(output);
        assertTrue(content.contains("<html>"));
        assertTrue(content.contains("Slide 1"));
    }
}
