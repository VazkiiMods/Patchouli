package vazkii.patchouli.api.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Minecraftschurli
 * @version 2020-02-26
 */
public class PageBuilder {
    private final EntryBuilder parent;
    private final String id;
    private final String type;
    private final JsonObject properties;

    public PageBuilder(String id, String type, JsonObject properties, EntryBuilder parent) {
        this.properties = properties;
        this.parent = parent;
        this.id = id;
        this.type = type;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", type);
        for (Map.Entry<String, JsonElement> entry : properties.entrySet()) {
            json.add(entry.getKey(), entry.getValue());
        }
        return json;
    }

    public EntryBuilder build() {
        return parent;
    }
}
