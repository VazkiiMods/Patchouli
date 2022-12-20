package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.Bookmark;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.LiquidBlockVertexConsumer;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookEye;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.SerializedMultiblock;
import vazkii.patchouli.xplat.IClientXplatAbstractions;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class PageMultiblock extends PageWithText {
	private static final RandomSource RAND = RandomSource.createNewThreadLocalInstance();

	String name = "";
	@SerializedName("multiblock_id") ResourceLocation multiblockId;

	@SerializedName("multiblock") SerializedMultiblock serializedMultiblock;

	@SerializedName("enable_visualize") boolean showVisualizeButton = true;

	private transient AbstractMultiblock multiblockObj;
	private transient Button visualizeButton;

	@Override
	public void build(BookEntry entry, BookContentsBuilder builder, int pageNum) {
		super.build(entry, builder, pageNum);
		if (multiblockId != null) {
			IMultiblock mb = MultiblockRegistry.MULTIBLOCKS.get(multiblockId);

			if (mb instanceof AbstractMultiblock) {
				multiblockObj = (AbstractMultiblock) mb;
			}
		}

		if (multiblockObj == null && serializedMultiblock != null) {
			multiblockObj = serializedMultiblock.toMultiblock();
		}

		if (multiblockObj == null) {
			throw new IllegalArgumentException("No multiblock located for " + multiblockId);
		}
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		if (showVisualizeButton) {
			addButton(visualizeButton = new GuiButtonBookEye(parent, 12, 97, this::handleButtonVisualize));
		}
	}

	@Override
	public int getTextHeight() {
		return 115;
	}

	@Override
	public void render(PoseStack ms, int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		GuiBook.drawFromTexture(ms, book, x, y, 405, 149, 106, 106);

		parent.drawCenteredStringNoShadow(ms, i18n(name), GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);

		if (multiblockObj != null) {
			renderMultiblock(ms);
		}

		super.render(ms, mouseX, mouseY, pticks);
	}

	public void handleButtonVisualize(Button button) {
		var entryKey = parent.getEntry().getId();
		Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
		MultiblockVisualizationHandler.setMultiblock(multiblockObj, i18nText(name), bookmark, true);
		parent.addBookmarkButtons();

		if (!PersistentData.data.clickedVisualize) {
			PersistentData.data.clickedVisualize = true;
			PersistentData.save();
		}
	}

	private void renderMultiblock(PoseStack ms) {
		multiblockObj.setWorld(mc.level);
		Vec3i size = multiblockObj.getSize();
		int sizeX = size.getX();
		int sizeY = size.getY();
		int sizeZ = size.getZ();
		float maxX = 90;
		float maxY = 90;
		float diag = (float) Math.sqrt(sizeX * sizeX + sizeZ * sizeZ);
		float scaleX = maxX / diag;
		float scaleY = maxY / sizeY;
		float scale = -Math.min(scaleX, scaleY);

		int xPos = GuiBook.PAGE_WIDTH / 2;
		int yPos = 60;
		ms.pushPose();
		ms.translate(xPos, yPos, 100);
		ms.scale(scale, scale, scale);
		ms.translate(-(float) sizeX / 2, -(float) sizeY / 2, 0);

		// Initial eye pos somewhere off in the distance in the -Z direction
		Vector4f eye = new Vector4f(0, 0, -100, 1);
		Matrix4f rotMat = new Matrix4f();
		rotMat.identity();

		// For each GL rotation done, track the opposite to keep the eye pos accurate
		ms.mulPose(Axis.XP.rotationDegrees(-30F));
		rotMat.rotation(Axis.XP.rotationDegrees(30));

		float offX = (float) -sizeX / 2;
		float offZ = (float) -sizeZ / 2 + 1;

		float time = parent.ticksInBook * 0.5F;
		if (!Screen.hasShiftDown()) {
			time += ClientTicker.partialTicks;
		}
		ms.translate(-offX, 0, -offZ);
		ms.mulPose(Axis.YP.rotationDegrees(time));
		rotMat.rotation(Axis.YP.rotationDegrees(-time));
		ms.mulPose(Axis.YP.rotationDegrees(45));
		rotMat.rotation(Axis.YP.rotationDegrees(-45));
		ms.translate(offX, 0, offZ);

		// Finally apply the rotations
		eye.mul(rotMat);
		//eye.perspectiveDivide();//TODO find what replaces this
		/* TODO XXX This does not handle visualization of sparse multiblocks correctly.
			Dense multiblocks store everything in positive X/Z, so this works, but sparse multiblocks store everything from the JSON as-is.
			Potential solution: Rotate around the offset vars of the multiblock, and add AABB method for extent of the multiblock
		*/
		renderElements(ms, multiblockObj, BlockPos.betweenClosed(BlockPos.ZERO, new BlockPos(sizeX - 1, sizeY - 1, sizeZ - 1)), eye);

		ms.popPose();
	}

	private void renderElements(PoseStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, Vector4f eye) {
		ms.pushPose();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		ms.translate(0, 0, -1);

		MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		doWorldRenderPass(ms, mb, blocks, buffers, eye);
		doTileEntityRenderPass(ms, mb, blocks, buffers, eye);

		// todo 1.15 transparency sorting
		buffers.endBatch();
		ms.popPose();
	}

	private void doWorldRenderPass(PoseStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, final @NotNull MultiBufferSource.BufferSource buffers, Vector4f eye) {
		for (BlockPos pos : blocks) {
			BlockState bs = mb.getBlockState(pos);
			ms.pushPose();
			ms.translate(pos.getX(), pos.getY(), pos.getZ());

			final FluidState fluidState = bs.getFluidState();
			final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
			if (!fluidState.isEmpty()) {
				final RenderType layer = ItemBlockRenderTypes.getRenderLayer(fluidState);
				final VertexConsumer buffer = buffers.getBuffer(layer);
				blockRenderer.renderLiquid(pos, mb, new LiquidBlockVertexConsumer(buffer, ms, pos), bs, fluidState);
			}
			IClientXplatAbstractions.INSTANCE.renderForMultiblock(bs, pos, mb, ms, buffers, RAND);
			ms.popPose();
		}
	}

	// Hold errored TEs weakly, this may cause some dupe errors but will prevent spamming it every frame
	private final transient Set<BlockEntity> erroredTiles = Collections.newSetFromMap(new WeakHashMap<>());

	private void doTileEntityRenderPass(PoseStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, MultiBufferSource buffers, Vector4f eye) {
		for (BlockPos pos : blocks) {
			BlockEntity te = mb.getBlockEntity(pos);
			if (te != null && !erroredTiles.contains(te)) {
				// Doesn't take pos anymore, maybe a problem?
				te.setLevel(mc.level);

				// fake cached state in case the renderer checks it as we don't want to query the actual world
				te.setBlockState(mb.getBlockState(pos));

				ms.pushPose();
				ms.translate(pos.getX(), pos.getY(), pos.getZ());
				try {
					BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(te);
					if (renderer != null) {
						renderer.render(te, ClientTicker.partialTicks, ms, buffers, 0xF000F0, OverlayTexture.NO_OVERLAY);
					}
				} catch (Exception e) {
					erroredTiles.add(te);
					PatchouliAPI.LOGGER.error("An exception occured rendering tile entity", e);
				} finally {
					ms.popPose();
				}
			}
		}
	}
}
