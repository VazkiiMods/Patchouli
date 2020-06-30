package vazkii.patchouli.client.book.template.test;

import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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

		Identifier key = new Identifier(entityType);
		entityName = Registry.ENTITY_TYPE.getOrEmpty(key)
				.map(EntityType::getName).map(Text::getString)
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
