package vazkii.patchouli.client.book.page;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

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
import vazkii.patchouli.common.base.Patchouli;
import vazkii.patchouli.common.multiblock.AbstractMultiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.SerializedMultiblock;

import javax.annotation.Nonnull;

import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

public class PageMultiblock extends PageWithText {
	private static final Random RAND = new Random();

	String name = "";
	@SerializedName("multiblock_id") ResourceLocation multiblockId;

	@SerializedName("multiblock") SerializedMultiblock serializedMultiblock;

	@SerializedName("enable_visualize") boolean showVisualizeButton = true;

	private transient AbstractMultiblock multiblockObj;
	private transient Button visualizeButton;

	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);
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
		RenderSystem.color3f(1F, 1F, 1F);
		GuiBook.drawFromTexture(ms, book, x, y, 405, 149, 106, 106);

		parent.drawCenteredStringNoShadow(ms, name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);

		if (multiblockObj != null) {
			renderMultiblock(ms);
		}

		super.render(ms, mouseX, mouseY, pticks);
	}

	public void handleButtonVisualize(Button button) {
		String entryKey = parent.getEntry().getId().toString();
		Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
		MultiblockVisualizationHandler.setMultiblock(multiblockObj, new StringTextComponent(name), bookmark, true);
		parent.addBookmarkButtons();

		if (!PersistentData.data.clickedVisualize) {
			PersistentData.data.clickedVisualize = true;
			PersistentData.save();
		}
	}

	private void renderMultiblock(MatrixStack ms) {
		multiblockObj.setWorld(mc.world);
		Vector3i size = multiblockObj.getSize();
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
		rotMat.setIdentity();

		// For each GL rotation done, track the opposite to keep the eye pos accurate
		ms.rotate(Vector3f.XP.rotationDegrees(-30F));
		rotMat.mul(Vector3f.XP.rotationDegrees(30));

		float offX = (float) -sizeX / 2;
		float offZ = (float) -sizeZ / 2 + 1;

		float time = parent.ticksInBook * 0.5F;
		if (!Screen.hasShiftDown()) {
			time += ClientTicker.partialTicks;
		}
		ms.translate(-offX, 0, -offZ);
		ms.rotate(Vector3f.YP.rotationDegrees(time));
		rotMat.mul(Vector3f.YP.rotationDegrees(-time));
		ms.rotate(Vector3f.YP.rotationDegrees(45));
		rotMat.mul(Vector3f.YP.rotationDegrees(-45));
		ms.translate(offX, 0, offZ);

		// Finally apply the rotations
		eye.transform(rotMat);
		eye.normalize();
		/* TODO XXX This does not handle visualization of sparse multiblocks correctly.
			Dense multiblocks store everything in positive X/Z, so this works, but sparse multiblocks store everything from the JSON as-is.
			Potential solution: Rotate around the offset vars of the multiblock, and add AABB method for extent of the multiblock
		*/
		renderElements(ms, multiblockObj, BlockPos.getAllInBoxMutable(BlockPos.ZERO, new BlockPos(sizeX - 1, sizeY - 1, sizeZ - 1)), eye);

		ms.pop();
	}

	private void renderElements(MatrixStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, Vector4f eye) {
		ms.push();
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		ms.translate(0, 0, -1);

		IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
		doWorldRenderPass(ms, mb, blocks, buffers, eye);
		doTileEntityRenderPass(ms, mb, blocks, buffers, eye);

		// todo 1.15 transparency sorting
		buffers.finish();
		ms.pop();
	}

	private void doWorldRenderPass(MatrixStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, final @Nonnull IRenderTypeBuffer.Impl buffers, Vector4f eye) {
		for (BlockPos pos : blocks) {
			BlockState bs = mb.getBlockState(pos);

			ms.push();
			ms.translate(pos.getX(), pos.getY(), pos.getZ());
			for (RenderType layer : RenderType.getBlockRenderTypes()) {
				if (RenderTypeLookup.canRenderInLayer(bs, layer)) {
					ForgeHooksClient.setRenderLayer(layer);
					IVertexBuilder buffer = buffers.getBuffer(layer);
					Minecraft.getInstance().getBlockRendererDispatcher().renderModel(bs, pos, mb, ms, buffer, false, RAND);
					ForgeHooksClient.setRenderLayer(null);
				}
			}
			ms.pop();
		}
	}

	// Hold errored TEs weakly, this may cause some dupe errors but will prevent spamming it every frame
	private final transient Set<TileEntity> erroredTiles = Collections.newSetFromMap(new WeakHashMap<>());

	private void doTileEntityRenderPass(MatrixStack ms, AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, IRenderTypeBuffer buffers, Vector4f eye) {
		for (BlockPos pos : blocks) {
			TileEntity te = mb.getTileEntity(pos);
			if (te != null && !erroredTiles.contains(te)) {
				te.setWorldAndPos(mc.world, pos);

				// fake cached state in case the renderer checks it as we don't want to query the actual world
				ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, te, mb.getBlockState(pos), "field_195045_e");

				ms.push();
				ms.translate(pos.getX(), pos.getY(), pos.getZ());
				try {
					TileEntityRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(te);
					if (renderer != null) {
						renderer.render(te, ClientTicker.partialTicks, ms, buffers, 0xF000F0, OverlayTexture.NO_OVERLAY);
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
