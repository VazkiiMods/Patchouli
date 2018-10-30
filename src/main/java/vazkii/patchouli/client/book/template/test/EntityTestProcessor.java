package vazkii.patchouli.client.book.template.test;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariableProvider;
import vazkii.patchouli.client.base.ClientAdvancements;

public class EntityTestProcessor implements IComponentProcessor {

	String entityName;
	boolean locked;
	
	@Override
	public void setup(IVariableProvider variables) {
		String entityType = variables.get("entity");
		if(entityType.contains("{"))
			entityType = entityType.substring(0, entityType.indexOf("{"));
		
		
		entityName = EntityList.getTranslationName(new ResourceLocation(entityType));
	}
	
	@Override
	public String process(String key) {
		if(key.equals("name"))
			return entityName;
		
		return null;
	}
	

}
