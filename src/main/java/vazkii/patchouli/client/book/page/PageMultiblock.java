// TODO bring back with multiblocks

//package vazkii.patchouli.client.book.page;
//
//import org.lwjgl.opengl.GL11;
//
//import net.minecraft.block.state.IBlockState;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiButton;
//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.client.renderer.BlockRendererDispatcher;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.client.renderer.texture.TextureMap;
//import net.minecraft.util.ResourceLocation;
//import vazkii.alquimia.client.handler.MultiblockVisualizationHandler;
//import vazkii.alquimia.common.multiblock.ModMultiblocks;
//import vazkii.alquimia.common.multiblock.Multiblock;
//import vazkii.alquimia.common.multiblock.Multiblock.StateMatcher;
//import vazkii.arl.util.ClientTicker;
//import vazkii.patchouli.client.base.PersistentData;
//import vazkii.patchouli.client.base.PersistentData.DataHolder.Bookmark;
//import vazkii.patchouli.client.book.BookEntry;
//import vazkii.patchouli.client.book.gui.GuiLexicon;
//import vazkii.patchouli.client.book.gui.GuiLexiconEntry;
//import vazkii.patchouli.client.book.gui.button.GuiButtonLexiconEye;
//import vazkii.patchouli.client.book.page.abstr.PageWithText;
//
//public class PageMultiblock extends PageWithText {
//
//	String name;
//	String multiblock;
//
//	transient Multiblock multiblockObj;
//	transient GuiButton visualizeButton;
//
//	@Override
//	public void build(BookEntry entry, int pageNum) {
//		multiblockObj = ModMultiblocks.MULTIBLOCKS.get(new ResourceLocation(multiblock));
//	}
//
//	@Override
//	public void onDisplayed(GuiLexiconEntry parent, int left, int top) {
//		super.onDisplayed(parent, left, top);
//
//		adddButton(visualizeButton = new GuiButtonLexiconEye(parent, 12, 97));
//	}
//
//	@Override
//	public int getTextHeight() {
//		return 115;
//	}
//	
//	@Override
//	public void render(int mouseX, int mouseY, float pticks) {
//		int x = GuiLexicon.PAGE_WIDTH / 2 - 53;
//		int y = 7;
//		GlStateManager.enableBlend();
//		GuiLexicon.drawFromTexture(x, y, 405, 149, 106, 106);
//		
//		parent.drawCenteredStringNoShadow(name, GuiLexicon.PAGE_WIDTH / 2, 0, book.headerColor);
//
//		if(multiblockObj != null)
//			renderMultiblock();
//		
//		super.render(mouseX, mouseY, pticks);
//	}
//
//	@Override
//	protected void onButtonClicked(GuiButton button) {
//		if(button == visualizeButton) {
//			String entryKey = parent.getEntry().getResource().toString();
//			Bookmark bookmark = new Bookmark(entryKey, pageNum / 2);
//			MultiblockVisualizationHandler.setMultiblock(multiblockObj, name, bookmark, true);
//			parent.addBookmarkButtons();
//			
//			if(!PersistentData.data.clickedVisualize) {
//				PersistentData.data.clickedVisualize = true;
//				PersistentData.save();
//			}
//				
//		}
//	}
//
//	private void renderMultiblock() {
//		float maxX = 90;
//		float maxY = 90;
//		float diag = (float) Math.sqrt(multiblockObj.sizeX * multiblockObj.sizeX + multiblockObj.sizeZ * multiblockObj.sizeZ);
//		float height = multiblockObj.sizeY;
//		float scaleX = maxX / diag;
//		float scaleY = maxY / height;
//		float scale = -Math.min(scaleX, scaleY);
//		
//		int xPos = GuiLexicon.PAGE_WIDTH / 2;
//		int yPos = 60;
//		GlStateManager.pushMatrix();
//		GlStateManager.translate(xPos, yPos, 100);
//		GlStateManager.scale(scale, scale, scale);
//		GlStateManager.translate(-(float) multiblockObj.sizeX / 2, -(float) multiblockObj.sizeY / 2, 0);
//
//		GlStateManager.rotate(-30F, 1F, 0F, 0F);
//		
//		float offX = (float) -multiblockObj.sizeX / 2;
//		float offZ = (float) -multiblockObj.sizeZ / 2 + 1;
//
//		float time = parent.ticksInBook * 0.5F;
//		if(!GuiScreen.isShiftKeyDown())
//			time += ClientTicker.partialTicks;
//		GlStateManager.translate(-offX, 0, -offZ);
//		GlStateManager.rotate(time, 0F, 1F, 0F);
//		GlStateManager.rotate(45F, 0F, 1F, 0F);
//		GlStateManager.translate(offX, 0, offZ);
//		
//		for(int x = 0; x < multiblockObj.sizeX; x++)
//			for(int y = 0; y < multiblockObj.sizeY; y++)
//				for(int z = 0; z < multiblockObj.sizeZ; z++)
//					renderElement(multiblockObj, x, y, z);
//		GlStateManager.popMatrix();
//	}
//
//	private void renderElement(Multiblock mb, int x, int y, int z) {
//		StateMatcher matcher = mb.stateTargets[x][y][z];
//		IBlockState state = matcher.displayState;
//		if(state == null)
//			return;
//
//		BlockRendererDispatcher brd = Minecraft.getMinecraft().getBlockRendererDispatcher();
//		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
//		
//		GlStateManager.pushMatrix();
//		GlStateManager.translate(x, y, z);
//		GlStateManager.color(1F, 1F, 1F, 1F);
//		GlStateManager.enableBlend();
//		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
//		brd.renderBlockBrightness(state, 1.0F);
//		
//		GlStateManager.color(1F, 1F, 1F, 1F);
//		GlStateManager.enableDepth();
//		GlStateManager.popMatrix();
//	}
//
//}
