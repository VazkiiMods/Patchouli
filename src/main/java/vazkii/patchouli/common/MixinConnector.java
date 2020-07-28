package vazkii.patchouli.common;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {
	@Override
	public void connect() {
		Mixins.addConfigurations("assets/patchouli/patchouli.mixins.json");
	}
}
