package vazkii.patchouli.client.book;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.common.base.PatchouliConfig;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;
import vazkii.patchouli.common.util.SerializationUtil;

public abstract class BookPage {

	protected transient Minecraft mc;
	protected transient FontRenderer fontRenderer;
	protected transient GuiBookEntry parent;

	public transient Book book;
	protected transient BookEntry entry;
	protected transient int pageNum;
	private transient List<GuiButton> buttons;
	public transient int left, top;
	
	String type, flag;
	
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
		buttons = new ArrayList();
	}
	
	public void onHidden(GuiBookEntry parent) {
		parent.getButtonList().removeAll(buttons);
	}
	
	protected void adddButton(GuiButton button) {
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
	
	public void renderItem(int x, int y, int mouseX, int mouseY, ItemStack stack) {
		if(stack == null || stack.isEmpty())
			return;
		
		RenderHelper.enableGUIStandardItemLighting();
		mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		mc.getRenderItem().renderItemOverlays(fontRenderer, stack, x, y);
		
		if(parent.isMouseInRelativeRange(mouseX, mouseY, x, y, 16, 16))
			parent.setTooltipStack(stack);
	}
	
	public void renderIngredient(int x, int y, int mouseX, int mouseY, Ingredient ingr) {
		ItemStack[] stacks = ingr.getMatchingStacks();
		if(stacks.length > 0)
			renderItem(x, y, mouseX, mouseY, stacks[(parent.ticksInBook / 20) % stacks.length]);
	}
	
	public boolean canAdd() {
		return flag == null || flag.isEmpty() || PatchouliConfig.getConfigFlag(flag);
	}
	
}
