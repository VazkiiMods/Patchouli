package vazkii.patchouli.client.book.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import vazkii.patchouli.client.base.PersistentData;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.button.*;
import vazkii.patchouli.client.gui.GuiAdvancementsExt;
import vazkii.patchouli.common.book.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static vazkii.patchouli.client.book.gui.GuiBookEntryList.ENTRIES_IN_FIRST_PAGE;
import static vazkii.patchouli.client.book.gui.GuiBookEntryList.ENTRIES_PER_PAGE;

// A lot of this is a copy of GuiBookLanding
public class GuiBookPamphletLanding extends GuiBook {
    BookTextRenderer text;
    final List<Button> entryButtons = new ArrayList<>();
    List<BookEntry> allEntries;

    public GuiBookPamphletLanding(Book book) {
        super(book, new TranslatableComponent(book.name));
    }

    @Override
    public void init() {
        super.init();

        // Left-hand side should be the same as the normal landing
        // just copy GuiBookLanding lmao
        text = new BookTextRenderer(this, new TranslatableComponent(book.landingText), LEFT_PAGE_X, TOP_PADDING + 25);

        boolean disableBar = !book.showProgress || !book.advancementsEnabled();

        int x = bookLeft + (disableBar ? 25 : 20);
        int y = bookTop + FULL_HEIGHT - (disableBar ? 25 : 62);
        int dist = 15;
        int pos = 0;

        // Resize
        if (maxScale > 2) {
            addRenderableWidget(new GuiButtonBookResize(this, x + (pos++) * dist, y, true, this::handleButtonResize));
        }

        // History
        addRenderableWidget(new GuiButtonBookHistory(this, x + (pos++) * dist, y, this::handleButtonHistory));

        // Advancements
        if (book.advancementsTab != null) {
            addRenderableWidget(new GuiButtonBookAdvancements(this, x + (pos++) * dist, y, this::handleButtonAdvancements));
        }

        if (Minecraft.getInstance().player.isCreative()) {
            addRenderableWidget(new GuiButtonBookEdit(this, x + (pos++) * dist, y, this::handleButtonEdit));
        }

        // And for the right hand side, take some notes from GuiBookEntryList
        allEntries = new ArrayList<>(book.getContents().entries.values());
        allEntries.removeIf(BookEntry::shouldHide);
        Collections.sort(allEntries);
        buildEntryButtons();
    }

    @Override
    void drawForegroundElements(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        text.render(ms, mouseX, mouseY);

        int topSeparator = TOP_PADDING + 12;
        int bottomSeparator = topSeparator + 25 + 24;

        // Draw the left-hand side
        drawHeader(ms);
        drawProgressBar(ms, book, mouseX, mouseY, (e) -> true);

        // the right-hand entries are handled by the buttons we added

        if (book.getContents().isErrored()) {
            int x = RIGHT_PAGE_X + PAGE_WIDTH / 2;
            int y = bottomSeparator + 12;

            drawCenteredStringNoShadow(ms, I18n.get("patchouli.gui.lexicon.loading_error"), x, y, 0xFF0000);
            drawCenteredStringNoShadow(ms, I18n.get("patchouli.gui.lexicon.loading_error_hover"), x, y + 10, 0x777777);

            x -= PAGE_WIDTH / 2;
            y -= 4;

            if (isMouseInRelativeRange(mouseX, mouseY, x, y, PAGE_WIDTH, 20)) {
                makeErrorTooltip();
            }
        }
    }

    @Override
    void onPageChanged() {
        buildEntryButtons();
    }

    void buildEntryButtons() {
        removeDrawablesIn(entryButtons);
        entryButtons.clear();

        maxSpreads = 1;

        addEntryButtons(RIGHT_PAGE_X, TOP_PADDING, 0, ENTRIES_IN_FIRST_PAGE);
    }

    void addEntryButtons(int x, int y, int start, int count) {
        if (!this.book.getContents().isErrored()) {
            for (int i = 0; i < count && (i + start) < allEntries.size(); i++) {
                Button button = new GuiButtonEntry(this, bookLeft + x, bookTop + y + i * 11, allEntries.get(start + i),
                    this::handleButtonEntry);
                addRenderableWidget(button);
                entryButtons.add(button);
            }
        }
    }

    void drawHeader(PoseStack ms) {
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        drawFromTexture(ms, book, -8, 12, 0, 180, 140, 31);

        int color = book.nameplateColor;
        font.draw(ms, book.getBookItem().getHoverName(), 13, 16, color);
        Component toDraw = book.getSubtitle().withStyle(book.getFontStyle());
        font.draw(ms, toDraw, 24, 24, color);
    }

    void makeErrorTooltip() {
        Throwable e = book.getContents().getException();

        List<Component> lines = new ArrayList<>();
        while (e != null) {
            String msg = e.getMessage();
            if (msg != null && !msg.isEmpty()) {
                lines.add(new TextComponent(e.getMessage()));
            }
            e = e.getCause();
        }

        if (!lines.isEmpty()) {
            lines.add(new TranslatableComponent("patchouli.gui.lexicon.loading_error_log").withStyle(ChatFormatting.GREEN));
            setTooltip(lines);
        }
    }

    public void handleButtonHistory(Button button) {
        displayLexiconGui(new GuiBookHistory(book), true);
    }

    public void handleButtonAdvancements(Button button) {
        minecraft.setScreen(new GuiAdvancementsExt(minecraft.player.connection.getAdvancements(), this, book.advancementsTab));
    }

    public void handleButtonEdit(Button button) {
        if (hasShiftDown()) {
            long time = System.currentTimeMillis();
            book.reloadContents(true);
            book.reloadLocks(false);
            displayLexiconGui(book.getLandingPage(), false);
            minecraft.player.displayClientMessage(new TranslatableComponent("patchouli.gui.lexicon.reloaded", (System.currentTimeMillis() - time)), false);
        } else {
            displayLexiconGui(new GuiBookWriter(book), true);
        }
    }

    public void handleButtonResize(Button button) {
        if (PersistentData.data.bookGuiScale >= maxScale) {
            PersistentData.data.bookGuiScale = 0;
        } else {
            PersistentData.data.bookGuiScale = Math.max(2, PersistentData.data.bookGuiScale + 1);
        }

        PersistentData.save();
        displayLexiconGui(this, false);
    }

    public void handleButtonEntry(Button button) {
        GuiBookEntry.displayOrBookmark(this, ((GuiButtonEntry) button).getEntry());
    }

    int getEntryCountStart() {
        if (spread == 0) {
            return 0;
        }

        int start = ENTRIES_IN_FIRST_PAGE;
        start += (ENTRIES_PER_PAGE * 2) * (spread - 1);
        return start;
    }
}
