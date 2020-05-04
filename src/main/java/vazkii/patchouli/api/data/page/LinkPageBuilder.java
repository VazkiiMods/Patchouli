package vazkii.patchouli.api.data.page;

import com.google.gson.JsonObject;

import vazkii.patchouli.api.data.AbstractPageBuilder;
import vazkii.patchouli.api.data.EntryBuilder;

public class LinkPageBuilder extends AbstractPageBuilder<LinkPageBuilder> {
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
