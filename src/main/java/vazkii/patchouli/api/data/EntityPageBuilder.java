package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;

/**
 * @author Minecraftschurli
 * @version 2020-02-27
 */
public class EntityPageBuilder extends PageBuilder {
    private final String entity;
    private Float scale;
    private Float offset;
    private Boolean rotate;
    private Float defaultRotation;
    private String name;
    private String text;

    public EntityPageBuilder(String entity, EntryBuilder entryBuilder) {
        super("entity", entryBuilder);
        this.entity = entity;
    }

    @Override
    protected void serialize(JsonObject json) {
        json.addProperty("entity", entity);
        if (scale != null)
            json.addProperty("scale", scale);
        if (offset != null)
            json.addProperty("offset", offset);
        if (rotate != null)
            json.addProperty("rotate", rotate);
        if (defaultRotation != null)
            json.addProperty("default_rotation", defaultRotation);
        if (name != null)
            json.addProperty("name", name);
        if (text != null)
            json.addProperty("text", text);
    }

    public void setScale(Float scale) {
        this.scale = scale;
    }

    public void setOffset(Float offset) {
        this.offset = offset;
    }

    public void setRotate(Boolean rotate) {
        this.rotate = rotate;
    }

    public void setDefaultRotation(Float defaultRotation) {
        this.defaultRotation = defaultRotation;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setText(String text) {
        this.text = text;
    }
}
