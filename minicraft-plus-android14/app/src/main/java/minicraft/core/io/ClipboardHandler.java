package minicraft.core.io;

/** In-game text fields retain a local clipboard when Android's system clipboard is unavailable. */
public final class ClipboardHandler {
    private static String contents = "";
    public void setClipboardContents(String value) { contents = value == null ? "" : value; }
    public String getClipboardContents() { return contents; }
}
