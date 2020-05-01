package vazkii.patchouli.client.book.template.test;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.api.IVariable;

public class EntityTestProcessor implements IComponentProcessor {

	private String entityName;

	@Override
	public void setup(IVariableProvider variables) {
		String entityType = variables.get("entity").unwrap().getAsString();
		if (entityType.contains("{")) {
			entityType = entityType.substring(0, entityType.indexOf("{"));
		}

		ResourceLocation key = new ResourceLocation(entityType);
		if (ForgeRegistries.ENTITIES.containsKey(key)) {
			entityName = ForgeRegistries.ENTITIES.getValue(key).getName().getString();
		}
	}

	@Override
	public IVariable process(String key) {
		if (key.equals("name")) {
			return IVariable.wrap(entityName);
		}

		return null;
	}

}
