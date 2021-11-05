package net.kieker.sourceinstrumentation.it;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.kieker.sourceinstrumentation.util.TestConstants;


public class SourceInstrumentationConstructorProblemIT {
   
   @BeforeEach
   public void before() throws IOException {
      FileUtils.deleteDirectory(TestConstants.CURRENT_FOLDER);
   }

   @Test
   public void testExecution() throws IOException {
      File resultFile = SimpleProjectUtil.obtainLogs("/example_constructorProblem/");

      String monitorLogs = FileUtils.readFileToString(resultFile, StandardCharsets.UTF_8);
      MatcherAssert.assertThat(monitorLogs, Matchers.containsString("public void de.peass.MainTest.testMe()"));
   }
}
