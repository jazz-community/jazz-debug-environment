package org.jazzcommunity.development.library.net;

import java.io.File;

public class DownloadConnection {

  private final String url;
  private final String cookieHeader;
  private final File destination;

  public DownloadConnection(String url, String cookieHeader, File destination) {
    this.url = url;
    this.cookieHeader = cookieHeader;
    this.destination = destination;
  }

  public String getUrl() {
    return url;
  }

  public String getCookieHeader() {
    return cookieHeader;
  }

  public File getDestination() {
    return destination;
  }
}
