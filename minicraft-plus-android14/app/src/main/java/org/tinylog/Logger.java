package org.tinylog;

/** Lightweight Android log facade for the desktop game's existing log calls. */
public final class Logger {
    private static final TaggedLogger DEFAULT = new TaggedLogger();
    private Logger() { }
    public static TaggedLogger tag(String tag) { return DEFAULT; }
    public static TaggedLogger tags() { return DEFAULT; }
    public static void trace(Object... values) { }
    public static void debug(Object... values) { }
    public static void info(Object... values) { }
    public static void warn(Object... values) { }
    public static void error(Object... values) { }
}
