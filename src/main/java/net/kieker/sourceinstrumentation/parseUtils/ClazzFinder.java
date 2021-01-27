package net.kieker.sourceinstrumentation.parseUtils;

import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class ClazzFinder {
   public static List<String> getClazzes(final Node node, final String parent, String clazzSeparator) {
      final List<String> clazzes = new LinkedList<>();
      if (node instanceof ClassOrInterfaceDeclaration) {
         final ClassOrInterfaceDeclaration clazz = (ClassOrInterfaceDeclaration) node;
         final String clazzname = parent.length() > 0 ? parent + clazzSeparator + clazz.getName().getIdentifier() : clazz.getName().getIdentifier();
         clazzes.add(clazzname);
         for (final Node child : node.getChildNodes()) {
            clazzes.addAll(getClazzes(child, clazzname, clazzSeparator));
         }
      } else {
         for (final Node child : node.getChildNodes()) {
            clazzes.addAll(getClazzes(child, parent, clazzSeparator));
         }
      }
      return clazzes;
   }

   public static List<String> getClazzes(CompilationUnit cu) {
      final List<String> clazzes = new LinkedList<>();
      for (final Node node : cu.getChildNodes()) {
         clazzes.addAll(getClazzes(node, "", "$"));
      }
      return clazzes;
   }

}
