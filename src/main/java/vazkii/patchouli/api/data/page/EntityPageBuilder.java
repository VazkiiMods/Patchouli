package vazkii.patchouli.api.data.page;

import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundNBT;

import vazkii.patchouli.api.data.AbstractPageBuilder;
import vazkii.patchouli.api.data.EntryBuilder;

public class EntityPageBuilder extends AbstractPageBuilder<EntityPageBuilder> {
	private final String entity;
	private CompoundNBT nbt;
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
		json.addProperty("entity", entity + ((nbt != null) ? nbt.toString() : ""));
		if (scale != null) {
			json.addProperty("scale", scale);
		}
		if (offset != null) {
			json.addProperty("offset", offset);
		}
		if (rotate != null) {
			json.addProperty("rotate", rotate);
		}
		if (defaultRotation != null) {
			json.addProperty("default_rotation", defaultRotation);
		}
		if (name != null) {
			json.addProperty("name", name);
		}
		if (text != null) {
			json.addProperty("text", text);
		}
	}

	public EntityPageBuilder setEntityNbt(CompoundNBT nbt) {
		this.nbt = nbt;
		return this;
	}

	public EntityPageBuilder setScale(Float scale) {
		this.scale = scale;
		return this;
	}

	public EntityPageBuilder setOffset(Float offset) {
		this.offset = offset;
		return this;
	}

	public EntityPageBuilder setRotate(Boolean rotate) {
		this.rotate = rotate;
		return this;
	}

	public EntityPageBuilder setDefaultRotation(Float defaultRotation) {
		this.defaultRotation = defaultRotation;
		return this;
	}

	public EntityPageBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public EntityPageBuilder setText(String text) {
		this.text = text;
		return this;
	}
}
