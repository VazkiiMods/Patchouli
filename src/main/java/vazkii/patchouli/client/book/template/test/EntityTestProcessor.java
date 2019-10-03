package vazkii.patchouli.client.book.template.test;

import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;

public class EntityTestProcessor implements IComponentProcessor {

	private String entityName;

	@Override
	public void setup(IVariableProvider<String> variables) {
		String entityType = variables.get("entity");
		if (entityType.contains("{"))
			entityType = entityType.substring(0, entityType.indexOf("{"));


		EntityType<?> type = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityType));
		if (type != null) {
			entityName = type.getName().getString();
		}
	}

	@Override
	public String process(String key) {
		if (key.equals("name"))
			return entityName;

		return null;
	}


}
