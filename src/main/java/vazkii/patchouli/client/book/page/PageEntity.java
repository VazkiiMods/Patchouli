package vazkii.patchouli.client.book.page;

import java.lang.reflect.Constructor;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;

public class PageEntity extends PageWithText {

	@SerializedName("entity")
	public String entityId;
	
	float scale = 1F;
	@SerializedName("offset")
	float extraOffset = 0F;
	String name;
	
	boolean rotate = true;
	@SerializedName("default_rotation")
	float defaultRotation = -45f;

	transient boolean errored;
	transient Constructor<? extends Entity> constructor;
	transient Entity entity;
	transient float renderScale, offset;
	transient NBTTagCompound nbt;

	@Override
	public void build(BookEntry entry, int pageNum) {
		super.build(entry, pageNum);
		
		String nbtStr;
		int nbtStart = entityId.indexOf("{");
		if(nbtStart > 0) {
			nbtStr = entityId.substring(nbtStart).replaceAll("([^\\\\])'", "$1\"").replaceAll("\\\\'", "'");
			entityId = entityId.substring(0, nbtStart);
			try {
				nbt = JsonToNBT.getTagFromJson(nbtStr);
			} catch(NBTException e) {
				e.printStackTrace();
				nbt = null;
			}
		}

		EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityId));
		if (entityEntry == null)
			throw new RuntimeException("Could not find entity: " + entityId);

		Class<? extends Entity> clazz = entityEntry.getEntityClass();
		try {
			constructor = clazz.getConstructor(World.class);
		} catch(Exception e) {
			throw new RuntimeException("Could not find constructor for entity type " + entityId, e);
		}
	}
	
	@Override
	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		super.onDisplayed(parent, left, top);
		
		loadEntity(parent.mc.world);
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

		if(errored)
			fontRenderer.drawStringWithShadow(I18n.format("patchouli.gui.lexicon.loading_error"), 58, 60, 0xFF0000);
		
		if(entity != null)
			renderEntity(parent.mc.world, rotate ? ClientTicker.total : defaultRotation);
		
		super.render(mouseX, mouseY, pticks);
	}
	
	private void renderEntity(World world, float rotation) {
		renderEntity(entity, world, 58, 60, rotation, renderScale, offset);
	}	
	
	public static void renderEntity(Entity entity, World world, float x, float y, float rotation, float renderScale, float offset) {
		entity.world = world;
		
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.color(1F, 1F, 1F);		
		GlStateManager.translate(x, y, 50.0F);
		GlStateManager.scale(-renderScale, renderScale, renderScale);
		GlStateManager.translate(0F, offset, 0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		Minecraft.getMinecraft().getRenderManager().playerViewY = 180.0F;
		Minecraft.getMinecraft().getRenderManager().renderEntity(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
	
	private void loadEntity(World world) {
		if(!errored && (entity == null || entity.isDead)) {
			try {
				entity = constructor.newInstance(world);
				
				if(nbt != null)
					entity.readFromNBT(nbt);
				
				float entitySize = entity.width;
				if(entity.width < entity.height)
					entitySize = entity.height;
				entitySize = Math.max(1F, entitySize);
				
				renderScale = 100F / entitySize * 0.8F * scale;
				offset = Math.max(entity.height, entitySize) * 0.5F + extraOffset;
				
				if(name == null || name.isEmpty())
					name = entity.getName();
			} catch(Exception e) {
				errored = true;
				e.printStackTrace();
			}
		}
	}
	
}
