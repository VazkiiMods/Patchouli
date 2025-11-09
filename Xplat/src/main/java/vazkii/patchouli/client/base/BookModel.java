package vazkii.patchouli.client.base;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.*;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.api.PatchouliAPI;
import vazkii.patchouli.common.item.ItemModBook;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public record BookModel(ResourceLocation model, List<BakedQuad> quads, ModelRenderProperties properties, RenderType type, ModelBaker baker) implements ItemModel {

    private static final Map<ResourceLocation, ResolvedModel> MODEL_CACHE = new ConcurrentHashMap<>();
    private static final Set<ResourceLocation> LOGGED_RESOLVES = ConcurrentHashMap.newKeySet();
    private static final Set<ResourceLocation> LOGGED_FALLBACKS = ConcurrentHashMap.newKeySet();

    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        Book book = ItemModBook.getBook(stack);
        ResourceLocation modelLocation = book.model;
        if (LOGGED_RESOLVES.add(modelLocation)) {
            PatchouliAPI.LOGGER.info("Resolving model for book: " + book.id + ", model: " + modelLocation);
            String msg = ">>> PATCHOULI_MODEL >>> Resolving model for book: " + book.id + ", model: " + modelLocation;
            System.out.println(msg);
            ModelLog.log(msg);
        }
        ResourceLocation modelPath = modelLocation; 

        // Resolve the correct model using the stored baker or cached resolve
        ResolvedModel resolvedModel = MODEL_CACHE.get(modelPath);
        if (resolvedModel == null) {
            resolvedModel = this.baker.getModel(modelPath);
            if (resolvedModel != null) {
                MODEL_CACHE.put(modelPath, resolvedModel);
                LOGGED_FALLBACKS.remove(modelPath);
            }
        }

        if (resolvedModel == null) {
            if (LOGGED_FALLBACKS.add(modelPath)) {
                PatchouliAPI.LOGGER.warn("Model not found for: " + modelPath + ", falling back to default model: " + this.model);
                String warn = ">>> PATCHOULI_MODEL >>> Model not found for: " + modelPath + ", falling back to default model: " + this.model;
                System.out.println(warn);
                ModelLog.log(warn);
            }
            resolvedModel = MODEL_CACHE.computeIfAbsent(this.model, key -> this.baker.getModel(key));
            if (resolvedModel == null) {
                return;
            }
        }

        // --- This part renders the resolved model ---
        ItemStackRenderState.LayerRenderState layerState = state.newLayer();
        if (stack.hasFoil()) {
            layerState.setFoilType(ItemStackRenderState.FoilType.STANDARD);
        }

        TextureSlots slots = resolvedModel.getTopTextureSlots();
        // --- Use the stored baker ---
        ModelRenderProperties props = ModelRenderProperties.fromResolvedModel(this.baker, resolvedModel, slots);

        layerState.setRenderType(this.type);
        List<BakedQuad> quads = resolvedModel.bakeTopGeometry(slots, this.baker, BlockModelRotation.X0_Y0).getAll();
        
        layerState.setExtents(() -> BlockModelWrapper.computeExtents(quads));
        props.applyToLayer(layerState, displayContext);
        layerState.prepareQuadList().addAll(quads);
    }

    public record Unbaked(ResourceLocation model, RenderType types) implements ItemModel.Unbaked {
        private static final BiMap<String, RenderType> RENDER_TYPES = Util.make(HashBiMap.create(), map -> {
            map.put("translucent_item", Sheets.translucentItemSheet());
            map.put("cutout_block", Sheets.cutoutBlockSheet());
        });
        public static final Codec<RenderType> RENDER_TYPE_CODEC = ExtraCodecs.idResolverCodec(Codec.STRING, RENDER_TYPES::get, RENDER_TYPES.inverse()::get);

        public static final MapCodec<BookModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("model").xmap(
                                (rl) -> rl.withPrefix("item/"),
                                (rl) -> rl.withPath(p -> p.startsWith("item/") ? p.substring(5) : p)
                        ).forGetter(BookModel.Unbaked::model),

                        RENDER_TYPE_CODEC.fieldOf("render_type").forGetter(BookModel.Unbaked::types)
                )
                .apply(instance, BookModel.Unbaked::new)
        );

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            String msg = ">>> PATCHOULI_MODEL >>> BookModel.Unbaked.resolveDependencies for model: " + this.model;
            System.out.println(msg);
            ModelLog.log(msg);
            resolver.markDependency(this.model);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context) {
            String msg = ">>> PATCHOULI_MODEL >>> BookModel.Unbaked.bake called for model: " + this.model;
            System.out.println(msg);
            ModelLog.log(msg);
            ModelBaker baker = context.blockModelBaker();
            ResolvedModel resolvedModel = baker.getModel(this.model);
            TextureSlots slots = resolvedModel.getTopTextureSlots();

            return new BookModel(
                    this.model,
                    resolvedModel.bakeTopGeometry(slots, baker, BlockModelRotation.X0_Y0).getAll(),
                    ModelRenderProperties.fromResolvedModel(baker, resolvedModel, slots),
                    this.types,
                    baker
            );
        }

        @Override
        public MapCodec<BookModel.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}