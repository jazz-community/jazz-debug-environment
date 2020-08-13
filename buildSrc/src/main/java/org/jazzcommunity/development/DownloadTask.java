package org.jazzcommunity.development;

import com.google.common.base.Strings;
import java.util.ArrayList;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.jazzcommunity.development.library.net.Credentials;
import org.jazzcommunity.development.library.net.DownloadConnection;
import org.jazzcommunity.development.library.net.JazzConnectionBuilder;
import org.jazzcommunity.development.library.net.UrlProvider;
import org.jazzcommunity.development.ui.CredentialDialog;

public class DownloadTask extends DefaultTask {

  private String sdk;
  private boolean dropins;

  @Option(option = "sdk", description = "Which version to download artifacts for.")
  public void setSdk(String sdk) {
    this.sdk = sdk;
  }

  @Option(option = "dropins", description = "Download eclipse dropins for feature based launches.")
  public void setDropins(boolean dropins) {
    this.dropins = dropins;
  }

  @TaskAction
  public void prepare() throws Exception {
    if (Strings.isNullOrEmpty(sdk) && !dropins) {
      getLogger()
          .error(
              "No version provided and drop ins not selected for download. Use '--sdk' to set which version to download artifacts for, or use the '--dropins' flag to download the dropins required for creating a new release.");
      return;
    }

    showCredentialDialog();
  }

  private void showCredentialDialog() throws Exception {
    // determine where to download
    UrlProvider provider = UrlProvider.fromVersion(sdk);
    // start gui interaction, logic is controlled by callbacks
    CredentialDialog dialog = new CredentialDialog(provider);
    // just close the dialog when cancel is clicked
    dialog.onCancelPressed(e -> dialog.dispose());
    // attach callback to download button
    dialog.onDownloadPressed(e -> download(dialog, provider));
    dialog.setVisible(true);
  }

  private void download(CredentialDialog dialog, UrlProvider provider) {
    if (!dialog.licenseAccepted()) {
      return;
    }
    // attempt download if license has been accepted
    dialog.dispose();
    Credentials credentials = dialog.getCredentials();
    ArrayList<DownloadConnection> connections = new ArrayList<>();
    try {
      JazzConnectionBuilder builder = new JazzConnectionBuilder(credentials);

      if (dropins) {
        connections.add(
            builder.build(
                "https://jazz.net/wiki/pub/Main/FeatureBasedLaunches/launcher442.zip",
                "jde/dropins"));
      }

      if (!Strings.isNullOrEmpty(sdk)) {
        connections.add(builder.buildWithLicense(provider.getSdkUrl(), "jde/sdks"));
        connections.add(builder.buildWithLicense(provider.getServerUrl(), "jde/servers"));
      }

      // set project property to resolved download connections
      getProject().setProperty("connections", connections);
    } catch (Exception ex) {
      getLogger().error("Connection to jazz.net failed. Retry with correct credentials.");
      getLogger().error(ex.getMessage());
    }
  }
}
