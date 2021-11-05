package net.kieker.sourceinstrumentation.it;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.kieker.sourceinstrumentation.AllowedKiekerRecord;
import net.kieker.sourceinstrumentation.SourceInstrumentationTestUtil;
import net.kieker.sourceinstrumentation.instrument.InstrumentKiekerSource;
import net.kieker.sourceinstrumentation.util.TestConstants;

public class SourceInstrumentationInterfaceInheritanceIT {
   
   private static final String INTERFACE_INHERITANCE_FOLDER = "/project_2_interface_inheritance/";

   @BeforeEach
   public void before() throws IOException {
      FileUtils.deleteDirectory(TestConstants.CURRENT_FOLDER);
      
      SimpleProjectUtil.cleanTempDir();
   }


   @Test
   public void testExecution() throws IOException {
      SourceInstrumentationTestUtil.initSimpleProject(INTERFACE_INHERITANCE_FOLDER);
      SourceInstrumentationTestUtil.copyResource("src/main/java/de/peass/SomeInterface.java", INTERFACE_INHERITANCE_FOLDER);
      SourceInstrumentationTestUtil.copyResource("src/main/java/de/peass/SomeOtherInterface.java", INTERFACE_INHERITANCE_FOLDER);

      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(AllowedKiekerRecord.OPERATIONEXECUTION);
      instrumenter.instrumentProject(TestConstants.CURRENT_FOLDER);

      final ProcessBuilder pb = new ProcessBuilder("mvn", "test", 
            "-Djava.io.tmpdir=" + TestConstants.CURRENT_RESULTS.getAbsolutePath());
      pb.directory(TestConstants.CURRENT_FOLDER);

      String monitorLogs = SimpleProjectUtil.getLogsFromProcessBuilder(pb);
      
      MatcherAssert.assertThat(monitorLogs, Matchers.containsString("public void de.peass.MainTest.testMe()"));
   }
}
