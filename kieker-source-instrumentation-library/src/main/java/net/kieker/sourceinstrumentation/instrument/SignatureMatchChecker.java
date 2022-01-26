package net.kieker.sourceinstrumentation.instrument;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kieker.monitoring.core.signaturePattern.InvalidPatternException;
import kieker.monitoring.core.signaturePattern.PatternParser;

public class SignatureMatchChecker {

   private static final Logger LOG = LogManager.getLogger(FileInstrumenter.class);

   private final Set<String> includes;
   private final Set<Pattern> includePatterns = new HashSet<>();
   private final Set<String> excludes;
   private final Set<Pattern> excludePatterns = new HashSet<>();

   public SignatureMatchChecker(final Set<String> includes, final Set<String> excludes) {
      this.includes = includes;
      this.excludes = excludes;

      populatePatternSet(includes, includePatterns);
      populatePatternSet(excludes, excludePatterns);
   }

   private void populatePatternSet(final Set<String> stringPatterns, final Set<Pattern> patternSet) {
      if (stringPatterns != null) {
         for (String include : stringPatterns) {
            String pattern = fixConstructorPattern(include);
            try {
               Pattern patternP = PatternParser.parseToPattern(pattern);
               patternSet.add(patternP);
            } catch (InvalidPatternException e) {
               throw new RuntimeException(e);
            }
         }
      }
   }

   public boolean testSignatureMatch(final String signature) {
      boolean oneMatches = false;
      if (includes == null) {
         oneMatches = true;
      } else {
         oneMatches = oneIncludeMatches(signature, oneMatches);
      }
      if (oneMatches && excludes != null) {
         oneMatches = oneExcludeMatches(signature, oneMatches);
      }

      return oneMatches;
   }

   private boolean oneExcludeMatches(final String signature, boolean oneMatches) {
      for (Pattern pattern : excludePatterns) {
         if (pattern.matcher(signature).matches()) {
            oneMatches = false;
            break;
         }
      }
      return oneMatches;
   }

   private boolean oneIncludeMatches(final String signature, boolean oneMatches) {
      for (Pattern pattern : includePatterns) {
         if (pattern.matcher(signature).matches()) {
            oneMatches = true;
            break;
         }
      }
      return oneMatches;
   }

   /**
    * In Kieker 1.14, the return type new is ignored for pattern. Therefore, * needs to be set as return type of constructors in pattern.
    */
   private String fixConstructorPattern(String pattern) {
      if (pattern.contains("<init>")) {
         final String[] tokens = pattern.substring(0, pattern.indexOf('(')).trim().split("\\s+");
         int returnTypeIndex = 0;
         String modifier = "";
         if (tokens[0].equals("private") || tokens[0].equals("public") || tokens[0].equals("protected")) {
            returnTypeIndex++;
            modifier = tokens[0];
         }
         final String returnType = tokens[returnTypeIndex];
         if (returnType.equals("new")) {
            String patternChanged = modifier + " *" + pattern.substring(pattern.indexOf("new") + 3);
            LOG.trace("Changing pattern {} to {}, since Kieker 1.14 does not allow pattern with new", pattern, patternChanged);
            pattern = patternChanged;
         }
      }
      return pattern;
   }
}
