package org.jazzcommunity.development;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jazzcommunity.development.library.config.ConfigReader;
import org.jazzcommunity.development.library.config.plugin.IniEntry;
import org.jazzcommunity.development.library.zip.Zip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeploymentTask extends DefaultTask {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @TaskAction
  public void deploy() throws IOException {
    // collect for multiple usage of locations
    List<String> locations =
        ConfigReader.readConfig(new File("jde/user/jts_locations.cfg"))
            .collect(Collectors.toList());
    // to enable re-deployment of plugins, delete all built-on files
    deleteBuiltOn(locations);
    // get all zip file locations
    Collection<Path> sourcePaths = getSourcePaths();
    // unzip them to temporary location for copying
    Path temp = Files.createTempDirectory("jde-");
    // defer deletion to when jvm terminates
    temp.toFile().deleteOnExit();
    // extract files to temporary directory
    sourcePaths.forEach(from -> Zip.extractWithErrorHandling(from, temp));

    // everything has been extracted already, now we just need to iterate the directory, copy
    // every folder recursively and the ini separately
    for (String destination : locations) {
      // this is where the content of the subfolder goes
      Path sites = Paths.get(destination, "server/conf/ccm/sites");
      // this is where the site xml file goes
      Path profiles = Paths.get(destination, "server/conf/ccm/provision_profiles");

      walk(temp, 1)
          .skip(1)
          .map(Path::toFile)
          .forEach(path -> copyZipContent(path, sites, profiles));
    }
  }

  private void copyZipContent(File path, Path sites, Path profiles) {
    try {
      if (path.isDirectory()) {
        // copy to sites
        FileUtils.copyDirectoryToDirectory(path, sites.toFile());
      } else {
        // copy to provision profiles
        FileUtils.copyFileToDirectory(path, profiles.toFile());
      }
    } catch (Exception e) {
      if (logger.isDebugEnabled()) {
        e.printStackTrace();
      }
      logger.error("Failed to copy '{}': {}", path, e.getMessage());
    }
  }

  private Collection<Path> getSourcePaths() {
    return ConfigReader.userConfiguration()
        .flatMap(IniEntry::getZip)
        .filter(Objects::nonNull)
        // collect for multiple consumption
        .collect(Collectors.toList());
  }

  private void deleteBuiltOn(List<String> locations) {
    locations
        .stream()
        .map(Paths::get)
        .flatMap(this::walk)
        .filter(f -> f.endsWith("built-on.txt"))
        .forEach(this::delete);
  }

  private void delete(Path file) {
    try {
      Files.delete(file);
    } catch (IOException e) {
      if (logger.isDebugEnabled()) {
        e.printStackTrace();
      }
      logger.error("Failed to delete '{}': {}. Delete this file manually.", file, e.getMessage());
    }
  }

  private Stream<Path> walk(Path start) {
    return walk(start, Integer.MAX_VALUE);
  }

  private Stream<Path> walk(Path start, int limit) {
    try {
      return Files.walk(start, limit);
    } catch (IOException e) {
      if (logger.isDebugEnabled()) {
        e.printStackTrace();
      }
      logger.error("Failed to resolve path '{}': {}", start, e.getMessage());
      return Stream.empty();
    }
  }
}
