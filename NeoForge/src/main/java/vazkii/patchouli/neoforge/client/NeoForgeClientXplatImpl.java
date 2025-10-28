package vazkii.patchouli.neoforge.client;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.model.data.ModelData;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NeoForgeClientXplatImpl implements IClientXplatAbstractions {
	@Override
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand) {
		BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
		if (state.getRenderShape() != RenderShape.INVISIBLE) {
			// Define a list of common render types to iterate through.
			// This covers most blocks that render in multiple layers (e.g., solid, cutout, translucent).
			// We will check if the block can render in each of these.
			// Collect the model parts to be rendered.
			BlockStateModel model = blockRenderer.getBlockModel(state);
			List<BlockModelPart> parts = new ArrayList<>();
			model.collectParts(multiblock, pos, state, rand, parts);

			List<RenderType> renderTypes = Arrays.asList(
					RenderType.solid(),
					RenderType.cutoutMipped(),
					RenderType.cutout(),
					RenderType.translucent()
			);

			for (RenderType layer : renderTypes) {
				// Check if the block state can actually render in this specific layer.
				if (ItemBlockRenderTypes.getRenderType(state) != null) {
					VertexConsumer buffer = buffers.getBuffer(layer);
					// Call the correct renderBatched method signature, which requires the list of parts.
					blockRenderer.renderBatched(state, pos, multiblock, ps, buffer, true, parts);
				}
			}
		}
	}
}
