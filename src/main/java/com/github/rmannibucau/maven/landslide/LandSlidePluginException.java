package com.github.rmannibucau.maven.landslide;

import org.apache.maven.plugin.MojoExecutionException;

public class LandSlidePluginException extends MojoExecutionException {
    public LandSlidePluginException(final String s) {
        super(s);
    }
}
