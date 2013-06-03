# Landslide Maven Plugin

Usage:

      <plugin>
        <groupId>com.github.rmannibucau</groupId>
        <artifactId>landslide-maven-plugin</artifactId>
        <version>0.1-SNAPSHOT</version>
        <configuration>
          <source>${project.basedir}/src/slides/</source>
          <destination>${project.build.directory}/landslide/presentation.html</destination>
          <theme>${project.basedir}/src/slides/theme</theme> <!-- nothing means default theme -->
          <extensions>tables</extensions> <!-- markdown extensions -->
          <embed>true</embed> <!-- include js, css, images -->
        </configuration>
      </plugin>

# Notes for developers

* The build integrates python dependencies automatically in the jar.
* mainmojo.py is mainly main.py from landslide with the following changes:
    * remove sys.exit calls
    * use args as parameter instead of relying on sys.argv
    * this file needs to be synchronized with main.py when upgrading landslide
* runner.py the wrapper called from java
