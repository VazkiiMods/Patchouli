package vazkii.patchouli.xplat;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IClientXplatAbstractions {
	// NB: Fluids handled at callsite in platform-independent manner
	void renderForMultiblock(BlockState state, BlockPos pos, BlockAndTintGetter multiblock, PoseStack ps, MultiBufferSource buffers, RandomSource rand);

	IClientXplatAbstractions INSTANCE = ServiceUtil.findService(IClientXplatAbstractions.class);
}
