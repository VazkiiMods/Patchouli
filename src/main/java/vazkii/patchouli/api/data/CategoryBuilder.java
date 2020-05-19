package vazkii.patchouli.api.data;

import com.google.gson.JsonObject;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import vazkii.patchouli.api.PatchouliAPI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryBuilder {

	private final BookBuilder bookBuilder;
	private final ResourceLocation id;
	private final String name;
	private final String description;
	private final String icon;
	private final List<EntryBuilder> entries = new ArrayList<>();
	private String parent;
	private String flag;
	private Integer sortnum;
	private Boolean secret;

	protected CategoryBuilder(String id, String name, String description, ItemStack icon, BookBuilder bookBuilder) {
		this(id, name, description, PatchouliAPI.instance.serializeItemStack(icon), bookBuilder);
	}

	protected CategoryBuilder(String id, String name, String description, String icon, BookBuilder bookBuilder) {
		this.bookBuilder = bookBuilder;
		this.id = new ResourceLocation(bookBuilder.getId().getNamespace(), id);
		this.name = name;
		this.description = description;
		this.icon = icon;
	}

	JsonObject toJson() {
		JsonObject json = new JsonObject();
		json.addProperty("name", name);
		json.addProperty("description", description);
		json.addProperty("icon", icon);
		if (parent != null) {
			json.addProperty("parent", parent);
		}
		if (flag != null) {
			json.addProperty("flag", flag);
		}
		if (sortnum != null) {
			json.addProperty("sortnum", sortnum);
		}
		if (secret != null) {
			json.addProperty("secret", secret);
		}
		this.serialize(json);
		return json;
	}

	protected void serialize(JsonObject json) {}

	protected List<EntryBuilder> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	public BookBuilder build() {
		return bookBuilder;
	}

	public EntryBuilder addEntry(String id, String name, String icon) {
		return this.addEntry(new EntryBuilder(id, name, icon, this));
	}

	public EntryBuilder addEntry(String id, String name, ItemStack icon) {
		return this.addEntry(new EntryBuilder(id, name, icon, this));
	}

	protected EntryBuilder addEntry(EntryBuilder builder) {
		this.entries.add(builder);
		return builder;
	}

	public CategoryBuilder setParent(String parent) {
		this.parent = parent;
		return this;
	}

	public CategoryBuilder setParent(CategoryBuilder parent) {
		this.parent = parent.getId().toString();
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

	protected ResourceLocation getId() {
		return id;
	}
}
