package name.haochenxie.hybridjudge.servlet;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

public class CompileWithWrapperTest extends CompileWithWrapperServlet {

  private static final long serialVersionUID = -1L;

  @Test
  public void testParseStatusLine() throws Exception {
    String sampleOutput = "1 sample message" + "\n\n" + "else output";
    StatusLine sampleStatusLine = new CompileWithWrapperServlet.StatusLine(1, "sample message");

    ByteArrayInputStream sampleStream =
        new ByteArrayInputStream(sampleOutput.getBytes(StandardCharsets.UTF_8));
    StatusLine actualStatusLine = CompileWithWrapperServlet.parseAndConsumeStatusLine(
        sampleStream);

    assertEquals(sampleStatusLine, actualStatusLine);
    assertEquals("else output", IOUtils.toString(sampleStream));
  }

  @Test
  @Ignore
  public void tryGenerateTemporaryDirectoryPath() throws Exception {
    CompileWithWrapperServlet instance = new CompileWithWrapperServlet();
    for (int i=0; i < 10; ++i) {
      String path = instance.generateTemporaryDirectoryPath();
      System.out.println(path);
    }
  }

}
