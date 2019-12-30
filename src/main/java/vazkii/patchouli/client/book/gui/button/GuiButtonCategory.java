package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.TextFormat;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookCategory;
import vazkii.patchouli.client.book.BookIcon;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonCategory extends ButtonWidget {

	private static final int ANIM_TIME = 5;

	GuiBook parent;
	BookCategory category;
	BookIcon icon;
	String name;
	int u, v;
	float timeHovered;

	public GuiButtonCategory(GuiBook parent, int x, int y, BookCategory category, ButtonWidget.PressAction onPress) {
		this(parent, x, y, category.getIcon(), category.getName(), onPress);
		this.category = category;
	}	

	public GuiButtonCategory(GuiBook parent, int x, int y, BookIcon icon, String name, ButtonWidget.PressAction onPress) {
		super(parent.bookLeft + x, parent.bookTop + y, 20, 20, "", onPress);
		this.parent = parent;
		this.u = x;
		this.v = y;
		this.icon = icon;
		this.name = name;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if(active && visible) {
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;

			if(isHovered)
				timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
			else timeHovered = Math.max(0, timeHovered - ClientTicker.delta);

			float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (isHovered ? partialTicks : -partialTicks)));
			float transparency = 0.5F - (time / ANIM_TIME) * 0.5F;
			boolean locked = category != null && category.isLocked();

			if(locked) {
				RenderSystem.color4f(1F, 1F, 1F, 0.7F);
				GuiBook.drawLock(parent.book, x + 2, y + 2); 
			} else
				icon.render(x + 2, y + 2);

			RenderSystem.pushMatrix();
			RenderSystem.enableBlend();
			RenderSystem.color4f(1F, 1F, 1F, transparency);
			RenderSystem.translatef(0, 0, 200);
			GuiBook.drawFromTexture(parent.book, x, y, u, v, width, height);
			RenderSystem.color4f(1F, 1F, 1F, 1F);

			if(category != null && !category.isLocked())
				GuiBook.drawMarking(parent.book, x, y, 0, category.getReadState());
			RenderSystem.popMatrix();

			if(isHovered)
				parent.setTooltip(locked ? (TextFormat.GRAY + I18n.translate("patchouli.gui.lexicon.locked")) : name);
		}
	}

	@Override
	public void playDownSound(SoundManager soundHandlerIn) {
		if(category != null && !category.isLocked())
			GuiBook.playBookFlipSound(parent.book);
	}

	public BookCategory getCategory() {
		return category;
	}

}
