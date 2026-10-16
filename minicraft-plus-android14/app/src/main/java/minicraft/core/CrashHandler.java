package minicraft.core;

import android.util.Log;
import minicraft.util.Logging;
import org.jetbrains.annotations.Nullable;

/** Android reports failures to Logcat instead of opening desktop Swing dialogs. */
public final class CrashHandler {
    private CrashHandler() {}

    public static void crashHandle(Thread thread, Throwable throwable) { crashHandle(throwable); }
    public static void crashHandle(Throwable throwable) { crashHandle(throwable, new ErrorInfo(true)); }
    public static void crashHandle(Throwable throwable, ErrorInfo info) {
        Log.e("Minicraft+", info.title, throwable);
        Logging.CRASHHANDLER.error(throwable);
        Game.quit();
    }
    public static void errorHandle(Throwable throwable) { errorHandle(throwable, new ErrorInfo()); }
    public static void errorHandle(Throwable throwable, ErrorInfo info) { errorHandle(throwable, info, null); }
    public static void errorHandle(Throwable throwable, ErrorInfo info, @Nullable Action handling) {
        Log.w("Minicraft+", info.title, throwable);
        Logging.CRASHHANDLER.warn(throwable);
        if (handling != null) handling.act();
    }

    public static class ErrorInfo {
        public final String title;
        public final ErrorType type;
        public final String message;
        public final boolean serious;
        public ErrorInfo() { this(false); }
        public ErrorInfo(boolean crashing) { this(crashing ? "General Application Crash" : "General Application Error", crashing ? ErrorType.DEFAULT : ErrorType.REPORT); }
        public ErrorInfo(String topic) { this(topic, ErrorType.DEFAULT); }
        public ErrorInfo(String topic, ErrorType type) { this(topic, type, type.exitCode != 0); }
        public ErrorInfo(String topic, ErrorType type, boolean serious) { this(topic, type, serious, null); }
        public ErrorInfo(String topic, ErrorType type, String message) { this(topic, type, type.exitCode < 0, message); }
        public ErrorInfo(String topic, ErrorType type, boolean serious, String message) {
            this.title = topic; this.type = type; this.message = message; this.serious = serious;
        }
        public enum ErrorType {
            DEFAULT(-1, "Unhandled error"), UNEXPECTED(-2, "Unexpected error"), UNHANDLEABLE(-3, "Unhandleable error"),
            SERIOUS(1, "Serious error"), HANDLED(0, "Handled error"), REPORT(0, "Error report");
            public final int exitCode; public final String name;
            ErrorType(int exitCode, String name) { this.exitCode = exitCode; this.name = name; }
        }
    }
}
