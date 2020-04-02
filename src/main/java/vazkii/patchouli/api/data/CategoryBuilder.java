package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Minecraftschurli
 * @version 2020-02-26
 */
public class CategoryBuilder {
    final List<EntryBuilder> entries = new ArrayList<>();

    protected final BookBuilder bookBuilder;
    protected final String id;

    private final String name;
    private final String description;
    private final String icon;
    private String parent;
    private String flag;
    private Integer sortnum;
    private Boolean secret;

    CategoryBuilder(String id, String name, String description, ItemStack icon, BookBuilder bookBuilder) {
        this(id, name, description, PatchouliAPI.instance.serializeItemStack(icon), bookBuilder);
    }

    CategoryBuilder(String id, String name, String description, String icon, BookBuilder bookBuilder) {
        this.bookBuilder = bookBuilder;
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
    }

    public BookBuilder build() {
        return bookBuilder;
    }

    JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("description", description);
        json.addProperty("icon", icon);
        if (parent != null)
            json.addProperty("parent", parent);
        if (flag != null)
            json.addProperty("flag", flag);
        if (sortnum != null)
            json.addProperty("sortnum", sortnum);
        if (secret != null)
            json.addProperty("secret", secret);
        return json;
    }

    public EntryBuilder addEntry(String id, String name, String icon) {
        EntryBuilder builder = new EntryBuilder(id, name, this.id, icon, this);
        entries.add(builder);
        return builder;
    }

    public EntryBuilder addEntry(String id, String name, ItemStack icon) {
        EntryBuilder builder = new EntryBuilder(id, name, this.id, icon, this);
        entries.add(builder);
        return builder;
    }

    public CategoryBuilder setParent(String parent) {
        this.parent = parent;
        return this;
    }

    public CategoryBuilder setFlag(String flag) {
        this.flag = flag;
        return this;
    }

    public CategoryBuilder setSortnum(Integer sortnum) {
        this.sortnum = sortnum;
        return this;
    }

    public CategoryBuilder setSecret(Boolean secret) {
        this.secret = secret;
        return this;
    }

    String getId() {
        return id;
    }
}
