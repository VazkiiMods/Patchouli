package vazkii.patchouli.api;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IVariableProvider {

	public String get(String key);
	public boolean has(String key);

}
