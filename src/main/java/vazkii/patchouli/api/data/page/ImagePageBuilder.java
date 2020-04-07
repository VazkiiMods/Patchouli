package vazkii.patchouli.api.data.page;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import vazkii.patchouli.api.data.AbstractPageBuilder;
import vazkii.patchouli.api.data.EntryBuilder;

/**
 * @author Minecraftschurli
 * @version 2020-02-27
 */
public class ImagePageBuilder extends AbstractPageBuilder {
    private final String[] images;
    private String title;
    private Boolean border;
    private String text;

    public ImagePageBuilder(String[] images, EntryBuilder parent) {
        super("image", parent);
        this.images = images;
    }

    @Override
    protected void serialize(JsonObject json) {
        JsonArray images = new JsonArray();
        for (String image : this.images) {
            images.add(image);
        }
        json.add("images", images);
        if (title != null)
            json.addProperty("title", title);
        if (border != null)
            json.addProperty("border", border);
        if (text != null)
            json.addProperty("text", text);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBorder(Boolean border) {
        this.border = border;
    }

    public void setText(String text) {
        this.text = text;
    }
}
