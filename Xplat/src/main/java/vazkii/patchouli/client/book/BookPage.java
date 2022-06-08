package vazkii.patchouli.client.book;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

import java.util.ArrayList;
import java.util.List;

public abstract class BookPage {

	public transient Minecraft mc;
	public transient Font fontRenderer;
	public transient GuiBookEntry parent;

	public transient Book book;
	protected transient BookEntry entry;
	protected transient int pageNum;
	private transient List<Button> buttons;
	public transient int left, top;
	public transient JsonObject sourceObject;

	protected String type, flag, advancement, anchor;

	public void build(BookEntry entry, BookContentsBuilder builder, int pageNum) {
		this.book = entry.getBook();
		this.entry = entry;
		this.pageNum = pageNum;
	}

	public void onDisplayed(GuiBookEntry parent, int left, int top) {
		mc = parent.getMinecraft();
		book = parent.book;
		fontRenderer = mc.font;
		this.parent = parent;
		this.left = left;
		this.top = top;
		buttons = new ArrayList<>();
	}

	public boolean isPageUnlocked() {
		return advancement == null || advancement.isEmpty() || ClientAdvancements.hasDone(advancement);
	}

	public void onHidden(GuiBookEntry parent) {
		parent.removeDrawablesIn(buttons);
	}

	protected void addButton(Button button) {
		button.x += (parent.bookLeft + left);
		button.y += (parent.bookTop + top);
		buttons.add(button);
		parent.addRenderableWidget(button);
	}

	public void render(PoseStack ms, int mouseX, int mouseY, float pticks) {}

	public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
		return false;
	}

	public boolean canAdd(Book book) {
		return flag == null || flag.isEmpty() || PatchouliConfig.getConfigFlag(flag);
	}

	public String i18n(String text) {
		return book.i18n ? I18n.get(text) : text;
	}

	public Component i18nText(String text) {
		return book.i18n ? Component.translatable(text) : Component.literal(text);
	}
}
