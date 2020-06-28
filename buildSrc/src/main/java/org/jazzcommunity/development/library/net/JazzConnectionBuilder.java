package org.jazzcommunity.development.library.net;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.jazzcommunity.development.library.FileTools;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class JazzConnectionBuilder {

  private final Map<String, String> cookies;

  public JazzConnectionBuilder(Credentials credentials) throws IOException {
    Response response =
        Jsoup.connect("https://jazz.net/auth/login")
            .data(
                "j_username",
                credentials.getUsername(),
                "j_password",
                String.copyValueOf(credentials.getPassword()))
            .method(Method.POST)
            .execute();

    this.cookies = response.cookies();

    // Login was only successful if there are jsessionid, jnat and node cookies set.
    // If invalid credentials were supplied, only jsessionid and node are set.
    if (!this.cookies.containsKey("JNAT")) {
      throw new RuntimeException("Invalid credentials provided. Please try again.");
    }
  }

  public DownloadConnection build(String url, String directory) throws URISyntaxException {
    File destination = FileTools.toAbsolute(directory + File.separator + getFileName(url));
    return new DownloadConnection(url, makeCookieHeader(), destination);
  }

  public DownloadConnection buildWithLicense(URI uri, String directory)
      throws IOException, URISyntaxException {
    String url = getDownloadUrl(uri);
    File destination = FileTools.toAbsolute(directory + File.separator + getFileName(url));
    return new DownloadConnection(url, makeCookieHeader(), destination);
  }

  private String getDownloadUrl(URI uri) throws IOException, URISyntaxException {
    URIBuilder uriBuilder = acceptLicense(uri);
    for (NameValuePair queryParam : uriBuilder.getQueryParams()) {
      if (queryParam.getName().equals("url")) {
        return queryParam.getValue();
      }
    }
    throw new RuntimeException(String.format("Cannot build download url from '%s'", uri));
  }

  private String makeCookieHeader() {
    StringBuilder builder = new StringBuilder();

    for (Entry<String, String> cookie : cookies.entrySet()) {
      builder.append(String.format("%s=%s;", cookie.getKey(), cookie.getValue()));
    }

    return builder.toString();
  }

  private URIBuilder acceptLicense(URI uri) throws IOException, URISyntaxException {
    Response licensePage =
        Jsoup.connect(uri.toString()).method(Method.GET).cookies(cookies).execute();
    // now we somehow have to post again with the license agreed to
    // I guess the easiest approach is to read all the data and just post it to the url
    Document licenseDocument = licensePage.parse();
    String forwardUrl = licenseDocument.selectFirst("input[name=forwardUrl]").val();
    String licenseId = licenseDocument.selectFirst("input[name=licenseId]").val();
    String licenseToken = licenseDocument.selectFirst("input[name=licenseToken]").val();
    String showProfile = "false";
    String license = "accept";
    String continueBtn = "Download";

    Response accept =
        Jsoup.connect("https://jazz.net/action/downloads/save")
            .method(Method.POST)
            .cookies(cookies)
            .timeout(60_000)
            .data(
                "forwardUrl",
                forwardUrl,
                "licenseId",
                licenseId,
                "licenseToken",
                licenseToken,
                "showProfile",
                showProfile,
                "license",
                license,
                "continueBtn",
                continueBtn)
            .execute();

    // then, after posting, we also have to figure out what the GET url then actually is...
    // first, I will try to get the iframe value and parse that...
    Document downloadDocument = accept.parse();

    String src = downloadDocument.selectFirst("iframe[id=file_download]").attr("src");
    return new URIBuilder(src);
  }

  private String getFileName(String url) throws URISyntaxException {
    URIBuilder builder = new URIBuilder(url);
    List<String> segments = builder.getPathSegments();
    return segments.get(segments.size() - 1);
  }
}
