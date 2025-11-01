package vazkii.patchouli.common.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.level.Level;
import vazkii.patchouli.xplat.ServerGetter;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class RecipeUtil {


    private RecipeUtil() {}

    private static Optional<MinecraftServer> getServer() {
        return Optional.ofNullable(ServerGetter.get().server());
    }

    private static Optional<RecipeManager> getRecipeManager() {
        return getServer().map(MinecraftServer::getRecipeManager);
    }

    private static Optional<ServerLevel> getAnyServerLevel() {
        return getServer().map(server -> server.getLevel(Level.OVERWORLD));
    }

    public static Optional<RegistryAccess> getRegistryAccess() {
        return getServer().map(MinecraftServer::registryAccess);
    }

    public static Optional<RecipeHolder<?>> getRecipeByKey(String namespace, String path) {
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, ResourceLocation.fromNamespaceAndPath(namespace, path));
        return getRecipeManager().flatMap(m -> m.byKey(key));
    }

    public static Optional<RecipeHolder<CraftingRecipe>> getCraftingRecipe(List<ItemStack> stacks) {
        CraftingInput input = CraftingInput.of(1, 1, stacks);
        Optional<RecipeManager> mgr = getRecipeManager();
        Optional<ServerLevel> lvl = getAnyServerLevel();
        if (mgr.isEmpty() || lvl.isEmpty()) return Optional.empty();
        return mgr.get().getRecipeFor(RecipeType.CRAFTING, input, lvl.get());
    }

    public static ItemStack getCraftingResult(List<ItemStack> stacks) {
        CraftingInput input = CraftingInput.of(1, 1, stacks);
        Optional<RecipeHolder<CraftingRecipe>> opt = getCraftingRecipe(stacks);
        if (opt.isEmpty()) return ItemStack.EMPTY;
        RegistryAccess regs = getRegistryAccess().orElse(null);
        if (regs == null) return ItemStack.EMPTY;
        return opt.get().value().assemble(input, regs);
    }
    @SuppressWarnings("unchecked")
    public static List<List<ItemStack>> getShapelessIngredients(ShapelessRecipe recipe) {
        List<RecipeDisplay> displays = recipe.display();
        List<List<ItemStack>> ingredientsList = new ArrayList<>();

        if (!displays.isEmpty() && displays.getFirst() instanceof ShapelessCraftingRecipeDisplay display) {
            for (SlotDisplay slot : display.ingredients()) {
                if (slot instanceof SlotDisplay.ItemStackSlotDisplay itemSlot) {
                    ingredientsList.add(List.of(itemSlot.stack()));
                } else {
                    ingredientsList.add(List.of(ItemStack.EMPTY)); // fallback for non-item slots
                }
            }
        }

        return ingredientsList; // <-- return the filled list
    }

    /**
     * Returns a Stream of matching crafting recipes.
     * Internally calls RecipeManager#getRecipesFor(...) and converts the returned List to a Stream.
     */
    public static Stream<RecipeHolder<CraftingRecipe>> getAllMatchingCrafting(List<ItemStack> stacks) {
        Optional<RecipeManager> mgr = getRecipeManager();
        Optional<ServerLevel> lvl = getAnyServerLevel();
        if (mgr.isEmpty() || lvl.isEmpty()) return Stream.empty();

        CraftingInput input = CraftingInput.of(1, 1, stacks);
        return mgr.get().getRecipes().stream()
                .filter(rh -> rh.value() instanceof CraftingRecipe)
                .map(rh -> (RecipeHolder<CraftingRecipe>) rh)
                .filter(rh -> rh.value().matches(input, lvl.get()));
    }

    /**
     * Returns all crafting recipes available on the server.
     * Internally calls RecipeManager#getAllRecipesFor(...)
     */
    @SuppressWarnings("unchecked")
    public static Collection<RecipeHolder<?>> getAllCrafting() {
        return getRecipeManager()
                .map(mgr -> (Collection<RecipeHolder<?>>) mgr.getRecipes())
                .orElseGet(java.util.List::of);
    }
}