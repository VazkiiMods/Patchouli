package vazkii.patchouli.client.book.template.component;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.common.util.ItemStackUtil;

public class ComponentItemStack extends TemplateComponent {

	@VariableHolder 
	public String item;
	
	boolean framed;
	@SerializedName("link_recipe")
	boolean linkedRecipe;
	
	transient ItemStack stack;
	
	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		stack = ItemStackUtil.loadStackFromString(item);
		
		if(linkedRecipe && !stack.isEmpty())
			entry.addRelevantStack(stack, pageNum);
	}
	
	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		super.render(page, mouseX, mouseY, pticks);
		
		if(framed) {
			GlStateManager.enableBlend();
			GlStateManager.color(1F, 1F, 1F, 1F);
			page.mc.renderEngine.bindTexture(page.book.craftingResource);
			Gui.drawModalRectWithCustomSizedTexture(x - 4, y - 4, 83, 71, 24, 24, 128, 128);
		}
		
		page.parent.renderItemStack(x, y, mouseX, mouseY, stack);
	}
	
}
