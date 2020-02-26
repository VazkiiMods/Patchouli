package vazkii.patchouli.api.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Minecraftschurli
 * @version 2020-02-26
 */
public class EntryBuilder {
    private final CategoryBuilder parent;
    private final String id;

    private final String name;
    private final String category;
    private final String icon;
    private final List<PageBuilder> pages = new ArrayList<>();
    private String advancement;
    private String flag;
    private Boolean priority;
    private Boolean secret;
    private Boolean readByDefault;
    private Integer sortnum;
    private String turnin;


    public EntryBuilder(String id, String name, String category, String icon, CategoryBuilder parent) {
        this.parent = parent;
        this.id = id;
        this.name = name;
        this.category = category;
        this.icon = icon;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("category", category);
        json.addProperty("icon", icon);
        JsonArray pages = new JsonArray();
        for (PageBuilder page : this.pages) {
            pages.add(page.toJson());
        }
        json.add("pages", pages);
        if (advancement != null)
            json.addProperty("advancement", advancement);
        if (flag != null)
            json.addProperty("flag", flag);
        if (priority != null)
            json.addProperty("priority", priority);
        if (secret != null)
            json.addProperty("secret", secret);
        if (readByDefault != null)
            json.addProperty("read_by_default", readByDefault);
        if (sortnum != null)
            json.addProperty("sortnum", sortnum);
        if (turnin != null)
            json.addProperty("turnin", turnin);
        return json;
    }

    public CategoryBuilder build() {
        return parent;
    }

    public String getId() {
        return id;
    }

    public PageBuilder addPage(String id, String type, JsonObject properties) {
        PageBuilder builder = new PageBuilder(id, type, properties, this);
        pages.add(builder);
        return builder;
    }

    public void setAdvancement(String advancement) {
        this.advancement = advancement;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    public void setSecret(Boolean secret) {
        this.secret = secret;
    }

    public void setReadByDefault(Boolean readByDefault) {
        this.readByDefault = readByDefault;
    }

    public void setSortnum(Integer sortnum) {
        this.sortnum = sortnum;
    }

    public void setTurnin(String turnin) {
        this.turnin = turnin;
    }
}
