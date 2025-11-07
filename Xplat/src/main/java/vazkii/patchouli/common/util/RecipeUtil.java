package vazkii.patchouli.common.util;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import vazkii.patchouli.xplat.ServerGetter;


import java.util.Collection;
import java.util.List;
import java.util.Optional;

public final class RecipeUtil {


    private RecipeUtil() {}

    private static Optional<MinecraftServer> getServer() {
        if (ServerGetter.get() != null) {
            return Optional.ofNullable(ServerGetter.get().server());
        }
        return Optional.empty();
    }

    public static Optional<RecipeManager> getRecipeManager() {
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
    public static List<Ingredient> getShapelessIngredients(ShapelessRecipe recipe) {
        return recipe.placementInfo().ingredients();
    }

    /**
     * Returns all crafting recipes available on the server.
     * Internally calls RecipeManager#getAllRecipesFor(...)
     */
    public static Collection<RecipeHolder<?>> getAllCrafting() {
        return getRecipeManager()
                .map(RecipeManager::getRecipes)
                .orElseGet(java.util.List::of);
    }
}