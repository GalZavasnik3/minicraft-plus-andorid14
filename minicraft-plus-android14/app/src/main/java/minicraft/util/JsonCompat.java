package minicraft.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Small bridge between desktop org.json and Android's built-in JSON API. */
public final class JsonCompat {
    private JsonCompat() {
    }

    public static List<String> keys(JSONObject object) {
        ArrayList<String> result = new ArrayList<>();
        Iterator<String> iterator = object.keys();
        while (iterator.hasNext()) result.add(iterator.next());
        return result;
    }

    public static List<Object> values(JSONArray array) {
        ArrayList<Object> result = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try { result.add(array.get(i)); }
            catch (org.json.JSONException ignored) { }
        }
        return result;
    }
}
