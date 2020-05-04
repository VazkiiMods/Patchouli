package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;

@SuppressWarnings("unchecked")
public abstract class AbstractPageBuilder<T extends AbstractPageBuilder<T>> {
	protected final EntryBuilder parent;
	private final String type;
	private String advancement;
	private String flag;
	private String anchor;

	protected AbstractPageBuilder(String type, EntryBuilder parent) {
		this.parent = parent;
		this.type = type;
	}

	JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("type", type);
		json.addProperty("advancement", advancement);
		json.addProperty("flag", flag);
		json.addProperty("anchor", anchor);
		this.serialize(json);
		return json;
	}

	protected abstract void serialize(JsonObject json);

	public EntryBuilder build() {
		return parent;
	}

	public T setAdvancement(String advancement) {
		this.advancement = advancement;
		return (T) this;
	}

	public T setFlag(String flag) {
		this.flag = flag;
		return (T) this;
	}

	public T setAnchor(String anchor) {
		this.anchor = anchor;
		return (T) this;
	}
}
