package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;

/**
 * @author Minecraftschurli
 * @version 2020-02-27
 */
public class LinkPageBuilder extends PageBuilder {
    private final String url;
    private final String linkText;

    public LinkPageBuilder(String url, String linkText, EntryBuilder entryBuilder) {
        super("link", entryBuilder);
        this.url = url;
        this.linkText = linkText;
    }

    @Override
    protected void serialize(JsonObject json) {
        json.addProperty("url", url);
        json.addProperty("link_text", linkText);
    }
}
