package vazkii.patchouli.client.book.template.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.gson.annotations.SerializedName;

import net.minecraft.client.resources.I18n;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.template.TemplateComponent;

public class ComponentTooltip extends TemplateComponent {

	@SerializedName("tooltip")
	public String[] tooltipRaw;
	
	int width, height;
	
	transient List<String> tooltip;

	@Override
	public void onVariablesAvailable(Function<String, String> lookup) {
		super.onVariablesAvailable(lookup);
		for (int i = 0; i < tooltipRaw.length; i++) {
			tooltipRaw[i] = lookup.apply(tooltipRaw[i]);
		}
	}

	@Override
	public void onDisplayed(BookPage page, GuiBookEntry parent, int left, int top) {
		tooltip = new ArrayList<>();
		
		for(String s : tooltipRaw) {
			s = I18n.format(s).replaceAll("&", "\u00A7");
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
