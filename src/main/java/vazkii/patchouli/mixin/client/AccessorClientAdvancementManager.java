package vazkii.patchouli.mixin.client;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.multiplayer.ClientAdvancementManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ClientAdvancementManager.class)
public interface AccessorClientAdvancementManager {
	@Accessor
	Map<Advancement, AdvancementProgress> getAdvancementToProgress();
}
