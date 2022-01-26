package net.kieker.sourceinstrumentation.instrument;

import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;

public class ReachabilityDecider {

   public static boolean isAfterUnreachable(final BlockStmt originalBlock) {
      boolean afterUnreachable = false;
      Optional<Statement> optionalLast = originalBlock.getStatements().getLast();
      if (optionalLast.isPresent()) {
         Statement last = optionalLast.get();
         afterUnreachable |= isAfterLastUnreachable(last);
      }
      return afterUnreachable;
   }

   private static boolean isAfterLastUnreachable(final Statement last) {
      boolean afterUnreachable = false;
      if (last instanceof ThrowStmt) {
         afterUnreachable = true;
      } else if (last instanceof WhileStmt) {
         WhileStmt stmt = (WhileStmt) last;
         if (stmt.getCondition().toString().equals("true")) {
            afterUnreachable = true;
         }
      } else if (last instanceof DoStmt) {
         DoStmt stmt = (DoStmt) last;
         if (stmt.getCondition().toString().equals("true")) {
            afterUnreachable = true;
         }
      } else if (last instanceof TryStmt) {
         TryStmt stmt = (TryStmt) last;
         afterUnreachable = isAfterUnreachable(stmt.getTryBlock());
         if (afterUnreachable) {
            for (CatchClause catchClause : stmt.getCatchClauses()) {
               afterUnreachable &= isAfterUnreachable(catchClause.getBody());
            }
         }
      } else if (last instanceof ReturnStmt) {
         afterUnreachable = true;
      } else if (last instanceof BlockStmt) {
         return isAfterUnreachable((BlockStmt) last);
      } else if (last instanceof SynchronizedStmt) {
         return isAfterUnreachable(((SynchronizedStmt) last).getBody());
      } else if (last instanceof ForStmt) {
         ForStmt stmt = (ForStmt) last;
         Optional<Expression> end = stmt.getCompare();

         // Do not change to isEmpty until only Java 11 is used
         if (!end.isPresent()) {
            return true;
         }
      } else if (last instanceof IfStmt) {
         IfStmt ifStatement = (IfStmt) last;
         if (ifStatement.getElseStmt().isPresent()) {
            Statement elseStatement = ifStatement.getElseStmt().get();
            boolean elseAfterEndIsUnreachable = isAfterLastUnreachable(elseStatement);
            if (elseAfterEndIsUnreachable) {
               boolean thenAfterEndIsUnreachable = isAfterLastUnreachable(ifStatement.getThenStmt());
               if (elseAfterEndIsUnreachable && thenAfterEndIsUnreachable) {
                  return true;
               }
            }
         }
         // boolean before
      } else if (last instanceof SwitchStmt) {
         SwitchStmt switchStmt = (SwitchStmt) last;
         Optional<SwitchEntry> optionalLastSwitch = switchStmt.getEntries().getLast();
         if (optionalLastSwitch.isPresent()) {
            SwitchEntry lastSwitch = optionalLastSwitch.get();
            if (lastSwitch.toString().startsWith("default:")) {
               Optional<Statement> potentialLastStatement = lastSwitch.getStatements().getLast();
               if (potentialLastStatement.isPresent()) {
                  return isAfterLastUnreachable(potentialLastStatement.get());
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
      return afterUnreachable;
   }

}
