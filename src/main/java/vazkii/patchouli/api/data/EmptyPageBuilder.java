package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;

/**
 * @author Minecraftschurli
 * @version 2020-02-27
 */
public class EmptyPageBuilder extends PageBuilder {
    private final boolean drawFiller;

    public EmptyPageBuilder(boolean drawFiller, EntryBuilder entryBuilder) {
        super("empty", entryBuilder);
        this.drawFiller = drawFiller;
    }

    @Override
    protected void serialize(JsonObject json) {
        json.addProperty("draw_filler", drawFiller);
    }
}
