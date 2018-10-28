package vazkii.patchouli.client.book.template.component;

import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.structure.template.Template;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.template.TemplateComponent;
import vazkii.patchouli.client.book.template.TemplateComponent.VariableHolder;
import vazkii.patchouli.common.util.ItemStackUtil;

public class ComponentItemStack extends TemplateComponent {

	@VariableHolder 
	public String item;
	
	boolean framed, linkedRecipe;
	
	transient ItemStack stack;
	
	@Override
	public void build(BookPage page, BookEntry entry, int pageNum) {
		stack = ItemStackUtil.loadStackFromString(item);
		
		if(!stack.isEmpty())
			entry.addRelevantStack(stack, pageNum);
	}
	
	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		super.render(page, mouseX, mouseY, pticks);
		
		if(framed) {
			page.mc.renderEngine.bindTexture(page.book.craftingResource);
			page.parent.drawModalRectWithCustomSizedTexture(x - 4, y - 4, 83, 71, 24, 24, 128, 128);
		}
		
		page.renderItem(x, y, mouseX, mouseY, stack);
	}
	
}
