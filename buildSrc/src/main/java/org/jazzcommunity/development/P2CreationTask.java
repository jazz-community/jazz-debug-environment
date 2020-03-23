package org.jazzcommunity.development;

import com.diffplug.gradle.eclipserunner.EclipseRunner;
import com.diffplug.gradle.eclipserunner.NativeRunner;
import com.diffplug.gradle.p2.FeaturesAndBundlesPublisher;
import java.io.File;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.zip.Zip;

public class P2CreationTask extends DefaultTask {
  private String sdk;

  @TaskAction
  public void createP2Repository() throws Exception {
    this.sdk = sdk.isEmpty() ? FileTools.newestVersion("jde/sdks") : sdk;
    System.out.println(String.format("Creating p2 repository for %s", sdk));

    String target = String.format("jde/p2repo/%s", sdk);
    if (FileTools.exists(target)) {
      System.out.println(String.format("P2 repository for %s already exists. Doing nothing.", sdk));
      return;
    }

    if (!FileTools.versionAvailable("jde/sdks", sdk)) {
      return;
    }

    System.out.println("Extracting sdk files.");
    File temp = FileTools.toAbsolute("jde/p2repo/tmp");
    Zip.extract(FileTools.byVersion("jde/sdks", sdk), temp);

    System.out.println("Creating p2 repository from sdk files");

    FeaturesAndBundlesPublisher publisher = new FeaturesAndBundlesPublisher();
    publisher.source(temp);
    publisher.compress();
    publisher.artifactRepository(FileTools.toAbsolute(target));
    publisher.metadataRepository(FileTools.toAbsolute(target));
    publisher.publishArtifacts();

    // TODO: I'm afraid that this uses an already existing target... but I'm not sure
    File eclipse = FileTools.toAbsolute("build/oomph-ide/eclipse");
    EclipseRunner nativeRunner = new NativeRunner(eclipse);
    publisher.runUsing(nativeRunner);

    FileTools.deleteFolder(temp);
  }

  @Option(
      option = "sdk",
      description = "Which SDK version to create p2 repository for. Default is latest.")
  public void setSdk(String sdk) {
    this.sdk = sdk;
  }
}
