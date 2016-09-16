package dk.aau.astep.logger;

/**
 * Created by kafuch on 08-03-16.
 */
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class ALogger {
  public static void log(String message, Module module, Level level){
    LogManager.getLogger(module.name()).log(level, message);
  }

  public static void log(Module module, Exception e){
    LogManager.getLogger(module.name()).log(Level.ERROR, formatException(e));
  }

  private static String formatException(Exception e){
    String s = "EXCEPTION Message: " +e.getMessage() + " Trace: ";
    for (StackTraceElement elem: e.getStackTrace()) {
      s+= elem.toString() + " <- ";
    }
    return s.substring(0, s.length()-4);
  }
}
