package vazkii.patchouli.client.book.template.test;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.renderer.GlStateManager;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;

public class TestCustomComponent implements ICustomComponent {

	int x, y;
	
	@SerializedName("text")
	@VariableHolder
	public String potato;
	
	@Override
	public void build(int componentX, int componentY, int pageNum) {
		this.x = componentX;
		this.y = componentY;
	}

	@Override
	public void render(IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		GlStateManager.rotate(30F, 0F, 0F, 1F);
		context.getFont().drawString(potato, 0, 0, 0x555555);
		
		GlStateManager.popMatrix();
	}

}
