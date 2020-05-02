package vazkii.patchouli.api.data.page;

import com.google.gson.JsonObject;

import vazkii.patchouli.api.data.AbstractPageBuilder;
import vazkii.patchouli.api.data.EntryBuilder;

/**
 * @author  Minecraftschurli
 * @version 2020-02-27
 */
public class EmptyPageBuilder extends AbstractPageBuilder<EmptyPageBuilder> {
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
