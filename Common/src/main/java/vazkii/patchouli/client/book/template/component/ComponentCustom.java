package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;
import com.mojang.blaze3d.vertex.PoseStack;

import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookContentsBuilder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.common.util.SerializationUtil;

import java.util.function.UnaryOperator;

public class ComponentCustom extends TemplateComponent {

	@SerializedName("class") String clazz;

	private transient ICustomComponent callbacks;

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup) {
		super.onVariablesAvailable(lookup);
		try {
			Class<?> classObj = Class.forName(clazz);
			callbacks = (ICustomComponent) SerializationUtil.RAW_GSON.fromJson(sourceObject, classObj);
			callbacks.onVariablesAvailable(lookup);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create custom component " + clazz, e);
		}
	}

	@Override
	public void build(BookContentsBuilder builder, BookPage page, BookEntry entry, int pageNum) {
		callbacks.build(x, y, pageNum);
	}

	@Override
	public void render(PoseStack ms, BookPage page, int mouseX, int mouseY, float pticks) {
		callbacks.render(ms, page.parent, pticks, mouseX, mouseY);
	}

	@Override
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		callbacks.onDisplayed(parent);
	}

	@Override
	public boolean mouseClicked(BookPage page, double mouseX, double mouseY, int mouseButton) {
		return callbacks.mouseClicked(page.parent, mouseX, mouseY, mouseButton);
	}
	
	@Override
	public boolean overwriteKeyPressed(BookPage page, int keyCode, int scanCode, int modifiers) {
		return callbacks.overwriteKeyPressed(page, keyCode, scanCode, modifiers);
	}

}
