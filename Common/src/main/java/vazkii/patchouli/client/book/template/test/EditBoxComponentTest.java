package vazkii.patchouli.client.book.template.test;

import java.util.function.UnaryOperator;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.vehicle.Minecart;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookPage;

public class EditBoxComponentTest implements ICustomComponent{
	private transient int x, y, page;
	private Object editbox;

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void build(int componentX, int componentY, int pageNum) {
		x = componentX;
		y = componentY;
		page = pageNum;
		this.editbox = new EditBox(Minecraft.getInstance().font, x, y, 120, 20, new TextComponent(""));
	}

	@Override
	public void render(PoseStack ms, IComponentRenderContext context, float pticks, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDisplayed(IComponentRenderContext context) {
		this.editbox = new EditBox(Minecraft.getInstance().font, x, y, 100, 20, new TextComponent(""));
		context.addWidget((EditBox) editbox, page);
	}
	
	@Override
	public boolean overwriteKeyPressed(BookPage page, int keyCode, int scanCode, int modifiers) {
		if (((EditBox)editbox).isFocused()) {
			((EditBox)editbox).keyPressed(keyCode, scanCode, modifiers);
			return true;
		}
		return ICustomComponent.super.overwriteKeyPressed(page, keyCode, scanCode, modifiers);
	}

}
