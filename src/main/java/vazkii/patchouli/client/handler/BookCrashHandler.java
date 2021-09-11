package vazkii.patchouli.client.handler;

import net.minecraft.CrashReportDetail;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.GuiBookEntryList;
import vazkii.patchouli.common.book.Book;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class BookCrashHandler implements CrashReportDetail<String> {
	private static final String INDENT = "\n\t\t";

	public String getLabel() {
		return "Patchouli open book context";
	}

	@Override
	public String call() {
		Screen screen = Minecraft.getInstance().screen;
		if (!(screen instanceof GuiBook)) {
			return "n/a";
		}
		GuiBook gui = (GuiBook) screen;
		Book book = gui.book;
		StringBuilder builder = new StringBuilder(INDENT);

		builder.append("Open book: ").append(book.getId());
		if (gui instanceof GuiBookEntry) {
			builder.append(INDENT).append("Current entry: ").append(((GuiBookEntry) gui).getEntry().getId());
		} else if (gui instanceof GuiBookEntryList) {
			builder.append(INDENT).append("Search query: ").append(((GuiBookEntryList) gui).getSearchQuery());
		}
		builder.append(INDENT).append("Current page spread: ").append(gui.getSpread());
		if (book.getContents().isErrored()) {
			Exception ex = book.getContents().getException();
			builder.append(INDENT).append("Book loading error: ");
			try (StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw)) {
				ex.printStackTrace(pw);
				builder.append(sw.toString().replaceAll("\n", INDENT));
			} catch (IOException ignored) {}
		}
		return builder.toString();
	}
}
