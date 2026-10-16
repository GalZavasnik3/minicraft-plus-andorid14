package minicraft.core;

/** Compatibility counters used by the optional debug overlay. */
public final class Initializer extends Game {
    private Initializer() {}
    static int fra, tik;
    public static int getCurFps() { return fra; }
    public static String getExceptionTrace(Throwable throwable) {
        java.io.StringWriter out = new java.io.StringWriter(); throwable.printStackTrace(new java.io.PrintWriter(out)); return out.toString();
    }
}
