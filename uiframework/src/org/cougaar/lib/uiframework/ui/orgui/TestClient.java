
package org.cougaar.lib.uiframework.ui.orgui;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *  This simple test client connects to a single URL and echoes the response to
 *  standard output.  It can be used to test practically anything that behaves
 *  like a URL.
 */
public class TestClient {
  private static String DEFAULT_URL =
    "http://localhost:5555/$Stuff/orgui/orgsub.psp";

  private String urlString = null;

  public void setUrl (String url) {
    urlString = url;
  }

  // Open the connection to the PSP and return the resulting InputStream
  private InputStream connect () {
    try {
      URL url = new URL(urlString);
      URLConnection conn = url.openConnection();
      conn.setDoInput(true);
      conn.setDoOutput(false);
      return conn.getInputStream();
    }
    catch (Exception oh_no) {
      oh_no.printStackTrace();
    }
    return null;
  }

  private void echo (InputStream in) {
    if (in == null) {
      System.out.println(
        "TestClient::echo:  No input detected--InputStream is null");
      return;
    }

    try {
      byte[] b = new byte[1];
      for (int n = -1; (n = in.read(b)) != -1; System.out.write(b, 0, n));
      System.out.println();
    }
    catch (Exception oh_no) {
      oh_no.printStackTrace();
    }
  }

  public void go () {
    echo(connect());
  }

  public static void main (String[] argv) {
    TestClient tc = new TestClient();
    if (argv.length > 0)
      tc.setUrl(argv[0]);
    else
      tc.setUrl(DEFAULT_URL);
    tc.go();
  }
}