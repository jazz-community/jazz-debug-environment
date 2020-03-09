package org.jazzcommunity.development.library.config;

import com.google.common.io.CharSink;
import com.google.common.io.Files;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.Runtime;
import org.jazzcommunity.development.library.RuntimeDetector;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

public final class ConfigWriter {
  private ConfigWriter() {}

  public static void prepareConfigurations(String runtime) {
    String folder = runtime.isEmpty() ? FileTools.newestVersion("jde/runtime") : runtime;
    System.out.println("Generate run time configurations.");
    System.out.println(String.format("Preparing %s run time", folder));

    try {
      System.out.println("Writing config.ini");
      writeIni(folder);
      System.out.println("Writing dev.properties");
      writeProperties(folder);
      System.out.println("Copying log4j configuration");
      copyLogConfig(folder);
      System.out.println("Creating executable files");
      writeExecutables(folder);
    } catch (IOException e) {
      System.out.println(String.format("Runtime configuration failed: %s", e.getMessage()));
    }
  }

  private static void copyLogConfig(String folder) {
    String destination = String.format("jde/runtime/%s/conf/", folder);
    FileTools.copyFile("jde/user/log4j/log4j.properties", destination);
  }

  private static void writeExecutables(String folder) throws IOException {
    String to = String.format("jde/runtime/%s/", folder);
    Runtime runtime = RuntimeDetector.getRuntime(folder);
    String executable = ConfigReader.javaPath();
    makeScript("tool/scripts/run_jetty_bash.twig", to + "run_jetty.sh", executable, runtime);
    makeScript("tool/scripts/run_jetty_powershell.twig", to + "run_jetty.ps1", executable, runtime);
    FileTools.setExecutable(String.format("jde/runtime/%s/run_jetty.sh", folder));
  }

  private static void makeScript(
      String source, String destination, String executable, Runtime runtime) throws IOException {
    CharSink out = Files.asCharSink(FileTools.toAbsolute(destination), Charset.forName("UTF-8"));
    List<String> parameters = ConfigReader.runtimeParameters();
    JtwigTemplate template = JtwigTemplate.fileTemplate(FileTools.toAbsolute(source));
    JtwigModel model =
        JtwigModel.newModel()
            .with("executable", executable)
            .with("launcher", runtime.getLauncherPath())
            .with("parameters", parameters);
    out.write(template.render(model));
  }

  private static void writeProperties(String folder) throws IOException {
    CharSink file =
        Files.asCharSink(
            FileTools.toAbsolute(String.format("jde/runtime/%s/conf/dev.properties", folder)),
            Charset.forName("UTF-8"));

    List<String> properties =
        ConfigReader.userConfiguration()
            .filter(IniEntry::needsPropertyEntry)
            .map(IniEntry::getPropertiesLine)
            .collect(Collectors.toList());

    JtwigTemplate template =
        JtwigTemplate.fileTemplate(FileTools.toAbsolute("tool/templates/dev.twig"));
    JtwigModel model = JtwigModel.newModel().with("properties", properties);
    file.write(template.render(model));
  }

  private static void writeIni(String folder) throws IOException {
    CharSink ini =
        Files.asCharSink(
            FileTools.toAbsolute(String.format("jde/runtime/%s/conf/config.ini", folder)),
            Charset.forName("UTF-8"));

    JtwigTemplate template =
        JtwigTemplate.fileTemplate(FileTools.toAbsolute("tool/templates/config.twig"));

    Runtime runtime = RuntimeDetector.getRuntime(folder);

    JtwigModel model =
        JtwigModel.newModel()
            .with("osgiPath", runtime.getOsgi().getName())
            .with("configurator", runtime.getConfigurator())
            .with("bundles", ConfigReader.sdkFiles(folder).collect(Collectors.toList()))
            .with(
                "configs",
                ConfigReader.userConfiguration()
                    .map(IniEntry::getIniLine)
                    .collect(Collectors.toList()));
    ini.write(template.render(model));
  }
}
