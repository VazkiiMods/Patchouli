package vazkii.patchouli.common.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.patchouli.Patchouli;

@Config(modid = Patchouli.MOD_ID)
public class PatchouliConfig {

	@Name("Disable Advancement Locking")
	@Comment("Set this to true to disable advancement locking and make all entries visible at all times")
	public static boolean disableAdvancementLocking = false;
	
	@Ignore private static Map<String, Boolean> configFlags = new HashMap();
	@Ignore private transient static boolean firstChange = true;
	
	public static void preInit() {
		MinecraftForge.EVENT_BUS.register(ChangeListener.class);

		List<ModContainer> mods = Loader.instance().getActiveModList();
		for(ModContainer container : mods)
			setFlag("mod:" + container.getModId(), true);
		
		setFlag("debug", Patchouli.debug);
		
		updateFlags();
	}
	
	private static void updateFlags() {
		setFlag("advancements_disabled", disableAdvancementLocking);
	}
	
	public static boolean getConfigFlag(String name) {
		if(name.startsWith("&"))
			return getConfigFlagAND(name.replaceAll("\\&|\\|", "").split(","));
		if(name.startsWith("|"))
			return getConfigFlagOR(name.replaceAll("\\&|\\|", "").split(","));
			
		boolean target = true;
		if(name.startsWith("!")) {
			name = name.substring(1);
			target = false;
		}
		name = name.trim().toLowerCase();
		
		boolean status = (configFlags.containsKey(name) && configFlags.get(name)) == target;
		return status;
	}
	
	public static boolean getConfigFlagAND(String[] tokens) {
		for(String s : tokens)
			if(!getConfigFlag(s))
				return false;
		
		return true;
	}
	
	public static boolean getConfigFlagOR(String[] tokens) {
		for(String s : tokens)
			if(getConfigFlag(s))
				return true;
		
		return false;
	}
	
	public static void setFlag(String flag, boolean value) {
		configFlags.put(flag.trim().toLowerCase(), value);
	}

	public static class ChangeListener {

		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
			if(eventArgs.getModID().equals(Patchouli.MOD_ID)) {
	            ConfigManager.sync(Patchouli.MOD_ID, Config.Type.INSTANCE);
	            updateFlags();
	            Patchouli.proxy.requestBookReload();
			}
		}

	}
	
	
}
