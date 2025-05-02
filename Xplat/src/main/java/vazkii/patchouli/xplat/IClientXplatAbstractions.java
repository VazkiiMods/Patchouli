package vazkii.patchouli.xplat;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.patchouli.api.PatchouliAPI;

import java.util.ServiceLoader;
import java.util.stream.Collectors;

public interface IClientXplatAbstractions {
	// NB: Fluids handled at callsite in platform-independent manner
	void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand);

	IClientXplatAbstractions INSTANCE = find();

	private static IClientXplatAbstractions find() {
		var providers = ServiceLoader.load(IClientXplatAbstractions.class).stream().toList();
		if (providers.size() != 1) {
			var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
			IllegalStateException exception = new IllegalStateException("There should be exactly one IClientXplatAbstractions implementation on the classpath, but there are " + providers.size() + ". Found: " + names);

			//TODO: Something is breaking our serviceloader on NeoForge (see https://github.com/VazkiiMods/Patchouli/issues/792).
			// No clue why. Just duct-tape it for now.
			try {
				IClientXplatAbstractions abs = (IClientXplatAbstractions) Class.forName("vazkii.patchouli.neoforge.client.NeoForgeClientXplatImpl").getConstructor().newInstance();
				PatchouliAPI.LOGGER.fatal("Successfully loaded NeoForge backup, but the ServiceLoader wasn't working. Report this.", exception);
				return abs;
			} catch (Exception e) {
				exception.addSuppressed(new RuntimeException("Failed to load NeoForge backup", e));
			}

			try {
				IClientXplatAbstractions abs = (IClientXplatAbstractions) Class.forName("vazkii.patchouli.fabric.client.FabricClientXplatImpl").getConstructor().newInstance();
				PatchouliAPI.LOGGER.fatal("Successfully loaded Fabric backup, but the ServiceLoader wasn't working. Report this.", exception);
				return abs;
			} catch (Exception e) {
				exception.addSuppressed(new RuntimeException("Failed to load Fabric backup", e));
			}

			throw exception;
		} else {
			var provider = providers.get(0);
			PatchouliAPI.LOGGER.debug("Instantiating client xplat impl: " + provider.type().getName());
			return provider.get();
		}
	}
}
