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
// --- ADD THESE IMPORTS ---
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;
// -------------------------

import javax.annotation.Nullable;
import java.util.List;

// 1. ADD `ModelBaker baker` to the record
public record BookModel(ResourceLocation model, List<BakedQuad> quads, ModelRenderProperties properties, RenderType type, ModelBaker baker) implements ItemModel {

    // 3. THIS IS THE FULLY CORRECTED UPDATE METHOD
    @Override
    public void update(ItemStackRenderState state, ItemStack stack, ItemModelResolver resolver, ItemDisplayContext displayContext, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        Book book = ItemModBook.getBook(stack);
        ResourceLocation modelLocation = book.model;

        // --- This MUST be "item/" ---
        ResourceLocation modelPath = modelLocation.withPrefix("item/"); 

        // Resolve the correct model using the stored baker
        ResolvedModel resolvedModel = this.baker.getModel(modelPath);
        if (resolvedModel == null) {
            // Fallback to the default baked model
            resolvedModel = this.baker.getModel(this.model); 
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

        // This is your correct MAP_CODEC with the .xmap() fix
        public static final MapCodec<BookModel.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        ResourceLocation.CODEC.fieldOf("model").xmap(
                                (rl) -> rl.withPrefix("item/"), // On read, add "item/"
                                (rl) -> rl.withPath(p -> p.startsWith("item/") ? p.substring(5) : p) // On write, remove "item/"
                        ).forGetter(BookModel.Unbaked::model),

                        RENDER_TYPE_CODEC.fieldOf("render_type").forGetter(BookModel.Unbaked::types)
                )
                .apply(instance, BookModel.Unbaked::new)
        );

        @Override
        public void resolveDependencies(ResolvableModel.Resolver resolver) {
            resolver.markDependency(this.model);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext context) {
            ModelBaker baker = context.blockModelBaker();
            ResolvedModel resolvedModel = baker.getModel(this.model);
            TextureSlots slots = resolvedModel.getTopTextureSlots();

            // 2. PASS `this.model` AND `baker` TO THE CONSTRUCTOR
            return new BookModel(
                    this.model, // <-- Pass the model location
                    resolvedModel.bakeTopGeometry(slots, baker, BlockModelRotation.X0_Y0).getAll(),
                    ModelRenderProperties.fromResolvedModel(baker, resolvedModel, slots),
                    this.types,
                    baker // <-- Pass the baker
            );
        }

        @Override
        public MapCodec<BookModel.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}