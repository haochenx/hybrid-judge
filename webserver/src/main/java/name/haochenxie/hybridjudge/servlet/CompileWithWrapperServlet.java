package name.haochenxie.hybridjudge.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;

/**
 * Servlet implementation class CompileWithWrapper
 */
@WebServlet("/compile0")
public class CompileWithWrapperServlet extends HttpServlet {
  private static final Charset utf8 = StandardCharsets.UTF_8;
  private static final long serialVersionUID = 1L;

  /**
   * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
   */
  @SuppressWarnings("deprecation")
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    ServletInputStream payloadInputStream = request.getInputStream();

    String[] cmdarr = new String[] {
        "emcc-wrapper",
        "-tmpdir=" + generateTemporaryDirectoryPath() };
    Process compilerProcess = Runtime.getRuntime().exec(cmdarr);

    IOUtils.copy(payloadInputStream, compilerProcess.getOutputStream());
    compilerProcess.getOutputStream().close();
    InputStream compilerStdout = new BufferedInputStream(compilerProcess.getInputStream());

    StatusLine statusLine = parseAndConsumeStatusLine(compilerStdout);

    if (statusLine.returnCode == 0) {
      // signal for successful compilation
      response.setContentType("application/javascript");
      response.setStatus(HttpServletResponse.SC_OK);
      IOUtils.copy(compilerStdout, response.getOutputStream());
    } else {
      response.setContentType("text/plain");
      response.setStatus(461, "Failed compilation - " + statusLine.returnCode);
      IOUtils.copy(compilerStdout, response.getOutputStream());
    }
  }

  protected static StatusLine parseAndConsumeStatusLine(InputStream is) throws IOException {
    Validate.isTrue(is.markSupported());

    // peek the header
    int trialLength = 1000;
    String header = null;
    while (header == null) {
      is.mark(trialLength);

      byte[] buff = new byte[trialLength];
      int bc = is.read(buff);
      String str = new String(buff, 0, bc, utf8);

      int idx = str.indexOf("\n\n");
      if (idx > 0) {
        header = str.substring(0, idx);
      } else {
        trialLength *= 10;
      }

      is.reset();
    }

    // consume the header from the original input stream
    is.read(new byte[header.getBytes(utf8).length + 2]);

    // parse the header
    Pattern regex = Pattern.compile("(\\d+) (.*)");
    Matcher matcher = regex.matcher(header);
    if (matcher.matches()) {
      int returnCode = Integer.parseInt(matcher.group(1));
      String statusMessage = matcher.group(2);

      return new StatusLine(returnCode, statusMessage);
    } else {
      throw new IOException("unparsable compiler output header: " + header);
    }
  }

  private transient SecureRandom srng = new SecureRandom();

  protected String generateTemporaryDirectoryPath() {
    byte[] buff = new byte[8];
    srng.nextBytes(buff);
    String suffix = Hex.encodeHexString(buff);

    String path = String.format("/tmp/hyj-%s", suffix);

    if (new File(path).isDirectory()) {
      return generateTemporaryDirectoryPath();
    } else {
      return path;
    }
  }

  protected static class StatusLine {
    public final int returnCode;
    public final String statusMessage;

    public StatusLine(int returnCode, String statusMessage) {
      this.returnCode = returnCode;
      this.statusMessage = statusMessage;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + returnCode;
      result = prime * result + ((statusMessage == null) ? 0 : statusMessage.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      StatusLine other = (StatusLine) obj;
      if (returnCode != other.returnCode)
        return false;
      if (statusMessage == null) {
        if (other.statusMessage != null)
          return false;
      } else if (!statusMessage.equals(other.statusMessage))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "StatusLine [returnCode=" + returnCode + ", statusMessage=" + statusMessage + "]";
    }
  }

}
