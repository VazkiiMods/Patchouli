package vazkii.patchouli.api.data;

import net.minecraft.item.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Minecraftschurli
 * @version 2020-04-03
 */
public class TranslationEntryBuilder extends EntryBuilder {
    private final AtomicInteger textPageCounter = new AtomicInteger();

    TranslationEntryBuilder(String id, String icon, TranslationCategoryBuilder parent) {
        super(id, "book."+parent.bookBuilder.modid+"."+parent.bookBuilder.id+"."+parent.id+"."+id+".name", parent.id, icon, parent);
    }

    TranslationEntryBuilder(String id, ItemStack icon, TranslationCategoryBuilder parent) {
        super(id, "book."+parent.bookBuilder.modid+"."+parent.bookBuilder.id+"."+parent.id+"."+id+".name", parent.id, icon, parent);
    }

    public TranslationEntryBuilder addSimpleTextPage() {
        this.addSimpleTextPage("book."+parent.bookBuilder.modid+"."+parent.bookBuilder.id+"."+parent.id+"."+id+".page."+textPageCounter.getAndIncrement());
        return this;
    }

    public TranslationEntryBuilder addSimpleTextPageWithTitle() {
        super.addSimpleTextPage("book."+parent.bookBuilder.modid+"."+parent.bookBuilder.id+"."+parent.id+"."+id+".page."+textPageCounter.get(), "book."+parent.bookBuilder.modid+"."+parent.bookBuilder.id+"."+parent.id+"."+id+".page."+textPageCounter.getAndIncrement()+".title");
        return this;
    }

    @Override
    public TranslationEntryBuilder addSimpleTextPage(String text) {
        super.addSimpleTextPage(text);
        return this;
    }

    @Override
    public TranslationEntryBuilder addSimpleTextPage(String text, String title) {
        super.addSimpleTextPage(text, title);
        return this;
    }

    @Override
    public TranslationCategoryBuilder build() {
        return (TranslationCategoryBuilder) super.build();
    }

    @Override
    public TranslationEntryBuilder setAdvancement(String advancement) {
        super.setAdvancement(advancement);
        return this;
    }

    @Override
    public TranslationEntryBuilder setFlag(String flag) {
        super.setFlag(flag);
        return this;
    }

    @Override
    public TranslationEntryBuilder setPriority(boolean priority) {
        super.setPriority(priority);
        return this;
    }

    @Override
    public TranslationEntryBuilder setSecret(boolean secret) {
        super.setSecret(secret);
        return this;
    }

    @Override
    public TranslationEntryBuilder setReadByDefault(boolean readByDefault) {
        super.setReadByDefault(readByDefault);
        return this;
    }

    @Override
    public TranslationEntryBuilder setSortnum(int sortnum) {
        super.setSortnum(sortnum);
        return this;
    }

    @Override
    public TranslationEntryBuilder setTurnin(String turnin) {
        super.setTurnin(turnin);
        return this;
    }
}
