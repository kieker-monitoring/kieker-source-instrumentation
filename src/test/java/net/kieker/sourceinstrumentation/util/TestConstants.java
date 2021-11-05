package net.kieker.sourceinstrumentation.util;

import java.io.File;

public class TestConstants {

   public static final File RESOURCE_FOLDER = new File("src/test/resources");

   public static final File CURRENT_FOLDER = new File("target/instrumentation-test-folder");
   public static final File CURRENT_RESULTS = new File(CURRENT_FOLDER, "results");
   public static final String KIEKER_ADAPTIVE_FILENAME = "config/kieker.adaptiveMonitoring.conf";
}
