package vazkii.patchouli.client.book;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import vazkii.patchouli.client.base.ClientAdvancements;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;

public abstract class BookPage {

	public transient Minecraft mc;
	public transient FontRenderer fontRenderer;
	public transient GuiBookEntry parent;

	public transient Book book;
	protected transient BookEntry entry;
	protected transient int pageNum;
	private transient List<GuiButton> buttons;
	public transient int left, top;
	public transient JsonObject sourceObject;
	
	protected String type, flag, advancement, anchor;
	
	public void build(BookEntry entry, int pageNum) {
		this.book = entry.book;
		this.entry = entry;
		this.pageNum = pageNum;
	}
	
	public void onDisplayed(GuiBookEntry parent, int left, int top) { 
		mc = parent.mc;
		book = parent.book;
		fontRenderer = mc.fontRenderer;
		this.parent = parent;
		this.left = left;
		this.top = top;
		buttons = new ArrayList<>();
	}

	public boolean isPageUnlocked() {
		return advancement == null || advancement.isEmpty() || ClientAdvancements.hasDone(advancement);
	}
	
	public void onHidden(GuiBookEntry parent) {
		parent.getButtonList().removeAll(buttons);
	}
	
	protected void addButton(GuiButton button) {
		button.x += (parent.bookLeft + left);
		button.y += (parent.bookTop + top);
		buttons.add(button);
		parent.getButtonList().add(button);
	}
	
	public void render(int mouseX, int mouseY, float pticks) { }
	public void mouseClicked(int mouseX, int mouseY, int mouseButton) { }
	protected void onButtonClicked(GuiButton button) { }
	
	public final boolean interceptButton(GuiButton button) {
		if(buttons.contains(button)) {
			onButtonClicked(button);
			return true;
		}
		
		return false;
	}
	
	public boolean canAdd(Book book) {
		return flag == null || flag.isEmpty() || PatchouliConfig.getConfigFlag(flag);
	}
	
}
