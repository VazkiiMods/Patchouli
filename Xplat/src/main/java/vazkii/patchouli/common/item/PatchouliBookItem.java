package vazkii.patchouli.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.common.base.PatchouliSounds;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.book.BookRegistry;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class PatchouliBookItem extends Item {
    public static final String BOOK_TAG = "patchouli:book";

    public PatchouliBookItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var itemStack = player.getItemInHand(hand);

        var book = getBook(itemStack);
        if (book == null) {
            return InteractionResultHolder.fail(itemStack);
        }

        var clientSide = level.isClientSide;
        if (!clientSide) {
            var serverPlayer = (ServerPlayer) player;

            PatchouliAPI.get().openBookGUI(serverPlayer, book.id);

            var sound = PatchouliSounds.getSound(book.openSound, PatchouliSounds.BOOK_OPEN);

            serverPlayer.playSound(sound, 1.0F, 0.6875F + (float) level.random.nextDouble() * 0.4F);

        }

        return InteractionResultHolder.sidedSuccess(itemStack, clientSide);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level $$1, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, $$1, list, tooltipFlag);

        Component component;

        var resourceLocation = getBookResourceLocation(itemStack);
        if (resourceLocation != null) {
            var book = getBook(itemStack);

            if (tooltipFlag.isAdvanced()) {
                list.add(text("Book Id: %s", resourceLocation));
            }

            component = book == null ? text("item.patchouli.guide_book.invalid", resourceLocation) : book.getSubtitle().withStyle(ChatFormatting.GRAY);
        } else {
            component = text("item.patchouli.guide_book.undefined");
        }

        list.add(component);
    }

    // For Forge, IForgeItem. Soft-implementation.
    public String getCreatorModId(ItemStack itemStack) {
        var book = getBook(itemStack);

        return book == null ? Registry.ITEM.getKey(this).getNamespace() : book.owner.getId();
    }

    @NotNull
    @Override
    public Component getName(ItemStack itemStack) {
        var book = getBook(itemStack);

        return book == null ? super.getName(itemStack) : Component.translatable(book.name);
    }

    private static Component text(String string, Object... args) {
        return Component.translatable(string, args).withStyle(ChatFormatting.GRAY);
    }

    public static float getCompletion(ItemStack itemStack) {
        var book = getBook(itemStack);

        if (book == null) {
            return 0.0F;
        }

        int total = 0;
        int unlocked = 0;

        for (BookEntry bookEntry : book.getContents().entries.values()) {
            if (!bookEntry.isSecret()) {
                total++;

                if (!bookEntry.isLocked()) {
                    unlocked++;
                }
            }
        }

        return (float) unlocked / total;
    }

    @Nullable
    public static Book getBook(ItemStack itemStack) {
        var resourceLocation = getBookResourceLocation(itemStack);

        return resourceLocation == null ? null : BookRegistry.INSTANCE.books.get(resourceLocation);
    }

    public static ItemStack getItemStack(Book book) {
        return getItemStack(book.id);
    }

    public static ItemStack getItemStack(ResourceLocation resourceLocation) {
        var itemStack = PatchouliItems.BOOK.getDefaultInstance();

        itemStack.getOrCreateTag().putString(BOOK_TAG, resourceLocation.toString());

        return itemStack;
    }

    private static ResourceLocation getBookResourceLocation(ItemStack itemStack) {
        var tag = itemStack.getOrCreateTag();

        return tag.contains(BOOK_TAG) ? null : ResourceLocation.tryParse(tag.getString(BOOK_TAG));
    }
}
