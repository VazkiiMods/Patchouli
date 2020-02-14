package vazkii.patchouli.client.book.gui.button;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.util.TextFormat;
import vazkii.patchouli.client.base.ClientTicker;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;

public class GuiButtonEntry extends ButtonWidget {
	
	private static final int ANIM_TIME = 5;

	private final GuiBook parent;
	private final BookEntry entry;
	private float timeHovered;

	public GuiButtonEntry(GuiBook parent, int x, int y, BookEntry entry, ButtonWidget.PressAction onPress) {
		super(x, y, GuiBook.PAGE_WIDTH, 10, entry.getName(), onPress);
		this.parent = parent;
		this.entry = entry;
	}
	
	@Override
	public void renderButton(int mouseX, int mouseY, float partialTicks) {
		if(active) {
			if(isHovered())
				timeHovered = Math.min(ANIM_TIME, timeHovered + ClientTicker.delta);
			else timeHovered = Math.max(0, timeHovered - ClientTicker.delta);
			
			float time = Math.max(0, Math.min(ANIM_TIME, timeHovered + (isHovered() ? partialTicks : -partialTicks)));
			float widthFract = time / ANIM_TIME;
			boolean locked = entry.isLocked();
			
			RenderSystem.scalef(0.5F, 0.5F, 0.5F);
			DrawableHelper.fill(x * 2, y * 2, (x + (int) ((float) width * widthFract)) * 2, (y + height) * 2, 0x22000000);
			RenderSystem.enableBlend();

			if(locked) {
				RenderSystem.color4f(1F, 1F, 1F, 0.7F);
				GuiBook.drawLock(parent.book, x * 2 + 2, y * 2 + 2); 
			} else
				entry.getIcon().render(x * 2 + 2, y * 2 + 2);
			
			RenderSystem.scalef(2F, 2F, 2F);

			String name = (entry.isPriority() ? TextFormat.ITALIC : "") + entry.getName();
			if(locked) {
				name = I18n.translate("patchouli.gui.lexicon.locked");
			}
			int color = getColor();
			entry.getBook().getFont().draw(name, x + 12, y, color);
			
			if(!entry.isLocked())
				GuiBook.drawMarking(parent.book, x + width - 5, y + 1, entry.hashCode(), entry.getReadState());
		}
	}
	
	private int getColor() {
		if (entry.isSecret()) 
			return 0xAA000000 | (parent.book.textColor & 0x00FFFFFF);
		if (entry.isLocked())
			return 0x77000000 | (parent.book.textColor & 0x00FFFFFF);
		return entry.getEntryColor();
	}
	
	@Override
    public void playDownSound(SoundManager soundHandlerIn) {
		if(entry != null && !entry.isLocked())
			GuiBook.playBookFlipSound(parent.book);
	}
	
	public BookEntry getEntry() {
		return entry;
	}

}
