package vazkii.patchouli.client.book.template.component.test;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;

public class TestCallback implements ICustomComponent {

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
	public void render(float pticks) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		GlStateManager.rotate(30F, 0F, 0F, 1F);
		Minecraft.getMinecraft().fontRenderer.drawString(potato, 0, 0, 0x555555);
		
		GlStateManager.popMatrix();
	}

}
