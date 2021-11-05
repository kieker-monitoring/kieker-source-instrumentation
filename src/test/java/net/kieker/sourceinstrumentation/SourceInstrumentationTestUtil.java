package net.kieker.sourceinstrumentation;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import net.kieker.sourceinstrumentation.it.SimpleProjectUtil;
import net.kieker.sourceinstrumentation.util.TestConstants;


public class SourceInstrumentationTestUtil {
   
   public static void initSimpleProject(final String sourcePath) throws IOException {
      clean();
      
      for (String path : new String[] {"src/main/java/de/peass/C0_0.java", 
            "src/test/java/de/peass/MainTest.java"}) {
         copyResource(path, sourcePath);
      }
      copyGenericPom();
   }

   private static void clean() throws IOException {
      FileUtils.deleteDirectory(TestConstants.CURRENT_FOLDER);
      SimpleProjectUtil.cleanTempDir();
      TestConstants.CURRENT_FOLDER.mkdirs();
   }

   private static void copyGenericPom() throws IOException {
      File source = new File("src/test/resources/generic_pom.xml");
      File target = new File(TestConstants.CURRENT_FOLDER, "pom.xml");
      FileUtils.copyFile(source, target);
   }
   
   public static void initProject(final String sourcePath) throws IOException {
      clean();
      
      for (String path : new String[] {"src/main/java/de/peass/C0_0.java", 
            "src/main/java/de/peass/C1_0.java", 
            "src/main/java/de/peass/AddRandomNumbers.java", 
            "src/test/java/de/peass/MainTest.java"}) {
         copyResource(path, sourcePath);
      }
      copyGenericPom();
   }
   
   public static File copyResource(final String name, final String sourcePath) throws IOException {
      File testFile = new File(TestConstants.CURRENT_FOLDER, name);
      if (!testFile.getParentFile().exists()) {
         testFile.getParentFile().mkdirs();
      }
      System.out.println(sourcePath + name);
      final URL exampleClass = TestSourceInstrumentation.class.getResource(sourcePath + name);
      FileUtils.copyURLToFile(exampleClass, testFile);
      return testFile;
   }
   
   public static void testFileIsNotInstrumented(final File testFile, final String fqn) throws IOException {
      String changedSource = FileUtils.readFileToString(testFile, StandardCharsets.UTF_8);

      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString("MonitoringController.getInstance().isMonitoringEnabled()")));
      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString(fqn)));
      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString("new OperationExecutionRecord")));
      MatcherAssert.assertThat(changedSource, Matchers.not(Matchers.containsString("kieker.monitoring.core.controller.MonitoringController")));
   }
}
