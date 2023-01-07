package vazkii.patchouli.mixin.client;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.fabric.client.FabricClientInitializer;

import java.util.Map;

@Mixin(ModelManager.class)
public class MixinModelManager {
       @Shadow
       @Final private Map<ResourceLocation, BakedModel> bakedRegistry;

       @Inject(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/resources/model/ModelBakery;getBakedTopLevelModels()Ljava/util/Map;", shift = At.Shift.AFTER), method = "apply(Lnet/minecraft/client/resources/model/ModelManager$ReloadState;Lnet/minecraft/util/profiling/ProfilerFiller;)V")
       public void insertBookModel(ModelManager.ReloadState reloadState, ProfilerFiller profiler, CallbackInfo info) {
               FabricClientInitializer.replaceBookModel(reloadState.modelBakery, bakedRegistry);
       }
}
