package vazkii.patchouli.mixin.client;

import net.minecraft.client.KeyMapping;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyMapping.class)
public interface AccessorKeyMapping {
	@Accessor("ALL")
	static Map<String, KeyMapping> getAllKeyMappings() {
		throw new IllegalStateException();
	}
}
