package org.jazzcommunity.development.library.net;

public class Credentials {
  private final String username;
  private final char[] password;

  public Credentials(String name, char[] password) {
    this.username = name;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public char[] getPassword() {
    return password;
  }
}
