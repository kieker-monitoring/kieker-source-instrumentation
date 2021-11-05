package net.kieker.sourceinstrumentation.it;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import net.kieker.sourceinstrumentation.AllowedKiekerRecord;
import net.kieker.sourceinstrumentation.SourceInstrumentationTestUtil;
import net.kieker.sourceinstrumentation.instrument.InstrumentKiekerSource;
import net.kieker.sourceinstrumentation.parseUtils.StreamGobbler;
import net.kieker.sourceinstrumentation.util.TestConstants;

public class SimpleProjectUtil {
   public static String obtainLogs(final String projectFolder) throws IOException {
      SourceInstrumentationTestUtil.initSimpleProject(projectFolder);

      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(AllowedKiekerRecord.OPERATIONEXECUTION);
      instrumenter.instrumentProject(TestConstants.CURRENT_FOLDER);

      final ProcessBuilder pb = new ProcessBuilder("mvn", "test", 
            "-Djava.io.tmpdir=" + TestConstants.CURRENT_RESULTS.getAbsolutePath());
      pb.directory(TestConstants.CURRENT_FOLDER);

      String logs = getLogsFromProcessBuilder(pb);
      return logs;
   }
   
   public static void cleanTempDir() throws IOException {
      if (TestConstants.CURRENT_RESULTS.exists()) {
         FileUtils.cleanDirectory(TestConstants.CURRENT_RESULTS);
      }
      if (!TestConstants.CURRENT_RESULTS.mkdirs()) {
         throw new RuntimeException("Could not create " + TestConstants.CURRENT_RESULTS.getAbsolutePath());
      }
   }
   
   public static String getLogsFromProcessBuilder(final ProcessBuilder pb) throws IOException {
      Process process = pb.start();
      StreamGobbler.showFullProcess(process);

      File resultFolder = TestConstants.CURRENT_RESULTS.listFiles()[0];
      File resultFile = resultFolder.listFiles((FileFilter) new WildcardFileFilter("*.dat"))[0];

      String monitorLogs = FileUtils.readFileToString(resultFile, StandardCharsets.UTF_8);
      return monitorLogs;
   }
}
