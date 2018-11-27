package vazkii.patchouli.client.book.template.component;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.TemplateComponent;

public class ComponentTooltip extends TemplateComponent {

	@VariableHolder
	@SerializedName("tooltip")
	public String[] tooltipRaw;
	
	int width, height;
	
	transient List<String> tooltip;
	
	@Override
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		tooltip = new ArrayList();
		
		for(String s : tooltipRaw) {
			s = I18n.translateToLocal(s).replaceAll("&", "\u00A7");
			if(!s.isEmpty())
				tooltip.add(s);
		}
	}
	
	@Override
	public void render(BookPage page, int mouseX, int mouseY, float pticks) {
		if(page.parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height))
			page.parent.setTooltip(tooltip);
	}
	
}
