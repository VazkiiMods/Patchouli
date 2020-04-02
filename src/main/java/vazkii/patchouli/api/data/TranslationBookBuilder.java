package vazkii.patchouli.api.data;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * @author Minecraftschurli
 * @version 2020-04-03
 */
public class TranslationBookBuilder extends BookBuilder {
    TranslationBookBuilder(String modid, String id) {
        super(modid, id, "book."+modid+"."+id+".name", "book."+modid+"."+id+".landing_text");
        super.setI18n(true);
    }

    public TranslationCategoryBuilder addCategory(String id, ItemStack icon) {
        TranslationCategoryBuilder builder = new TranslationCategoryBuilder(id, icon, this);
        categories.add(builder);
        return builder;
    }

    public TranslationCategoryBuilder addCategory(String id, String icon) {
        TranslationCategoryBuilder builder = new TranslationCategoryBuilder(id, icon, this);
        categories.add(builder);
        return builder;
    }

    @Override
    public TranslationBookBuilder setBookTexture(String bookTexture) {
        super.setBookTexture(bookTexture);
        return this;
    }

    @Override
    public TranslationBookBuilder setFillerTexture(String fillerTexture) {
        super.setFillerTexture(fillerTexture);
        return this;
    }

    @Override
    public TranslationBookBuilder setCraftingTexture(String craftingTexture) {
        super.setCraftingTexture(craftingTexture);
        return this;
    }

    @Override
    public TranslationBookBuilder setModel(ResourceLocation model) {
        super.setModel(model);
        return this;
    }

    @Override
    public TranslationBookBuilder setModel(String model) {
        super.setModel(model);
        return this;
    }

    @Override
    public TranslationBookBuilder setTextColor(String textColor) {
        super.setTextColor(textColor);
        return this;
    }

    @Override
    public TranslationBookBuilder setHeaderColor(String headerColor) {
        super.setHeaderColor(headerColor);
        return this;
    }

    @Override
    public TranslationBookBuilder setNameplateColor(String nameplateColor) {
        super.setNameplateColor(nameplateColor);
        return this;
    }

    @Override
    public TranslationBookBuilder setLinkColor(String linkColor) {
        super.setLinkColor(linkColor);
        return this;
    }

    @Override
    public TranslationBookBuilder setLinkHoverColor(String linkHoverColor) {
        super.setLinkHoverColor(linkHoverColor);
        return this;
    }

    @Override
    public TranslationBookBuilder setProgressBarColor(String progressBarColor) {
        super.setProgressBarColor(progressBarColor);
        return this;
    }

    @Override
    public TranslationBookBuilder setProgressBarBackground(String progressBarBackground) {
        super.setProgressBarBackground(progressBarBackground);
        return this;
    }

    @Override
    public TranslationBookBuilder setOpenSound(String openSound) {
        super.setOpenSound(openSound);
        return this;
    }

    @Override
    public TranslationBookBuilder setFlipSound(String flipSound) {
        super.setFlipSound(flipSound);
        return this;
    }

    @Override
    public TranslationBookBuilder setIndexIcon(String indexIcon) {
        super.setIndexIcon(indexIcon);
        return this;
    }

    @Override
    public TranslationBookBuilder setIndexIcon(ItemStack indexIcon) {
        super.setIndexIcon(indexIcon);
        return this;
    }

    @Override
    public TranslationBookBuilder setVersion(String version) {
        super.setVersion(version);
        return this;
    }

    @Override
    public TranslationBookBuilder setSubtitle(String subtitle) {
        super.setSubtitle(subtitle);
        return this;
    }

    @Override
    public TranslationBookBuilder setCreativeTab(String creativeTab) {
        super.setCreativeTab(creativeTab);
        return this;
    }

    @Override
    public TranslationBookBuilder setAdvancementsTab(String advancementsTab) {
        super.setAdvancementsTab(advancementsTab);
        return this;
    }

    @Override
    public TranslationBookBuilder setCustomBookItem(ItemStack customBookItem) {
        super.setCustomBookItem(customBookItem);
        return this;
    }

    @Override
    public TranslationBookBuilder setShowProgress(boolean showProgress) {
        super.setShowProgress(showProgress);
        return this;
    }

    @Override
    public TranslationBookBuilder setDontGenerateBook(boolean dontGenerateBook) {
        super.setDontGenerateBook(dontGenerateBook);
        return this;
    }

    @Override
    public TranslationBookBuilder setShowToasts(boolean showToasts) {
        super.setShowToasts(showToasts);
        return this;
    }

    @Override
    public TranslationBookBuilder setUseBlockyFont(boolean useBlockyFont) {
        super.setUseBlockyFont(useBlockyFont);
        return this;
    }

    @Override
    public TranslationBookBuilder setI18n(boolean i18n) {
        return this;
    }
}
