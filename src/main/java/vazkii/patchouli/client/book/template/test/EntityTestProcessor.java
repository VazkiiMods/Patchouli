package vazkii.patchouli.client.book.template.test;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.template.IComponentProcessor;
import vazkii.patchouli.client.book.template.IVariableProvider;

public class EntityTestProcessor implements IComponentProcessor {

	String entityName;
	String unlockAdvancement;
	boolean locked;
	
	// TODO support nbt
	
	@Override
	public void setup(IVariableProvider variables) {
		String entityType = variables.get("entity");
		if(entityType.contains("{"))
			entityType = entityType.substring(0, entityType.indexOf("{"));
		
		
		entityName = EntityList.getTranslationName(new ResourceLocation(entityType));
		unlockAdvancement = variables.get("unlock_advancement");
	}
	
	@Override
	public void refresh(GuiScreen parent, int left, int top) {
		locked = !ClientAdvancements.hasDone(unlockAdvancement);
	}

	@Override
	public String process(String key) {
		if(key.equals("name"))
			return entityName;
		
		return null;
	}
	
	@Override
	public boolean allowRender(String group) {
		switch(group) {
		case "locked": return locked;
		case "unlocked": return !locked;
		default: return true;
		}
	}

}
