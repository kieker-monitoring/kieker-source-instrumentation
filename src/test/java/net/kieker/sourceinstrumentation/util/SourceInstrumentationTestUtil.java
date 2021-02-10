package net.kieker.sourceinstrumentation.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import net.kieker.sourceinstrumentation.TestSourceInstrumentation;

public class SourceInstrumentationTestUtil {
   
   public static void initSimpleProject(String sourcePath) throws IOException {
      TestConstants.CURRENT_FOLDER.mkdirs();
      
      FileUtils.copyFile(new File("src/test/resources/pom.xml"), new File(TestConstants.CURRENT_FOLDER, "pom.xml"));
      for (String path : new String[] {"src/main/java/de/peass/C0_0.java", 
            "src/test/java/de/peass/MainTest.java"}) {
         copyResource(path, sourcePath);
      }
      for (String path : new String[] {
            "src/main/java/de/peass/C1_0.java", 
            "src/main/java/de/peass/AddRandomNumbers.java"}) {
         File testFile = new File(TestConstants.CURRENT_FOLDER, path);
         testFile.delete();
      }
   }
   
   public static void initProject(String sourcePath) throws IOException {
      TestConstants.CURRENT_FOLDER.mkdirs();
      
      FileUtils.copyFile(new File("src/test/resources/pom.xml"), new File(TestConstants.CURRENT_FOLDER, "pom.xml"));
      for (String path : new String[] {"src/main/java/de/peass/C0_0.java", 
            "src/main/java/de/peass/C1_0.java", 
            "src/main/java/de/peass/AddRandomNumbers.java", 
            "src/test/java/de/peass/MainTest.java" }) {
         copyResource(path, sourcePath);
      }
   }
   
   public static File copyResource(String name, String sourcePath) throws IOException {
      File testFile = new File(TestConstants.CURRENT_FOLDER, name);
      if (!testFile.getParentFile().exists()) {
         testFile.getParentFile().mkdirs();
      }
      final URL exampleClass = TestSourceInstrumentation.class.getResource(sourcePath + name);
      FileUtils.copyURLToFile(exampleClass, testFile);
      return testFile;
   }
   
   public static void testFileIsNotInstrumented(File testFile, String fqn) throws IOException {
      String changedSource = FileUtils.readFileToString(testFile, StandardCharsets.UTF_8);

      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString("MonitoringController.getInstance().isMonitoringEnabled()")));
      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString(fqn)));
      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString("new OperationExecutionRecord")));
      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString("kieker.monitoring.core.controller.MonitoringController")));
   }
}
