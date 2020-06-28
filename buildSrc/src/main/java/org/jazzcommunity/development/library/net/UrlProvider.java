package org.jazzcommunity.development.library.net;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.http.client.utils.URIBuilder;
import org.jazzcommunity.development.library.DetectOperatingSystem;
import org.jazzcommunity.development.library.FileTools;
import org.jazzcommunity.development.library.config.ConfigReader;

public class UrlProvider {

  private final URI baseUrl;
  private final String serverName;
  private final String sdkName;
  private final String version;
  private final String licenseUrl;

  private UrlProvider(
      String baseUrl, String serverName, String sdkName, String version, String licenseUrl)
      throws URISyntaxException {
    this.baseUrl = append(new URIBuilder(baseUrl).build(), version);
    this.serverName = serverName;
    this.sdkName = sdkName;
    this.version = version;
    this.licenseUrl = licenseUrl;
  }

  public static UrlProvider fromVersion(String sdk) throws URISyntaxException {
    if (sdk.compareTo("7.0") >= 0) {
      String baseUrl =
          ConfigReader.readConfig(FileTools.toAbsolute("jde/user/downloads/workflow_locations.cfg"))
              .findFirst()
              .get();

      return new UrlProvider(
          baseUrl,
          "JTS-CCM-keys",
          "EWM-SDK-Server",
          sdk,
          "https://jazz.net/downloads/pages/workflow-management/%s/%s/license/%s");
    }

    // all legacy versions
    String baseUrl =
        ConfigReader.readConfig(FileTools.toAbsolute("jde/user/downloads/rtc_locations.cfg"))
            .findFirst()
            .get();

    return new UrlProvider(
        baseUrl,
        "JTS-CCM-keys",
        "RTC-SDK-Server",
        sdk,
        "https://jazz.net/downloads/pages/rational-team-concert/%s/%s/license/%s");
  }

  public URI getSdkUrl() throws URISyntaxException {
    String sdkFile = String.format("%s-%s.zip", sdkName, version);
    return append(baseUrl, sdkFile);
  }

  public URI getServerUrl() throws URISyntaxException {
    String serverFile = String.format("%s-%s64_%s.zip", serverName, getOsString(), version);
    return append(baseUrl, serverFile);
  }

  public URI getServerLicenseUrl() throws URISyntaxException {
    String replaced = String.format(licenseUrl, version, version, "license_en.html");
    return new URI(replaced);
  }

  public URI getSdkLicenseUrl() throws URISyntaxException {
    String replaced = String.format(licenseUrl, version, version, "source/license_en.html");
    return new URI(replaced);
  }

  private static URI append(URI uri, String pathFragment) throws URISyntaxException {
    URIBuilder builder = new URIBuilder(uri);
    List<String> segments = builder.getPathSegments();
    segments.add(pathFragment);
    builder.setPathSegments(segments);
    return builder.build().normalize();
  }

  private String getOsString() {
    if (DetectOperatingSystem.isLinux()) {
      return "Linux";
    }

    if (DetectOperatingSystem.isWindows()) {
      return "Win";
    }

    throw new RuntimeException("Unknown operating system");
  }
}
