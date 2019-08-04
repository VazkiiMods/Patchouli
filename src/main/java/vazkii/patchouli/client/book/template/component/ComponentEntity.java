package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.page.PageEntity;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.common.util.EntityUtil;
import vazkii.patchouli.common.util.EntityUtil.EntityCreator;

public class ComponentEntity extends TemplateComponent {

	@VariableHolder @SerializedName("entity")
	public String entityId;
	
	@SerializedName("render_size")
	float renderSize = 100;
	
	boolean rotate = true;
	@SerializedName("default_rotation")
	float defaultRotation = -45f;
	
	transient boolean errored;
	transient Entity entity;
	transient EntityCreator creator;
	transient float renderScale, offset;

	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		creator = EntityUtil.loadEntity(entityId);
	}
	
	@Override
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		loadEntity(page.mc.world);
	}
	
	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		if(errored)
			page.fontRenderer.drawStringWithShadow(I18n.format("patchouli.gui.lexicon.loading_error"), x, y, 0xFF0000);
		
		if(entity != null)
			renderEntity(page.mc.world, rotate ?  ClientTicker.total : defaultRotation);
	}

	private void renderEntity(World world, float rotation) {
		PageEntity.renderEntity(entity, world, x, y, rotation, renderScale, offset);
	}
	
	private void loadEntity(World world) {
		if(!errored && (entity == null || !entity.isAlive())) {
			try {
				entity = creator.create(world);
				float width = entity.getWidth();
				float height = entity.getHeight();
				
				float entitySize = width;
				if(width < height)
					entitySize = height;
				entitySize = Math.max(1F, entitySize);
				
				renderScale = renderSize / entitySize * 0.8F;
				offset = Math.max(height, entitySize) * 0.5F;
			} catch(Exception e) {
				errored = true;
				e.printStackTrace();
			}
		}
	}
	
}
