package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;

/**
 * @author Minecraftschurli
 * @version 2020-02-27
 */
public class TextPageBuilder extends PageBuilder {
    private final String text;
    private String title;

    public TextPageBuilder(String text, EntryBuilder entryBuilder) {
        super("text", entryBuilder);
        this.text = text;
    }

    @Override
    protected void serialize(JsonObject json) {
        json.addProperty("text", text);
        if (title != null)
            json.addProperty("title", title);
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
