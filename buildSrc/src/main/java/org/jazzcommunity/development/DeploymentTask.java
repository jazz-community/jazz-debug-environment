package org.jazzcommunity.development;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jazzcommunity.development.library.config.ConfigReader;

public class DeploymentTask extends DefaultTask {

  @TaskAction
  public void deploy() throws IOException {
    List<String> lines =
        ConfigReader.readLines(new File("jde/user/jts_locations.cfg"))
            .filter(l -> !l.startsWith("#"))
            .filter(l -> !l.isEmpty())
            .collect(Collectors.toList());

    String path = lines.get(0);
    System.out.println(String.format("JTS Location Path: %s", path));
    File file = new File(path);
    System.out.println(Arrays.asList(file.listFiles()));

    Files.walk(Paths.get(path))
        .filter(f -> f.endsWith("built-on.txt"))
        .forEach(System.out::println);
  }
}
