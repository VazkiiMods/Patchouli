package vazkii.patchouli.api;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
