package net.kieker.sourceinstrumentation.it;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import net.kieker.sourceinstrumentation.AllowedKiekerRecord;
import net.kieker.sourceinstrumentation.InstrumentationConfiguration;
import net.kieker.sourceinstrumentation.JavaVersionUtil;
import net.kieker.sourceinstrumentation.SourceInstrumentationTestUtil;
import net.kieker.sourceinstrumentation.instrument.InstrumentKiekerSource;
import net.kieker.sourceinstrumentation.util.TestConstants;

public class SourceInstrumentationEnumIT {

   public File writeAdaptiveInstrumentationInfo() throws IOException {
      final File configFolder = new File(TestConstants.CURRENT_FOLDER, "config");
      configFolder.mkdir();

      final File adaptiveFile = new File(TestConstants.CURRENT_FOLDER, TestConstants.KIEKER_ADAPTIVE_FILENAME);
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(adaptiveFile))) {
         writer.write("- *\n");
         writer.write("+ public void de.peass.MainTest.testMe()\n");
         writer.flush();
      }
      return adaptiveFile;
   }

   @Test
   public void testExecution() throws IOException {
      SourceInstrumentationTestUtil.initSimpleProject("/example_enum/");

      InstrumentationConfiguration configuration = new InstrumentationConfiguration(AllowedKiekerRecord.DURATION, true, null, false, false, 10, false);
      
      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(configuration);
      instrumenter.instrumentProject(TestConstants.CURRENT_FOLDER);

      final ProcessBuilder pb = new ProcessBuilder("mvn", "test",
            "-Djava.io.tmpdir=" + TestConstants.CURRENT_RESULTS.getAbsolutePath());
      pb.directory(TestConstants.CURRENT_FOLDER);

      String monitorLogs = SimpleProjectUtil.getLogsFromProcessBuilder(pb);
      
      MatcherAssert.assertThat(monitorLogs, Matchers.containsString("public void de.peass.MainTest.testMe()"));
      if (JavaVersionUtil.getSystemJavaVersion() > 8) {
         MatcherAssert.assertThat(monitorLogs, Matchers.not(Matchers.containsString("public void de.peass.AddRandomNumbers.addSomething();")));
      }
   }
}
