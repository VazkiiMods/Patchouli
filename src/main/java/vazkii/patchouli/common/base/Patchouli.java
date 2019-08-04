package vazkii.patchouli.common.base;

import java.lang.management.ManagementFactory;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import vazkii.patchouli.client.base.ClientProxy;

@Mod(Patchouli.MOD_ID)
public class Patchouli {

	public static boolean debug = ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

	// Mod Constants
	public static final String MOD_ID = "patchouli";
	public static final String PREFIX = MOD_ID + ":";

	public static Patchouli instance;
	public static CommonProxy proxy;

	public Patchouli() {
		instance = this;

		proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
		proxy.start();
	}

}

