package vazkii.patchouli.xplat;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;

import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface IClientXplatAbstractions {
	IClientXplatAbstractions INSTANCE = find();

	private static IClientXplatAbstractions find() {
		var providers = ServiceLoader.load(IClientXplatAbstractions.class, IClientXplatAbstractions.class.getClassLoader()).stream().toList();
		if (providers.size() != 1) {
			var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
			throw new IllegalStateException("There should be exactly one IClientXplatAbstractions implementation on the classpath. Found: " + names);
		} else {
			var provider = providers.getFirst();
			PatchouliAPI.LOGGER.debug("Instantiating client xplat impl: {}", provider.type().getName());
			return provider.get();
		}
	}

	// NB: Fluids handled at callsite in platform-independent manner
	void renderForMultiblock(BlockRenderDispatcher blockRenderer, BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack poseStack, Function<ChunkSectionLayer, VertexConsumer> bufferLookup, RandomSource rand);

	void submitGuiElement(GuiGraphics graphics, GuiElementRenderState renderState);
	
	void submitMultiblockPiP(GuiGraphics graphics, AbstractMultiblock multiblock, float scale, Vector3f translation, Quaternionf rotation, int x0, int y0, int x1, int y1);
}
