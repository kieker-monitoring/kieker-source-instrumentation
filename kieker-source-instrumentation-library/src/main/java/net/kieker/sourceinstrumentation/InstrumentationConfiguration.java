package net.kieker.sourceinstrumentation;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.kieker.sourceinstrumentation.instrument.SignatureMatchChecker;
import net.kieker.sourceinstrumentation.instrument.codeblocks.AggregationBlockBuilder;
import net.kieker.sourceinstrumentation.instrument.codeblocks.BlockBuilder;

public class InstrumentationConfiguration {

   private static final String JAVA_8_MESSAGE = "Since Java 8 is used, adaptive instrumentation and deactivation is disabled, since both may cause problems with constructor instrumentation. "
         + "If you like to use this, please consider replacing the return in if (MonitoringController.getInstance().isEnabbled()) { ...; return ; } - calls by an else and check the performance implications.";

   private static final Logger LOG = LogManager.getLogger(InstrumentationConfiguration.class);

   private final AllowedKiekerRecord usedRecord;
   private final boolean aggregate;
   private final int samplingCount;
   private final boolean enableDeactivation;
   private final boolean createDefaultConstructor;
   private final boolean enableAdaptiveMonitoring;
   private final Set<String> includedPatterns;
   private final Set<String> excludedPatterns;
   private final SignatureMatchChecker checker;
   private final boolean extractMethod;

   /**
    * Simple constructor, setting default values for everything except usedRecord, sample and includedPatterns
    */
   public InstrumentationConfiguration(final AllowedKiekerRecord usedRecord, final boolean aggregate,
         final Set<String> includedPatterns, final boolean enableAdaptiveMonitoring, final boolean enableDecativation, final int samplingCount, final boolean extractMethod) {
      this.usedRecord = usedRecord;
      this.aggregate = aggregate;
      this.includedPatterns = includedPatterns;
      excludedPatterns = new HashSet<String>();
      if (JavaVersionUtil.getSystemJavaVersion() == 8) {
         LOG.info(JAVA_8_MESSAGE);
         this.enableAdaptiveMonitoring = false;
         this.enableDeactivation = false;
         this.extractMethod = false;
      } else {
         this.enableAdaptiveMonitoring = enableAdaptiveMonitoring;
         this.enableDeactivation = enableDecativation;
         this.extractMethod = extractMethod;
      }

      this.createDefaultConstructor = true;
      this.samplingCount = samplingCount;
      
      checker = new SignatureMatchChecker(includedPatterns, excludedPatterns);

      check();
   }

   public InstrumentationConfiguration(final AllowedKiekerRecord usedRecord, final boolean aggregate,
         final boolean createDefaultConstructor, final boolean enableAdaptiveMonitoring,
         final Set<String> includedPatterns, final boolean enableDecativation, final int samplingCount, final boolean extractMethod) {
      this.usedRecord = usedRecord;
      this.aggregate = aggregate;
      this.createDefaultConstructor = createDefaultConstructor;
      if (JavaVersionUtil.getSystemJavaVersion() == 8) {
         LOG.info(JAVA_8_MESSAGE);
         this.enableAdaptiveMonitoring = false;
         this.enableDeactivation = false;
         this.extractMethod = false;
      } else {
         this.enableAdaptiveMonitoring = enableAdaptiveMonitoring;
         this.enableDeactivation = enableDecativation;
         this.extractMethod = extractMethod;
      }
      this.includedPatterns = includedPatterns;
      excludedPatterns = new HashSet<String>();
      this.samplingCount = samplingCount;

      checker = new SignatureMatchChecker(includedPatterns, excludedPatterns);
      
      check();
   }

   public InstrumentationConfiguration(final AllowedKiekerRecord usedRecord, final boolean sample,
         final boolean createDefaultConstructor, final boolean enableAdaptiveMonitoring,
         final Set<String> includedPatterns, final Set<String> excludedPatterns, final boolean enableDecativation, final int samplingCount, final boolean extractMethod) {
      this.usedRecord = usedRecord;
      this.aggregate = sample;
      this.createDefaultConstructor = createDefaultConstructor;
      if (JavaVersionUtil.getSystemJavaVersion() == 8) {
         LOG.info(JAVA_8_MESSAGE);
         this.enableAdaptiveMonitoring = false;
         this.enableDeactivation = false;
         this.extractMethod = false;
      } else {
         this.enableAdaptiveMonitoring = enableAdaptiveMonitoring;
         this.enableDeactivation = enableDecativation;
         this.extractMethod = extractMethod;
      }
      this.includedPatterns = includedPatterns;
      this.excludedPatterns = excludedPatterns;
      this.samplingCount = samplingCount;
      
      checker = new SignatureMatchChecker(includedPatterns, excludedPatterns);

      check();
   }

   private void check() {
      if (aggregate && usedRecord == AllowedKiekerRecord.OPERATIONEXECUTION) {
         throw new RuntimeException("Sampling + OperationExecutionRecord does not make sense, since OperationExecutionRecord contains too complex metadata for sampling");
      }
      if (!enableDeactivation && extractMethod) {
         throw new RuntimeException("Disabling deactivation and extracting methods does not make sense, since it only slows down the process");
      }
   }

   

   public AllowedKiekerRecord getUsedRecord() {
      return usedRecord;
   }

   public boolean isAggregate() {
      return aggregate;
   }

   public int getSamplingCount() {
      return samplingCount;
   }

   public boolean isCreateDefaultConstructor() {
      return createDefaultConstructor;
   }

   public boolean isEnableAdaptiveMonitoring() {
      return enableAdaptiveMonitoring;
   }

   public boolean isEnableDeactivation() {
      return enableDeactivation;
   }

   public boolean isExtractMethod() {
      return extractMethod;
   }

   public BlockBuilder getBlockBuilder() {
      BlockBuilder blockBuilder;
      if (this.isAggregate()) {
         blockBuilder = new AggregationBlockBuilder(this.getUsedRecord(), this.getSamplingCount());
      } else {
         blockBuilder = new BlockBuilder(this.getUsedRecord(), this.isEnableDeactivation(), this.isEnableAdaptiveMonitoring());
      }
      return blockBuilder;
   }
   
   public SignatureMatchChecker getChecker() {
      return checker; 
   }
}