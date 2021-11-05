package net.kieker.sourceinstrumentation.it;

import java.io.File;
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

public class SourceInstrumentationInterfaceIT {
   
   private static final String INTERFACE_FOLDER = "/project_2_interface/";


   @BeforeEach
   public void before() throws IOException {
      FileUtils.deleteDirectory(TestConstants.CURRENT_FOLDER);
      
      SimpleProjectUtil.cleanTempDir();
   }


   @Test
   public void testExecution() throws IOException {
      SourceInstrumentationTestUtil.initProject(INTERFACE_FOLDER);
      SourceInstrumentationTestUtil.copyResource("src/main/java/de/peass/SomeInterface.java", INTERFACE_FOLDER);
      SourceInstrumentationTestUtil.copyResource("src/main/java/de/peass/SomeEnum.java", INTERFACE_FOLDER);
      SourceInstrumentationTestUtil.copyResource("src/main/java/de/peass/SomeEnumWithConstructor.java", INTERFACE_FOLDER);

      File tempFolder = new File(TestConstants.CURRENT_FOLDER, "results");
      tempFolder.mkdir();

      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(AllowedKiekerRecord.OPERATIONEXECUTION);
      instrumenter.instrumentProject(TestConstants.CURRENT_FOLDER);

      final ProcessBuilder pb = new ProcessBuilder("mvn", "test", 
            "-Djava.io.tmpdir=" + tempFolder.getAbsolutePath());
      pb.directory(TestConstants.CURRENT_FOLDER);

      String monitorLogs =  SimpleProjectUtil.getLogsFromProcessBuilder(pb);
      
      MatcherAssert.assertThat(monitorLogs, Matchers.containsString("public void de.peass.MainTest.testMe()"));
      MatcherAssert.assertThat(monitorLogs, Matchers.containsString("public void de.peass.AddRandomNumbers.addSomething();"));
   }
}
