package org.jazzcommunity.development.library;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.gradle.api.Project;

public final class RuntimeDetector {
  private RuntimeDetector() {}

  public static String getTargetPlatform(Project project) {
    // this is a workaround for running the ide task while initializing an older version than newest
    // maybe a better idea is just to document properly how to initialize old versions. I'm not yet
    // sure which way to go with this.
    String initializedVersion = FileTools.newestVersion("jde/dev/initialize");
    if (initializedVersion != null) {
      String path = String.format("jde/dev/initialize/%s/sdk", initializedVersion);
      return FileTools.toAbsolute(path).getAbsolutePath();
    }

    String newestVersion = FileTools.newestVersion("jde/sdks");
    // start with "unpacked" parts of the sdk
    if (FileTools.exists(String.format("jde/dev/initialize/%s", newestVersion))) {
      return FileTools.toAbsolute(String.format("jde/dev/initialize/%s/sdk", newestVersion))
          .getPath();
    }

    // fall back to default runtime
    String newestSdk = String.format("jde/runtime/%s/sdk", get(project).get());
    return FileTools.toAbsolute(newestSdk).getPath();
  }

  public static Optional<String> get(Project project) {
    String runtime =
        !project.hasProperty("runtime")
            ? FileTools.newestVersion("jde/runtime")
            : (String) project.getProperties().get("runtime");

    if (runtime == null) {
      return Optional.empty();
    }

    return Optional.of(runtime);
  }

  public static Stream<Runtime> getRuntimes() {
    return Arrays.stream(FileTools.getFiles("jde/runtime")).map(Runtime::new);
  }

  public static Runtime getRuntime(String version) {
    return getRuntimes()
        .filter(r -> r.getVersion().equals(version))
        .findFirst()
        .orElseThrow(
            () -> new RuntimeException(String.format("Invalid run time setup for %s", version)));
  }
}
