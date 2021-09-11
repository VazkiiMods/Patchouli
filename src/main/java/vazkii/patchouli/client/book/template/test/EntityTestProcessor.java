package vazkii.patchouli.client.book.template.test;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class EntityTestProcessor implements IComponentProcessor {

	private String entityName;

	@Override
	public void setup(IVariableProvider variables) {
		String entityType = variables.get("entity").unwrap().getAsString();
		if (entityType.contains("{")) {
			entityType = entityType.substring(0, entityType.indexOf("{"));
		}

		ResourceLocation key = new ResourceLocation(entityType);
		entityName = Registry.ENTITY_TYPE.getOptional(key)
				.map(EntityType::getDescription).map(Component::getString)
				.orElse(null);
	}

	@Override
	public IVariable process(String key) {
		if (key.equals("name")) {
			return IVariable.wrap(entityName);
		}

		return null;
	}

}
