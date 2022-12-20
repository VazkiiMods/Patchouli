package vazkii.patchouli.mixin.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import vazkii.patchouli.fabric.client.FabricClientInitializer;

@Mixin(targets = "net.minecraft.client.resources.model.ModelBakery.ModelBakerImpl")
public class MixinModelBakery {

	@ModifyArg(method = "bake(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/model/ModelState;)Lnet/minecraft/client/resources/model/BakedModel;", at = @At(value = "RETURN"))
	public BakedModel insertBookModel(BakedModel model, ResourceLocation id, ModelBakery bakery) {
		return FabricClientInitializer.replaceBookModel(model, bakery, id);
	}
}
