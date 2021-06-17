package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vector4f;

import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookEye;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.SerializedMultiblock;
import vazkii.patchouli.mixin.client.AccessorBlockEntity;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

public class PageMultiblock extends PageWithText {
	private static final Random RAND = new Random();

	String name = "";
	@SerializedName("multiblock_id") Identifier multiblockId;

	@SerializedName("multiblock") SerializedMultiblock serializedMultiblock;

	@SerializedName("enable_visualize") boolean showVisualizeButton = true;

	private transient AbstractMultiblock multiblockObj;
	private transient ButtonWidget visualizeButton;

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
	public void render(MatrixStack ms, int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		GuiBook.drawFromTexture(ms, book, x, y, 405, 149, 106, 106);

		parent.drawCenteredStringNoShadow(ms, name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);

		if (multiblockObj != null) {
			renderMultiblock(ms);
		}

		super.render(ms, mouseX, mouseY, pticks);
	}

	public void handleButtonVisualize(ButtonWidget button) {
		String entryKey = parent.getEntry().getId().toString();
		Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
		MultiblockVisualizationHandler.setMultiblock(multiblockObj, new LiteralText(name), bookmark, true);
		parent.addBookmarkButtons();

		if (!PersistentData.data.clickedVisualize) {
			PersistentData.data.clickedVisualize = true;
			PersistentData.save();
		}
	}

	private void renderMultiblock(MatrixStack ms) {
		multiblockObj.setWorld(mc.world);
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
		ms.push();
		ms.translate(xPos, yPos, 100);
		ms.scale(scale, scale, scale);
		ms.translate(-(float) sizeX / 2, -(float) sizeY / 2, 0);

		// Initial eye pos somewhere off in the distance in the -Z direction
		Vector4f eye = new Vector4f(0, 0, -100, 1);
		Matrix4f rotMat = new Matrix4f();
		rotMat.loadIdentity();

		// For each GL rotation done, track the opposite to keep the eye pos accurate
		ms.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-30F));
		rotMat.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(30));

		float offX = (float) -sizeX / 2;
		float offZ = (float) -sizeZ / 2 + 1;

		float time = parent.ticksInBook * 0.5F;
		if (!Screen.hasShiftDown()) {
			time += ClientTicker.partialTicks;
		}
		ms.translate(-offX, 0, -offZ);
		ms.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(time));
		rotMat.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-time));
		ms.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(45));
		rotMat.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-45));
		ms.translate(offX, 0, offZ);

		// Finally apply the rotations
		eye.transform(rotMat);
		eye.normalizeProjectiveCoordinates();
		/* TODO XXX This does not handle visualization of sparse multiblocks correctly.
			Dense multiblocks store everything in positive X/Z, so this works, but sparse multiblocks store everything from the JSON as-is.
			Potential solution: Rotate around the offset vars of the multiblock, and add AABB method for extent of the multiblock
		*/
		renderElements(ms, multiblockObj, BlockPos.iterate(BlockPos.ORIGIN, new BlockPos(sizeX - 1, sizeY - 1, sizeZ - 1)), eye);

		ms.pop();
	}

	private void renderElements(MatrixStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, Vector4f eye) {
		ms.push();
		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
		ms.translate(0, 0, -1);

		VertexConsumerProvider.Immediate buffers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		doWorldRenderPass(ms, mb, blocks, buffers, eye);
		doTileEntityRenderPass(ms, mb, blocks, buffers, eye);

		// todo 1.15 transparency sorting
		buffers.draw();
		ms.pop();
	}

	private void doWorldRenderPass(MatrixStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, final @Nonnull VertexConsumerProvider.Immediate buffers, Vector4f eye) {
		for (BlockPos pos : blocks) {
			BlockState bs = mb.getBlockState(pos);
			VertexConsumer buffer = buffers.getBuffer(RenderLayers.getBlockLayer(bs));

			ms.push();
			ms.translate(pos.getX(), pos.getY(), pos.getZ());
			MinecraftClient.getInstance().getBlockRenderManager().renderBlock(bs, pos, mb, ms, buffer, false, RAND);
			ms.pop();
		}
	}

	// Hold errored TEs weakly, this may cause some dupe errors but will prevent spamming it every frame
	private final transient Set<BlockEntity> erroredTiles = Collections.newSetFromMap(new WeakHashMap<>());

	private void doTileEntityRenderPass(MatrixStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, VertexConsumerProvider buffers, Vector4f eye) {
		for (BlockPos pos : blocks) {
			BlockEntity te = mb.getBlockEntity(pos);
			if (te != null && !erroredTiles.contains(te)) {
				// Doesn't take pos anymore, maybe a problem?
				te.setWorld(mc.world);

				// fake cached state in case the renderer checks it as we don't want to query the actual world
				((AccessorBlockEntity) te).setCachedState(mb.getBlockState(pos));

				ms.push();
				ms.translate(pos.getX(), pos.getY(), pos.getZ());
				try {
					BlockEntityRenderer<BlockEntity> renderer = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().get(te);
					if (renderer != null) {
						renderer.render(te, ClientTicker.partialTicks, ms, buffers, 0xF000F0, OverlayTexture.DEFAULT_UV);
					}
				} catch (Exception e) {
					erroredTiles.add(te);
					Patchouli.LOGGER.error("An exception occured rendering tile entity", e);
				} finally {
					ms.pop();
				}
			}
		}
	}
}
