package net.kieker.sourceinstrumentation.util;

import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

public class MavenPomUtil {

   public static void extendDependencies(final Model model, final boolean junit3) {
      updateToLatestJUnit(model);

      final List<Dependency> dependencies = model.getDependencies();

      final String scope = "";

      final Dependency kopeme_dependency = getDependency("de.dagere.kopeme", "0.15", scope, "kopeme-junit");
      dependencies.add(kopeme_dependency);

      final Dependency kieker_dependency2 = getDependency("net.kieker-monitoring", "1.15", "", "kieker");
      dependencies.add(kieker_dependency2);
   }

   private static void updateToLatestJUnit(final Model model) {
      for (final Dependency dependency : model.getDependencies()) {
         if (dependency.getArtifactId().equals("junit") && dependency.getGroupId().equals("junit")) {
            dependency.setVersion("4.13.1");
         }
         if (dependency.getArtifactId().equals("junit-jupiter") && dependency.getGroupId().equals("org.junit.jupiter")) {
            dependency.setVersion("5.7.0");
         }
      }
   }

   public static Dependency getDependency(final String groupId, final String kopemeVersion, final String scope, final String artifactId) {
      final Dependency kopeme_dependency2 = new Dependency();
      kopeme_dependency2.setGroupId(groupId);
      kopeme_dependency2.setArtifactId(artifactId);
      kopeme_dependency2.setVersion(kopemeVersion);
      kopeme_dependency2.setScope(scope);
      return kopeme_dependency2;
   }
}
