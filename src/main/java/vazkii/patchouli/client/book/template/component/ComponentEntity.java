package vazkii.patchouli.client.book.template.component;

import java.lang.reflect.Constructor;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.fixes.EntityId;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.PageEntity;
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
	transient NBTTagCompound nbt;

	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		String nbtStr = "";
		int nbtStart = entityId.indexOf("{");
		if(nbtStart > 0) {
			nbtStr = entityId.substring(nbtStart).replaceAll("'", "\"");
			entityId = entityId.substring(0, nbtStart);
			try {
				nbt = JsonToNBT.getTagFromJson(nbtStr);
			} catch(NBTException e) {
				e.printStackTrace();
				nbt = null;
			}
		}
		
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
			renderEntity(page.mc.world, ClientTicker.total);
	}

	private void renderEntity(World world, float rotation) {
		PageEntity.renderEntity(entity, world, x, y, rotation, renderScale, offset);
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
				
				renderScale = renderSize / entitySize * 0.8F;
				offset = Math.max(entity.height, entitySize) * 0.5F;
			} catch(Exception e) {
				errored = true;
				e.printStackTrace();
			}
		}
	}
	
}
