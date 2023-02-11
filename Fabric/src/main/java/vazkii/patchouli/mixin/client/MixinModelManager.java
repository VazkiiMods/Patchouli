package vazkii.patchouli.mixin.client;

import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import vazkii.patchouli.client.base.BookModel;

import java.util.Map;

@Mixin(ModelManager.class)
public class MixinModelManager {
	@Inject(method = "loadModels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/ModelBakery;getBakedTopLevelModels()Ljava/util/Map;"))
	private void replaceBookModel(ProfilerFiller profilerFiller,
			Map<ResourceLocation, AtlasSet.StitchResult> map, ModelBakery modelBakery,
			CallbackInfoReturnable<?> cir) {
		BookModel.replace(modelBakery.getBakedTopLevelModels(), modelBakery);
	}
}
