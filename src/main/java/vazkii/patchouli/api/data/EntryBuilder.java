package vazkii.patchouli.api.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.PatchouliAPI;

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

    public EntryBuilder(String id, String name, String category, ItemStack icon, CategoryBuilder parent) {
        this.parent = parent;
        this.id = id;
        this.name = name;
        this.category = category;
        this.icon = PatchouliAPI.instance.serializeItemStack(icon);
    }

    JsonObject toJson() {
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

    String getId() {
        return id;
    }

    public TextPageBuilder addTextPage(String text) {
        return addPage(new TextPageBuilder(text, this));
    }

    public ImagePageBuilder addImagePage(String... images) {
        return addPage(new ImagePageBuilder(images, this));
    }

    public CraftingPageBuilder addCraftingPage(ResourceLocation recipe) {
        return addPage(new CraftingPageBuilder(recipe, this));
    }

    public SmeltingPageBuilder addSmeltingPage(ResourceLocation recipe) {
        return addPage(new SmeltingPageBuilder(recipe, this));
    }

    public EntityPageBuilder addEntityPage(String entity) {
        return addPage(new EntityPageBuilder(entity, this));
    }

    public EntityPageBuilder addEntityPage(ResourceLocation entity) {
        return addPage(new EntityPageBuilder(entity.toString(), this));
    }

    public SpotlightPageBuilder addSpotlightPage(ItemStack stack) {
        return addPage(new SpotlightPageBuilder(stack, this));
    }

    public LinkPageBuilder addLinkPage(String url, String linkText) {
        return addPage(new LinkPageBuilder(url, linkText, this));
    }

    public EmptyPageBuilder addEmptyPage() {
        return addPage(new EmptyPageBuilder(true, this));
    }

    public EmptyPageBuilder addEmptyPage(boolean drawFiller) {
        return addPage(new EmptyPageBuilder(drawFiller, this));
    }

    public <T extends PageBuilder> T addPage(T builder) {
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
