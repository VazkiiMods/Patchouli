package vazkii.patchouli.client.mixin;

import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.patchouli.client.handler.BookCrashHandler;

@Mixin(CrashReport.class)
public abstract class MixinCrashReport {
	@Shadow
	public abstract CrashReportSection getSystemDetailsSection();

	@Inject(at = @At("RETURN"), method = "fillSystemDetails")
	private void fillPatchouliContext(CallbackInfo info) {
		BookCrashHandler crashInfo = new BookCrashHandler();
		getSystemDetailsSection().add(crashInfo.getLabel(), crashInfo);

	}
}
