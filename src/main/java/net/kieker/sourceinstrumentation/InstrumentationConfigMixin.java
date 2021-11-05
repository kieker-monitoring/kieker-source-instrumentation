package net.kieker.sourceinstrumentation;

import java.util.Set;

import picocli.CommandLine.Option;

public class InstrumentationConfigMixin {
   @Option(names = { "-extractMethod", "--extractMethod" }, description = "Whether to extract the monitored method to a separate method")
   private boolean extractMethod = false;

   @Option(names = { "-usedRecord", "--usedRecord" }, description = "Which record should be used (OPERATIONEXECUTION or DURATIONRECORD")
   private AllowedKiekerRecord usedRecord = AllowedKiekerRecord.OPERATIONEXECUTION;

   @Option(names = { "-aggregate", "--aggregate" }, description = "Use data aggregation (instead of recording each invocation separately)")
   private boolean aggregate = false;

   @Option(names = { "-aggregationCount", "--aggregationCount" }, description = "How many invocations should be aggregated")
   private int aggregationCount = 1;

   @Option(names = { "-disableDeactivation", "--disableDeactivation" }, description = "Whether to disable deactivation (might improve performance)")
   private boolean disableDeactivation = false;

   @Option(names = { "-createDefaultConstructor", "--createDefaultConstructor" }, description = "If no constructor is present, create an instrumented default constructor")
   private boolean createDefaultConstructor = false;

   @Option(names = { "-enableAdaptiveMonitoring", "--enableAdaptiveMonitoring" }, description = "Enable adaptive monitoring (If not enabled, performance might be better)")
   private boolean enableAdaptiveMonitoring = false;

   @Option(names = { "-includedPatterns", "--includedPatterns" }, description = "Method patterns that should be included in instrumentation")
   private Set<String> includedPatterns;

   @Option(names = { "-excludedPatterns", "--excludedPatterns" }, description = "Method patterns that should be excluded from instrumentation")
   private Set<String> excludedPatterns;

   public void setExtractMethod(final boolean extractMethod) {
      this.extractMethod = extractMethod;
   }

   public boolean isExtractMethod() {
      return extractMethod;
   }

   public AllowedKiekerRecord getUsedRecord() {
      return usedRecord;
   }

   public void setUsedRecord(final AllowedKiekerRecord usedRecord) {
      this.usedRecord = usedRecord;
   }

   public boolean isAggregate() {
      return aggregate;
   }

   public void setAggregate(final boolean aggregate) {
      this.aggregate = aggregate;
   }

   public int getAggregationCount() {
      return aggregationCount;
   }

   public void setAggregationCount(final int aggregationCount) {
      this.aggregationCount = aggregationCount;
   }

   public boolean isDisableDeactivation() {
      return disableDeactivation;
   }

   public void setDisableDeactivation(final boolean disableDeactivation) {
      this.disableDeactivation = disableDeactivation;
   }

   public boolean isCreateDefaultConstructor() {
      return createDefaultConstructor;
   }

   public void setCreateDefaultConstructor(final boolean createDefaultConstructor) {
      this.createDefaultConstructor = createDefaultConstructor;
   }

   public boolean isEnableAdaptiveMonitoring() {
      return enableAdaptiveMonitoring;
   }

   public void setEnableAdaptiveMonitoring(final boolean enableAdaptiveMonitoring) {
      this.enableAdaptiveMonitoring = enableAdaptiveMonitoring;
   }

   public Set<String> getIncludedPatterns() {
      return includedPatterns;
   }

   public void setIncludedPatterns(final Set<String> includedPatterns) {
      this.includedPatterns = includedPatterns;
   }

   public Set<String> getExcludedPatterns() {
      return excludedPatterns;
   }

   public void setExcludedPatterns(final Set<String> excludedPatterns) {
      this.excludedPatterns = excludedPatterns;
   }
}
