package vazkii.patchouli.client.book.template.component;

import java.lang.reflect.Constructor;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.TemplateComponent;

public class ComponentEntity extends TemplateComponent {

	@VariableHolder @SerializedName("entity")
	public String entityId;
	
	@SerializedName("render_size")
	float renderSize = 100;
	
	transient boolean errored;
	transient Constructor<Entity> constructor;
	transient Entity entity;
	transient float renderScale, offset;

	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		Class clazz = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityId)).getEntityClass();
		try {
			constructor = clazz.getConstructor(World.class);
		} catch(Exception e) {
			throw new RuntimeException("Could not find constructor for entity type " + entityId, e);
		}
	}
	
	@Override
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		loadEntity(page.mc.world);
	}
	
	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		if(errored)
			page.fontRenderer.drawStringWithShadow(I18n.translateToLocal("patchouli.gui.lexicon.loading_error"), x, y, 0xFF0000);
		
		if(entity != null)
			renderEntity(page.mc.world, ClientTicker.ticksInGame + pticks);
	}

	private void renderEntity(World world, float rotation) {
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
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
	
	private void loadEntity(World world) {
		if(!errored && (entity == null || entity.isDead)) {
			try {
				entity = constructor.newInstance(world);
				
				float entitySize = entity.width;
				if(entity.width < entity.height)
					entitySize = entity.height;
				entitySize = Math.max(1F, entitySize);
				
				renderScale = renderSize / entitySize * 0.8F;
				offset = Math.max(entity.height, entitySize) * 0.5F;
			} catch(Exception e) {
				errored = true;
				e.printStackTrace();
			}
		}
	}
	
}
