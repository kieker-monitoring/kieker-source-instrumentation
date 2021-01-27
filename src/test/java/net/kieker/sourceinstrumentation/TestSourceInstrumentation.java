package net.kieker.sourceinstrumentation;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import net.kieker.sourceinstrumentation.instrument.InstrumentKiekerSource;
import net.kieker.sourceinstrumentation.util.SourceInstrumentationTestUtil;

public class TestSourceInstrumentation {

   @Test
   public void testSingleClass() throws IOException {
      TestConstants.CURRENT_FOLDER.mkdirs();

      File testFile = SourceInstrumentationTestUtil.copyResource("src/main/java/de/peass/C0_0.java", "/project_2/");

      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(AllowedKiekerRecord.OPERATIONEXECUTION);
      instrumenter.instrument(testFile);

      testFileIsInstrumented(testFile, "public void de.peass.C0_0.method0()", "OperationExecutionRecord");
   }

   public static void testFileIsInstrumented(File testFile, String fqn, String recordName) throws IOException {
      String changedSource = FileUtils.readFileToString(testFile, StandardCharsets.UTF_8);

      MatcherAssert.assertThat(changedSource, Matchers.containsString("import kieker.monitoring.core.controller.MonitoringController;"));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("import kieker.monitoring.core.registry.ControlFlowRegistry;"));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("import kieker.monitoring.core.registry.SessionRegistry;"));

      MatcherAssert.assertThat(changedSource, Matchers.containsString("signature = \"" + fqn));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("new " + recordName));
   }

   @Test
   public void testInnerConstructor() throws IOException {
      SourceInstrumentationTestUtil.initSimpleProject("/example_instanceInnerClass/");

      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(AllowedKiekerRecord.OPERATIONEXECUTION);
      instrumenter.instrumentProject(TestConstants.CURRENT_FOLDER);

      testFileIsInstrumented(new File(TestConstants.CURRENT_FOLDER, "src/main/java/de/peass/C0_0.java"), "public new de.peass.C0_0$InstanceInnerClass.<init>(de.peass.C0_0,int)",
            "OperationExecutionRecord");
      testFileIsInstrumented(new File(TestConstants.CURRENT_FOLDER, "src/main/java/de/peass/C0_0.java"),
            "new de.peass.C0_0$InstanceInnerClass$InnerInnerClass.<init>(de.peass.C0_0$InstanceInnerClass)", "OperationExecutionRecord");

   }

   @Test
   public void testProjectInstrumentation() throws IOException {
      SourceInstrumentationTestUtil.initProject("/project_2/");

      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(AllowedKiekerRecord.OPERATIONEXECUTION);
      instrumenter.instrumentProject(TestConstants.CURRENT_FOLDER);

      testFileIsInstrumented(new File(TestConstants.CURRENT_FOLDER, "src/main/java/de/peass/C0_0.java"), "public void de.peass.C0_0.method0()", "OperationExecutionRecord");
      testFileIsInstrumented(new File(TestConstants.CURRENT_FOLDER, "src/main/java/de/peass/C1_0.java"), "public void de.peass.C1_0.method0()", "OperationExecutionRecord");
      testFileIsInstrumented(new File(TestConstants.CURRENT_FOLDER, "src/main/java/de/peass/AddRandomNumbers.java"), "public int de.peass.AddRandomNumbers.getValue()",
            "OperationExecutionRecord");
      testFileIsInstrumented(new File(TestConstants.CURRENT_FOLDER, "/src/test/java/de/peass/MainTest.java"), "public void de.peass.MainTest.testMe()", "OperationExecutionRecord");
      testFileIsInstrumented(new File(TestConstants.CURRENT_FOLDER, "/src/test/java/de/peass/MainTest.java"), "public new de.peass.MainTest.<init>()", "OperationExecutionRecord");

      testConstructorVisibility();
   }

   private void testConstructorVisibility() throws IOException {
      String changedSourceC1 = FileUtils.readFileToString(new File(TestConstants.CURRENT_FOLDER, "src/main/java/de/peass/C1_0.java"), StandardCharsets.UTF_8);
      MatcherAssert.assertThat(changedSourceC1, Matchers.containsString("String signature = \"public new de.peass.C1_0.<init>()\""));
      String changedSourceC0 = FileUtils.readFileToString(new File(TestConstants.CURRENT_FOLDER, "src/main/java/de/peass/C0_0.java"), StandardCharsets.UTF_8);
      MatcherAssert.assertThat(changedSourceC0, Matchers.containsString("String signature = \"new de.peass.C0_0.<init>()\""));
   }

   @Test
   public void testDifferentSignatures() throws IOException {
      SourceInstrumentationTestUtil.initProject("/project_2_signatures/");

      InstrumentKiekerSource instrumenter = new InstrumentKiekerSource(AllowedKiekerRecord.REDUCED_OPERATIONEXECUTION);
      instrumenter.instrumentProject(TestConstants.CURRENT_FOLDER);

      String changedSource = FileUtils.readFileToString(new File(TestConstants.CURRENT_FOLDER, "src/main/java/de/peass/C0_0.java"), StandardCharsets.UTF_8);

      MatcherAssert.assertThat(changedSource, Matchers.containsString("signature = \"public void de.peass.C0_0.method0(int)\""));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("signature = \"public java.lang.String de.peass.C0_0.method0(java.lang.String)\""));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("signature = \"public java.lang.String[] de.peass.C0_0.methodWithArrayParam(byte[],int[],java.lang.String[])\""));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("signature = \"public java.util.List[] de.peass.C0_0.secondMethodWithArrayParam(byte[],int[],java.util.List[])\""));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("signature = \"public de.peass.C0_0 de.peass.C0_0.doSomethingWithSamePackageObject(de.peass.C1_0)\""));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("signature = \"public de.peass.C0_0$MyInnerClass2 de.peass.C0_0.getInnerInstance()\""));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("signature = \"public static void de.peass.C0_0.myStaticStuff()\""));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("signature = \"public new de.peass.C0_0$MyInnerClass.<init>(int)\""));
      MatcherAssert.assertThat(changedSource, Matchers.containsString("signature = \"public void de.peass.C0_0$MyInnerClass.innerMethod()\""));
   }
}
