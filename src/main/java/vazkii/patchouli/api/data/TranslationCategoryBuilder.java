package vazkii.patchouli.api.data;

import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.PatchouliAPI;

/**
 * @author Minecraftschurli
 * @version 2020-04-03
 */
public class TranslationCategoryBuilder extends CategoryBuilder {
    TranslationCategoryBuilder(String id, ItemStack icon, TranslationBookBuilder parent) {
        super(id, "book."+parent.modid+"."+parent.id+"."+id+".name", "book."+parent.modid+"."+parent.id+"."+id+".description", icon, parent);
    }

    TranslationCategoryBuilder(String id, String icon, TranslationBookBuilder parent) {
        super(id, "book."+parent.modid+"."+parent.id+"."+id+".name", "book."+parent.modid+"."+parent.id+"."+id+".description", icon, parent);
    }

    @Override
    public TranslationBookBuilder build() {
        return (TranslationBookBuilder) super.build();
    }

    public TranslationEntryBuilder addEntry(String id, String icon) {
        TranslationEntryBuilder builder = new TranslationEntryBuilder(id, icon, this);
        entries.add(builder);
        return builder;
    }

    public TranslationEntryBuilder addEntry(String id, ItemStack icon) {
        return addEntry(id, PatchouliAPI.instance.serializeItemStack(icon));
    }

    @Override
    public TranslationCategoryBuilder setParent(String parent) {
        super.setParent(parent);
        return this;
    }

    @Override
    public TranslationCategoryBuilder setFlag(String flag) {
        super.setFlag(flag);
        return this;
    }

    @Override
    public TranslationCategoryBuilder setSortnum(Integer sortnum) {
        super.setSortnum(sortnum);
        return this;
    }

    @Override
    public TranslationCategoryBuilder setSecret(Boolean secret) {
        super.setSecret(secret);
        return this;
    }
}
