package net.kieker.sourceinstrumentation.it;

import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import net.kieker.sourceinstrumentation.AllowedKiekerRecord;
import net.kieker.sourceinstrumentation.SourceInstrumentationTestUtil;
import net.kieker.sourceinstrumentation.instrument.InstrumentKiekerSource;
import net.kieker.sourceinstrumentation.util.TestConstants;

public class SourceInstrumentationCollisionIT {

   @Test
   public void testExecution() throws IOException {
      SourceInstrumentationTestUtil.initSimpleProject("/example_nameCollision/");

      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(AllowedKiekerRecord.OPERATIONEXECUTION);
      instrumenter.instrumentProject(TestConstants.CURRENT_FOLDER);

      final ProcessBuilder pb = new ProcessBuilder("mvn", "test", "-Djava.io.tmpdir=" + TestConstants.CURRENT_RESULTS.getAbsolutePath());
      pb.directory(TestConstants.CURRENT_FOLDER);

      String monitorLogs =  SimpleProjectUtil.getLogsFromProcessBuilder(pb);

      MatcherAssert.assertThat(monitorLogs, Matchers.containsString("public void de.peass.MainTest.testMe()"));
      MatcherAssert.assertThat(monitorLogs, Matchers.not(Matchers.containsString("public void de.peass.AddRandomNumbers.addSomething();")));
   }
}
