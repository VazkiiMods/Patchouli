package vazkii.patchouli.mixin.client;

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
	public abstract CrashReportSection addElement(String name);

	@Inject(at = @At("RETURN"), method = "<init>")
	private void fillPatchouliContext(CallbackInfo info) {
		CrashReportSection section = this.addElement("Patchouli Book Info");
		var crashInfo = new BookCrashHandler();
		section.add(crashInfo.getLabel(), crashInfo);
	}
}
