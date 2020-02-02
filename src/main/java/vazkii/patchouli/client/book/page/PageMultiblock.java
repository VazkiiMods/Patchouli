package vazkii.patchouli.client.book.page;

import java.util.Collections;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
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

public class PageMultiblock extends PageWithText {
	private static final Random RAND = new Random();

	String name;
	@SerializedName("multiblock_id")
	ResourceLocation multiblockId;
	
	@SerializedName("multiblock")
	SerializedMultiblock serializedMultiblock;

	@SerializedName("enable_visualize")
	boolean showVisualizeButton = true;
	
	private transient AbstractMultiblock multiblockObj;
	private transient Button visualizeButton;

	@Override
	public void build(BookEntry entry, int pageNum) {
		if(multiblockId != null) {
			IMultiblock mb = MultiblockRegistry.MULTIBLOCKS.get(multiblockId);
			
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
	
	public void handleButtonVisualize(Button button) {
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
		renderElements(multiblockObj, BlockPos.getAllInBoxMutable(BlockPos.ZERO, new BlockPos(sizeX - 1, sizeY - 1, sizeZ - 1)), eye);

		RenderSystem.popMatrix();
	}

	private void renderElements(AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, Vector4f eye) {
		RenderSystem.pushMatrix();
		RenderSystem.color4f(1F, 1F, 1F, 1F);
		RenderSystem.translatef(0, 0, -1);

		IRenderTypeBuffer.Impl buffers = Minecraft.getInstance().getBufferBuilders().getEntityVertexConsumers();
		doWorldRenderPass(mb, blocks, buffers, eye);
		doTileEntityRenderPass(mb, blocks, buffers, eye);

		// todo 1.15 transparency sorting
		buffers.draw();
		RenderSystem.popMatrix();
	}

	private void doWorldRenderPass(AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, final @Nonnull IRenderTypeBuffer.Impl buffers, Vector4f eye) {
		MatrixStack ms = new MatrixStack();
		for (BlockPos pos : blocks) {
			BlockState bs = mb.getBlockState(pos);
			IVertexBuilder buffer = buffers.getBuffer(RenderTypeLookup.getBlockLayer(bs));

			ms.push();
			ms.translate(pos.getX(), pos.getY(), pos.getZ());
			Minecraft.getInstance().getBlockRendererDispatcher().renderBlock(bs, pos, mb, ms, buffer, false, RAND);
			ms.pop();
		}
	}

	// Hold errored TEs weakly, this may cause some dupe errors but will prevent spamming it every frame
	private final transient Set<TileEntity> erroredTiles = Collections.newSetFromMap(new WeakHashMap<>());

	private void doTileEntityRenderPass(AbstractMultiblock mb, Iterable<? extends BlockPos> blocks, IRenderTypeBuffer buffers, Vector4f eye) {
		MatrixStack ms = new MatrixStack();

		for (BlockPos pos : blocks) {
			TileEntity te = mb.getTileEntity(pos);
			if (te != null && !erroredTiles.contains(te)) {
				te.setLocation(mc.world, pos);

				// fake cached state in case the renderer checks it as we don't want to query the actual world
				ObfuscationReflectionHelper.setPrivateValue(TileEntity.class, te, mb.getBlockState(pos), "field_195045_e");

				ms.push();
				ms.translate(pos.getX(), pos.getY(), pos.getZ());
				try {
					TileEntityRenderer<TileEntity> renderer = TileEntityRendererDispatcher.instance.getRenderer(te);
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
