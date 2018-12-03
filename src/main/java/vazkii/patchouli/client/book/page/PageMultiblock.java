package vazkii.patchouli.client.book.page;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;
import javax.vecmath.Vector4f;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.google.gson.annotations.SerializedName;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.base.PersistentData.DataHolder.BookData.Bookmark;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBookEye;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.client.handler.MultiblockVisualizationHandler;
import vazkii.patchouli.common.multiblock.Multiblock;
import vazkii.patchouli.common.multiblock.MultiblockRegistry;
import vazkii.patchouli.common.multiblock.SerializedMultiblock;

public class PageMultiblock extends PageWithText {

	String name;
	@SerializedName("multiblock_id")
	String multiblockId;
	
	@SerializedName("multiblock")
	SerializedMultiblock serializedMultiblock;

	@SerializedName("enable_visualize")
	boolean showVisualizeButton = true;
	
	transient Multiblock multiblockObj;
	transient GuiButton visualizeButton;

	@Override
	public void build(BookEntry entry, int pageNum) {
		if(multiblockId != null && !multiblockId.isEmpty()) {
			IMultiblock mb = MultiblockRegistry.MULTIBLOCKS.get(new ResourceLocation(multiblockId));
			
			if(mb instanceof Multiblock)
				multiblockObj = (Multiblock) mb;
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
			addButton(visualizeButton = new GuiButtonBookEye(parent, 12, 97));
	}

	@Override
	public int getTextHeight() {
		return 115;
	}
	
	@Override
	public void render(int mouseX, int mouseY, float pticks) {
		int x = GuiBook.PAGE_WIDTH / 2 - 53;
		int y = 7;
		GlStateManager.enableBlend();
		GlStateManager.color(1F, 1F, 1F);
		GuiBook.drawFromTexture(book, x, y, 405, 149, 106, 106);
		
		parent.drawCenteredStringNoShadow(name, GuiBook.PAGE_WIDTH / 2, 0, book.headerColor);

		if(multiblockObj != null)
			renderMultiblock();
		
		super.render(mouseX, mouseY, pticks);
	}

	@Override
	protected void onButtonClicked(GuiButton button) {
		if(button == visualizeButton) {
			String entryKey = parent.getEntry().getResource().toString();
			Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
			MultiblockVisualizationHandler.setMultiblock(multiblockObj, name, bookmark, true);
			parent.addBookmarkButtons();
			
			if(!PersistentData.data.clickedVisualize) {
				PersistentData.data.clickedVisualize = true;
				PersistentData.save();
			}
		}
	}

	private void renderMultiblock() {
		float maxX = 90;
		float maxY = 90;
		float diag = (float) Math.sqrt(multiblockObj.sizeX * multiblockObj.sizeX + multiblockObj.sizeZ * multiblockObj.sizeZ);
		float height = multiblockObj.sizeY;
		float scaleX = maxX / diag;
		float scaleY = maxY / height;
		float scale = -Math.min(scaleX, scaleY);
		
		int xPos = GuiBook.PAGE_WIDTH / 2;
		int yPos = 60;
		GlStateManager.pushMatrix();
		GlStateManager.translate(xPos, yPos, 100);
		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate(-(float) multiblockObj.sizeX / 2, -(float) multiblockObj.sizeY / 2, 0);

		// Initial eye pos somewhere off in the distance in the -Z direction
		Vector4f eye = new Vector4f(0, 0, -100, 1);
		Matrix4f rotMat = new Matrix4f();
		rotMat.setIdentity();

		// For each GL rotation done, track the opposite to keep the eye pos accurate
		GlStateManager.rotate(-30F, 1F, 0F, 0F);
		rotMat.rotX((float) Math.toRadians(30F));

		float offX = (float) -multiblockObj.sizeX / 2;
		float offZ = (float) -multiblockObj.sizeZ / 2 + 1;

		float time = parent.ticksInBook * 0.5F;
		if(!GuiScreen.isShiftKeyDown())
			time += ClientTicker.partialTicks;
		GlStateManager.translate(-offX, 0, -offZ);
		GlStateManager.rotate(time, 0F, 1F, 0F);
		rotMat.rotY((float) Math.toRadians(-time));
		GlStateManager.rotate(45F, 0F, 1F, 0F);
		rotMat.rotY((float) Math.toRadians(-45F));
		GlStateManager.translate(offX, 0, offZ);
		
		// Finally apply the rotations
		rotMat.transform(eye);
		renderElements(multiblockObj, BlockPos.getAllInBoxMutable(BlockPos.ORIGIN, new BlockPos(multiblockObj.sizeX - 1, multiblockObj.sizeY - 1, multiblockObj.sizeZ - 1)), eye);

		GlStateManager.popMatrix();
	}
	
	private void renderElements(Multiblock mb, Iterable<? extends BlockPos> blocks, Vector4f eye) {

		GlStateManager.pushMatrix();
		GlStateManager.color(1F, 1F, 1F, 1F);
		GlStateManager.translate(0, 0, -1);
		
		TileEntityRendererDispatcher.instance.entityX = eye.x;
		TileEntityRendererDispatcher.instance.entityY = eye.y;
		TileEntityRendererDispatcher.instance.entityZ = eye.z;
		TileEntityRendererDispatcher.staticPlayerX = eye.x;
		TileEntityRendererDispatcher.staticPlayerY = eye.y;
		TileEntityRendererDispatcher.staticPlayerZ = eye.z;

		BlockRenderLayer oldRenderLayer = MinecraftForgeClient.getRenderLayer();
		for (BlockRenderLayer layer : BlockRenderLayer.values()) {
			if (layer == BlockRenderLayer.TRANSLUCENT) {
				doTileEntityRenderPass(mb, blocks, 0);
			}
			doWorldRenderPass(mb, blocks, layer, eye);
			if (layer == BlockRenderLayer.TRANSLUCENT) {
				doTileEntityRenderPass(mb, blocks, 1);
			}
		}
		ForgeHooksClient.setRenderLayer(oldRenderLayer);

		ForgeHooksClient.setRenderPass(-1);
		setGlStateForPass(0);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		GlStateManager.popMatrix();
	}

	private void doWorldRenderPass(Multiblock mb, Iterable<? extends BlockPos> blocks, final @Nonnull BlockRenderLayer layer, Vector4f eye) {
		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		
		ForgeHooksClient.setRenderLayer(layer);
		setGlStateForPass(layer);
		
		BufferBuilder wr = Tessellator.getInstance().getBuffer();
		wr.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

		for (BlockPos pos : blocks) {
			IBlockState bs = mb.getBlockState(pos);
			Block block = bs.getBlock();
			bs = bs.getActualState(mb, pos);
			if (block.canRenderInLayer(bs, layer)) {
				renderBlock(bs, pos, mb, Tessellator.getInstance().getBuffer());
			}
		}

		if (layer == BlockRenderLayer.TRANSLUCENT) {
			wr.sortVertexData(eye.x, eye.y, eye.z);
		}
		Tessellator.getInstance().draw();
	}

	public void renderBlock(@Nonnull IBlockState state, @Nonnull BlockPos pos, @Nonnull Multiblock mb, @Nonnull BufferBuilder worldRendererIn) {

		try {
			BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRendererDispatcher();
			EnumBlockRenderType type = state.getRenderType();
			if (type != EnumBlockRenderType.MODEL) {
				blockrendererdispatcher.renderBlock(state, pos, mb, worldRendererIn);
				return;
			}

			// We only want to change one param here, the check sides
			IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(state);
			state = state.getBlock().getExtendedState(state, mb, pos);
			blockrendererdispatcher.getBlockModelRenderer().renderModel(mb, ibakedmodel, state, pos, worldRendererIn, false);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// Hold errored TEs weakly, this may cause some dupe errors but will prevent spamming it every frame
	private final transient Set<TileEntity> erroredTiles = Collections.newSetFromMap(new WeakHashMap<>());

	private void doTileEntityRenderPass(Multiblock mb, Iterable<? extends BlockPos> blocks, final int pass) {
		mb.setWorld(mc.world);
		
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();
		
		ForgeHooksClient.setRenderPass(1);
		setGlStateForPass(1);
		
		for (BlockPos pos : blocks) {
			TileEntity te = mb.getTileEntity(pos);
			BlockPos relPos = new BlockPos(mc.player);
			if (te != null && !erroredTiles.contains(te) && te.shouldRenderInPass(pass)) {
				te.setWorld(mc.world);
				te.setPos(relPos.add(pos));

				try {
					TileEntityRendererDispatcher.instance.render(te, pos.getX(), pos.getY(), pos.getZ(), ClientTicker.partialTicks);
				} catch (Exception e) {
					erroredTiles.add(te);
					e.printStackTrace();
				}
			}
		}
		
		ForgeHooksClient.setRenderPass(-1);
		RenderHelper.disableStandardItemLighting();
	}

	private void setGlStateForPass(@Nonnull BlockRenderLayer layer) {
		int pass = layer == BlockRenderLayer.TRANSLUCENT ? 1 : 0;
		setGlStateForPass(pass);
	}

	private void setGlStateForPass(int layer) {
		GlStateManager.color(1, 1, 1);

		if (layer == 0) {
			GlStateManager.enableDepth();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		} else {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.depthMask(false);
		}
	}
}
