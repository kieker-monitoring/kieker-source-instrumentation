package net.kieker.sourceinstrumentation.it;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import net.kieker.sourceinstrumentation.AllowedKiekerRecord;
import net.kieker.sourceinstrumentation.InstrumentationConfiguration;
import net.kieker.sourceinstrumentation.SourceInstrumentationTestUtil;
import net.kieker.sourceinstrumentation.instrument.InstrumentKiekerSource;
import net.kieker.sourceinstrumentation.util.MavenPomUtil;
import net.kieker.sourceinstrumentation.util.TestConstants;

public class DurationRecordSourceInstrumentationIT {

   @Test
   public void testExecution() throws IOException, XmlPullParserException {
      SourceInstrumentationTestUtil.initProject("/project_2_signatures/");


      final HashSet<String> includedPatterns = new HashSet<>();
      includedPatterns.add("public * de.peass.C0_0.*(..)");
      // Kieker currently does not accept <init> if only * is passed, therefore, <init> needs to be added as separate pattern
      includedPatterns.add("public new de.peass.C0_0.<init>(..)");
      includedPatterns.add("public new de.peass.C1_0.<init>(..)");

      includedPatterns.add("public * de.peass.C0_0$MyInnerClass.*(..)");
      includedPatterns.add("new de.peass.C0_0$MyInnerClass.<init>(..)");
      includedPatterns.add("public * de.peass.MainTest.*(..)");
      InstrumentationConfiguration kiekerConfiguration = new InstrumentationConfiguration(AllowedKiekerRecord.DURATION, true, includedPatterns, false, true, 1000, false);
      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(kiekerConfiguration);

      extendMaven();

      instrumenter.instrumentProject(TestConstants.CURRENT_FOLDER);

      final ProcessBuilder pb = new ProcessBuilder("mvn", "test",
            "-Djava.io.tmpdir=" + TestConstants.CURRENT_RESULTS.getAbsolutePath(),
            "-Dkieker.monitoring.adaptiveMonitoring.enabled=false");
      pb.directory(TestConstants.CURRENT_FOLDER);

      String monitorLogs = SimpleProjectUtil.getLogsFromProcessBuilder(pb);

      MatcherAssert.assertThat(monitorLogs, Matchers.containsString("public void de.peass.MainTest.testMe()"));
      MatcherAssert.assertThat(monitorLogs, Matchers.containsString("public void de.peass.C0_0.method0()"));
      MatcherAssert.assertThat(monitorLogs, Matchers.containsString("public new de.peass.C0_0.<init>()"));
      MatcherAssert.assertThat(monitorLogs, Matchers.containsString("new de.peass.C0_0$MyInnerClass.<init>(int)"));
      MatcherAssert.assertThat(monitorLogs, Matchers.not(Matchers.containsString("public void de.peass.C1_0.method0()")));
      MatcherAssert.assertThat(monitorLogs, Matchers.not(Matchers.containsString("public void de.peass.AddRandomNumbers.addSomething()")));
   }

   

   private void extendMaven() throws IOException, XmlPullParserException, FileNotFoundException {
      final MavenXpp3Reader reader = new MavenXpp3Reader();
      final File pomFile = new File(TestConstants.CURRENT_FOLDER, "pom.xml");
      try (FileInputStream inputStream = new FileInputStream(pomFile)) {
         final Model model = reader.read(inputStream);
         MavenPomUtil.extendDependencies(model, false);

         try (FileWriter fileWriter = new FileWriter(pomFile)) {
            final MavenXpp3Writer writer = new MavenXpp3Writer();
            writer.write(fileWriter, model);
         }
      }
   }
}
