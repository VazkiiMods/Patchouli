package vazkii.patchouli.neoforge.client;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class NeoForgeClientXplatImpl implements IClientXplatAbstractions {
	@Override
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, net.minecraft.client.renderer.MultiBufferSource.@NotNull BufferSource buffers, RandomSource rand) {
		BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
		if (state.getRenderShape() != RenderShape.INVISIBLE) {
			BlockStateModel model = blockRenderer.getBlockModel(state);
			List<BlockModelPart> parts = new ArrayList<>();
			model.collectParts(multiblock, pos, state, rand, parts);

			if (parts.isEmpty()) {
				return;
			}

			// Group model parts by their declared render type so mixed-layer blocks pick the right buffer.
			Map<RenderType, List<BlockModelPart>> partsByLayer = new LinkedHashMap<>();
			for (BlockModelPart part : parts) {
				RenderType layer = part.getRenderType(state);
				if (layer == null) {
					layer = ItemBlockRenderTypes.getChunkRenderType(state);
				}
				partsByLayer.computeIfAbsent(layer, key -> new ArrayList<>()).add(part);
			}

			for (Map.Entry<RenderType, List<BlockModelPart>> entry : partsByLayer.entrySet()) {
				VertexConsumer buffer = buffers.getBuffer(entry.getKey());
				blockRenderer.renderBatched(state, pos, multiblock, ps, buffer, true, entry.getValue());
			}
		}
	}
}
