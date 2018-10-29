package vazkii.patchouli.client.book.template;

import net.minecraft.client.gui.GuiScreen;

public interface IComponentProcessor {

	public void setup(IVariableProvider variables);
	
	public String process(String key);
	
	public default void refresh(GuiScreen parent, int left, int top) {
		// NO-OP
	}
	
	public default boolean allowRender(String group) {
		return true;
	}
	
}
