package vazkii.patchouli.client.book.template.test;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.template.IComponentProcessor;
import vazkii.patchouli.client.book.template.IVariableProvider;

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
