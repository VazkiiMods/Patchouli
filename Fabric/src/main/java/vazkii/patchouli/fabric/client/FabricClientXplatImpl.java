package vazkii.patchouli.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.patchouli.xplat.IClientXplatAbstractions;

import java.util.Random;

public class FabricClientXplatImpl implements IClientXplatAbstractions {
	@Override
	public void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, Random rand) {
		var layer = ItemBlockRenderTypes.getChunkRenderType(state);
		var buffer = buffers.getBuffer(layer);
		Minecraft.getInstance().getBlockRenderer()
				.renderBatched(state, pos, multiblock, ps, buffer, false, rand);
	}
}
