package net.kieker.sourceinstrumentation.it;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.kieker.sourceinstrumentation.AllowedKiekerRecord;
import net.kieker.sourceinstrumentation.InstrumentationConfiguration;
import net.kieker.sourceinstrumentation.instrument.InstrumentKiekerSource;
import net.kieker.sourceinstrumentation.util.MavenPomUtil;
import net.kieker.sourceinstrumentation.util.SourceInstrumentationTestUtil;
import net.kieker.sourceinstrumentation.util.StreamGobbler;
import net.kieker.sourceinstrumentation.util.TestConstants;

public class SamplingSourceInstrumentationIT {

   File tempFolder;
   
   @BeforeEach
   public void before() throws IOException {
      FileUtils.deleteDirectory(TestConstants.CURRENT_FOLDER);
      
      SourceInstrumentationTestUtil.initProject("/project_2/");

      tempFolder = new File(TestConstants.CURRENT_FOLDER, "results");
      tempFolder.mkdir();
   }

   @Test
   public void testExecution() throws IOException, XmlPullParserException {
      SourceInstrumentationTestUtil.initProject("/project_2_signatures/");

      File tempFolder = new File(TestConstants.CURRENT_FOLDER, "results");
      tempFolder.mkdir();

      final HashSet<String> includedPatterns = new HashSet<>();
      includedPatterns.add("public * de.peass.C0_0.*(..)");
      // Kieker currently does not accept <init> if only * is passed, therefore, <init> needs to be added as separate pattern
      includedPatterns.add("public new de.peass.C0_0.<init>(..)"); 
      includedPatterns.add("public new de.peass.C1_0.<init>(..)");
      
      includedPatterns.add("public * de.peass.C0_0$MyInnerClass.*(..)");
      includedPatterns.add("new de.peass.C0_0$MyInnerClass.<init>(..)");
      includedPatterns.add("public * de.peass.MainTest.*(..)");
      InstrumentationConfiguration kiekerConfiguration = new InstrumentationConfiguration(AllowedKiekerRecord.REDUCED_OPERATIONEXECUTION, true, includedPatterns, false, true);
      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(kiekerConfiguration);

      extendMaven();

      instrumenter.instrumentProject(TestConstants.CURRENT_FOLDER);

      final ProcessBuilder pb = new ProcessBuilder("mvn", "test",
            "-Djava.io.tmpdir=" + tempFolder.getAbsolutePath(),
            "-Dkieker.monitoring.adaptiveMonitoring.enabled=false");
      pb.directory(TestConstants.CURRENT_FOLDER);

      Process process = pb.start();
      StreamGobbler.showFullProcess(process);

      File resultFolder = tempFolder.listFiles()[0];
      File resultFile = resultFolder.listFiles((FileFilter) new WildcardFileFilter("*.dat"))[0];

      String monitorLogs = FileUtils.readFileToString(resultFile, StandardCharsets.UTF_8);
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
      final Model model = reader.read(new FileInputStream(pomFile));
      MavenPomUtil.extendDependencies(model, false);
      final MavenXpp3Writer writer = new MavenXpp3Writer();
      writer.write(new FileWriter(pomFile), model);
   }
}
