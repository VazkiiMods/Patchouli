package vazkii.patchouli.client.book.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import vazkii.patchouli.Patchouli;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.book.Book;

public class BookTextRenderer {
	
	private static final int LINK_COLOR = 0x0000EE;
	private static final int LINK_COLOR_HOVER = 0x8800EE;
	
	// TODO support custom macros
	private static final Map<String, String> MACROS = new HashMap() {{
		put("$(obf)", "$(k)");
		put("$(bold)", "$(l)");
		put("$(strike)", "$(m)");
		put("$(italic)", "$(o)");
		put("$(italics)", "$(o)");
		put("$(list", "$(li"); //  The lack of ) is intended
		put("$(reset)", "$()");
		put("$(clear)", "$()");
		put("$(2br)", "$(br2)");
		put("$(p)", "$(br2)");
		
		put("/$", "$()");
		put("<br>", "$(br)");
		
		put("$(item)", "$(#b0b)");
		put("$(thing)", "$(#490)");
	}};

	final Book book;
	final GuiBook gui;
	final FontRenderer font;
	final String text;
	final int x, y, width;
	final int spaceWidth;
	final int lineHeight;
	final boolean defaultUnicode;
	
	int currX, currY, currLen, currColor, prevColor;
	String currCodes, currHref;
	boolean externalHref;
	List<Word> currCluster;
	
	List<Word> words;
	
	public BookTextRenderer(GuiBook gui, String text, int x, int y) {
		this(gui, text, x, y, GuiBook.PAGE_WIDTH, GuiBook.TEXT_LINE_HEIGHT);
	}
	
	public BookTextRenderer(GuiBook gui, String text, int x, int y, int width, int lineHeight) {
		this.book = gui.book;
		this.gui = gui;
		this.font = gui.mc.fontRenderer;
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.spaceWidth = font.getStringWidth(" ");
		this.lineHeight = lineHeight;
		this.defaultUnicode = font.getUnicodeFlag();
		
		build();
	}
	
	private void build() {
		font.setUnicodeFlag(true);
		
		words = new LinkedList<>();
		
		String actualText = text;
		if(actualText == null)
			actualText = "[ERROR]";
		
		for(String key : MACROS.keySet())
			actualText = actualText.replace(key, MACROS.get(key));
		
		actualText = actualText.replaceAll(" ", "\0 ").replaceAll("(\\$\\(.*?\\))", " $1 ");
		String[] tokens = actualText.split(" ");
		
		currX = x;
		currY = y;
		currLen = 0;
		currColor = 0;
		prevColor = 0;
		currCodes = "";
		currHref = "";
		currCluster = null;
		
		for(String s : tokens) {
			boolean space = s.contains("\0");
			
			try {
				s = buildCommand(s);
			} catch(Exception e) { 
				s = "[ERROR]";
			}
			
			if(s.isEmpty())
				continue;
			
			s = s.replaceAll("\0", "");
			
			int strWidth = font.getStringWidth(currCodes + s) + (space ? spaceWidth : 0);
			currLen += strWidth;
			if(currLen > width) {
				currLen = strWidth;
				currX = x;
				currY += lineHeight;
			}
			
			Word word = new Word(font, currX, currY, strWidth, s, currColor, currCodes, currHref, currCluster);
			words.add(word);
			if(currCluster != null)
				currCluster.add(word);
			
			currX += strWidth;
		}
		
		font.setUnicodeFlag(defaultUnicode);
	}
	
	private String buildCommand(String s) {
		if(s.matches("^\\$\\((.*?)\\)$")) { // Special codes
			String cmd = s.substring(2, s.length() - 1);
			boolean endingExternal = false;
			
			if(cmd.isEmpty()) { // Remove formatting
				endingExternal = !currHref.isEmpty() && externalHref;
				currColor = 0;
				currCodes = "";
				currHref = "";
				currCluster = null;
				externalHref = false;
			}
			
			else if(cmd.matches("br|br2")) { // Line break
				currLen = 0;
				currX = x;
				currY += (cmd.contains("2") ? lineHeight * 2 : lineHeight);
			}
			
			else if(cmd.matches("li\\d?")) { // List Element
				char c = cmd.length() > 2 ? cmd.charAt(2) : '1';
				int dist = Character.isDigit(c) ? Character.digit(c, 10) : 1;
				int pad = dist * 4;
				char bullet = dist % 2 == 0 ? '\u25E6' : '\u2022';
				if(currY > y || currX > x)
					currY += lineHeight;
				currLen = pad;
				currX = x + pad;
				
				return TextFormatting.BLACK + "" + bullet + " ";
			}
			
			else if(cmd.startsWith("#") && (cmd.length() == 4 || cmd.length() == 7)) { // Hex colors
				String parse = cmd.substring(1);
				if(parse.length() == 3)
					parse = "" + parse.charAt(0) + parse.charAt(0) + parse.charAt(1) + parse.charAt(1) + parse.charAt(2) + parse.charAt(2);
				try {
					currColor = Integer.parseInt(parse, 16);
				} catch(NumberFormatException e) {
					currColor = 0;
				}
			}
			
			else if(cmd.matches("^[0123456789abcdef]$")) // Vanilla colors
				currColor = font.getColorCode(cmd.charAt(0));
			
			else if(cmd.matches("^[klmnor]$")) // Vanilla codes
				currCodes = "\u00A7" + cmd;
			
			else if(cmd.startsWith("l:")) { // Links
				String nextHref = cmd.substring(2);
				if(!nextHref.equals(currHref))
					currCluster = new LinkedList();
				
				currHref = cmd.substring(2);
				prevColor = currColor;
				currColor = LINK_COLOR;
				externalHref = currHref.matches("^https?\\:.*");
			} 
			else if(cmd.equals("/l")) { // Link breaks
				endingExternal = !currHref.isEmpty() && externalHref;
				currHref = "";
				externalHref = false;
				currColor = prevColor;
				currCluster = null;
			}
			
			if(endingExternal)
				return TextFormatting.GRAY + "\u21AA";
			
			return "";
		}
		
		return s;
	}
	
	public void render(int mouseX, int mouseY) {
		font.setUnicodeFlag(true);
		words.forEach(word -> word.render(mouseX, mouseY));
		font.setUnicodeFlag(defaultUnicode);
	}
	
	public void click(int mouseX, int mouseY, int mouseButton) {
		words.forEach(word -> word.click(mouseX, mouseY, mouseButton));
	}
	
	class Word {
		
		final FontRenderer font;
		final int x, y, width, height;
		final String text;
		final int color;
		final String codes;
		final ResourceLocation href;
		final boolean externalHref;
		final List<Word> linkCluster;
		
		Word(FontRenderer font, int x, int y, int width, String text, int color, String codes, String href, List<Word> linkCluster) {
			this.font = font;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = 8;
			this.text = text;
			this.color = color;
			this.codes = codes;
			this.href = href == null ? null : new ResourceLocation(href.contains(":") ? href : (book.getModNamespace() + ":" +  href));
			this.externalHref = href != null && this.href.getResourceDomain().matches("https?");
			this.linkCluster = linkCluster;
		}
		
		public void render(int mouseX, int mouseY) {
			String renderTarget = codes + text;
			int renderColor = color;
			if(isClusterHovered(mouseX, mouseY) && href != null) {
				renderColor = LINK_COLOR_HOVER;
				
				if(externalHref)
					gui.setTooltip(I18n.translateToLocal("patchouli.gui.lexicon.external_link"));
				else {
					BookEntry entry = getHrefEntry();
					if(entry != null) 
						gui.setTooltip(entry.isLocked() ? (TextFormatting.GRAY +  I18n.translateToLocal("patchouli.gui.lexicon.locked")) : entry.getName());
					else gui.setTooltip("BAD LINK " + href);
				}
			}
			
			font.drawString(renderTarget, x, y, renderColor);
		}
		
		public void click(int mouseX, int mouseY, int mouseButton) {
			if(href != null && mouseButton == 0 && isHovered(mouseX, mouseY))
				onClicked();
		}
		
		private void onClicked() {
			if(href != null) {
				if(externalHref)
					GuiBook.openWebLink(href.toString());
				else {
					BookEntry entry = getHrefEntry();
					if(entry != null)
						gui.displayLexiconGui(new GuiBookEntry(book, entry), true);
				}
			}
		}
		
		private BookEntry getHrefEntry() {
			return book.contents.entries.get(href);
		}
		
		private boolean isHovered(int mouseX, int mouseY) {
			return gui.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height);
		}
		
		private boolean isClusterHovered(int mouseX, int mouseY) {
			if(linkCluster == null)
				return false;
						
			for(Word w : linkCluster)
				if(w.isHovered(mouseX, mouseY))
					return true;
			
			return false;
		}
		
	}
	
}

