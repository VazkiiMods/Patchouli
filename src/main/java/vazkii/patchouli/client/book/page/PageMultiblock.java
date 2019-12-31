package vazkii.patchouli.client.book.page;

import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Vec3i;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookEye;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.client.base.CustomVertexConsumer;
import vazkii.patchouli.client.mixin.MixinBlockEntity;
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.SerializedMultiblock;

public class PageMultiblock extends PageWithText {
	public static final Random RAND = new Random();

	String name;
	@SerializedName("multiblock_id")
	String multiblockId;
	
	@SerializedName("multiblock")
	SerializedMultiblock serializedMultiblock;

	@SerializedName("enable_visualize")
	boolean showVisualizeButton = true;
	
	private transient AbstractMultiblock multiblockObj;
	private transient ButtonWidget visualizeButton;
	private final transient Random random = new Random();

	@Override
	public void build(BookEntry entry, int pageNum) {
		if(multiblockId != null && !multiblockId.isEmpty()) {
			IMultiblock mb = MultiblockRegistry.MULTIBLOCKS.get(new Identifier(multiblockId));
			
			if(mb instanceof AbstractMultiblock)
				multiblockObj = (AbstractMultiblock) mb;
		}
		
		if(multiblockObj == null && serializedMultiblock != null)
			multiblockObj = serializedMultiblock.toMultiblock();
		
		if(multiblockObj == null)
			throw new IllegalArgumentException("No multiblock located for " + multiblockId);
	}

	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);

		if(showVisualizeButton)
			addButton(visualizeButton = new GuiButtonBookEye(parent, 12, 97, this::handleButtonVisualize));
	}

	@Override
	public int getTextHeight() {
		return 115;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		RenderSystem.enableBlend();
		RenderSystem.color3f(1F, 1F, 1F);
		GuiBook.drawFromTexture(book, x, y, 405, 149, 106, 106);
		
		parent.drawCenteredStringNoShadow(name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);

		if(multiblockObj != null)
			renderMultiblock();
		
		super.render(mouseX, mouseY, pticks);
	}
	
	public void handleButtonVisualize(ButtonWidget button) {
		String entryKey = parent.getEntry().getResource().toString();
		Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
		MultiblockVisualizationHandler.setMultiblock(multiblockObj, name, bookmark, true);
		parent.addBookmarkButtons();
		
		if(!PersistentData.data.clickedVisualize) {
			PersistentData.data.clickedVisualize = true;
			PersistentData.save();
		}
	}

	private void renderMultiblock() {
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
		RenderSystem.pushMatrix();
		RenderSystem.translatef(xPos, yPos, 100);
		RenderSystem.scalef(scale, scale, scale);
		RenderSystem.translatef(-(float) sizeX / 2, -(float) sizeY / 2, 0);

		// Initial eye pos somewhere off in the distance in the -Z direction
		Vector4f eye = new Vector4f(0, 0, -100, 1);
		Matrix4f rotMat = new Matrix4f();
		rotMat.loadIdentity();

		// For each GL rotation done, track the opposite to keep the eye pos accurate
		RenderSystem.rotatef(-30F, 1F, 0F, 0F);
		rotMat.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(30));

		float offX = (float) -sizeX / 2;
		float offZ = (float) -sizeZ / 2 + 1;

		float time = parent.ticksInBook * 0.5F;
		if(!Screen.hasShiftDown())
			time += ClientTicker.partialTicks;
		RenderSystem.translatef(-offX, 0, -offZ);
		RenderSystem.rotatef(time, 0F, 1F, 0F);
		rotMat.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-time));
		RenderSystem.rotatef(45F, 0F, 1F, 0F);
		rotMat.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-45));
		RenderSystem.translatef(offX, 0, offZ);

		// Finally apply the rotations
		eye.transform(rotMat);
		eye.normalizeProjectiveCoordinates();
		renderElements(multiblockObj, BlockPos.iterate(BlockPos.ORIGIN, new BlockPos(sizeX - 1, sizeY - 1, sizeZ - 1)), eye);

		RenderSystem.popMatrix();
	}
	
	private void renderElements(AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, Vector4f eye) {
		RenderSystem.pushMatrix();
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		RenderSystem.translatef(0, 0, -1);

		VertexConsumerProvider.Immediate buffers = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		doWorldRenderPass(mb, blocks, buffers, eye);
		doTileEntityRenderPass(mb, blocks, buffers, eye);

		((CustomVertexConsumer) buffers).patchouli_drawWithCamera(eye.getX(), eye.getY(), eye.getZ());
		RenderSystem.popMatrix();
	}

	private void doWorldRenderPass(AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, final @Nonnull VertexConsumerProvider.Immediate buffers, Vector4f eye) {
		MatrixStack ms = new MatrixStack();
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

	private void doTileEntityRenderPass(AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, VertexConsumerProvider buffers, Vector4f eye) {
		MatrixStack ms = new MatrixStack();
		
		for (BlockPos pos : blocks) {
			BlockEntity te = mb.getBlockEntity(pos);
			if (te != null && !erroredTiles.contains(te)) {
				te.setWorld(mc.world, pos);

				// fake this in case the renderer checks it as we don't want to query the actual world
				((MixinBlockEntity) te).setCachedState(mb.getBlockState(pos));

				ms.push();
				ms.translate(pos.getX(), pos.getY(), pos.getZ());
				try {
					BlockEntityRenderer<BlockEntity> renderer = BlockEntityRenderDispatcher.INSTANCE.get(te);
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
