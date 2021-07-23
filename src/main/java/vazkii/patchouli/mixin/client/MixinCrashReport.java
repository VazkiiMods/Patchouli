package vazkii.patchouli.mixin.client;

import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.handler.BookCrashHandler;

@Mixin(CrashReport.class)
public abstract class MixinCrashReport {
	@Shadow
	public abstract CrashReportCategory addCategory(String name);

	@Inject(at = @At("RETURN"), method = "<init>")
	private void fillPatchouliContext(CallbackInfo info) {
		CrashReportCategory section = this.addCategory("Patchouli Book Info");
		BookCrashHandler crashInfo = new BookCrashHandler();
		section.setDetail(crashInfo.getLabel(), crashInfo);
	}
}
