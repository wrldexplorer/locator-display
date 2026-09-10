package com.explorer.locatordisplay.client.mixin;

import com.explorer.locatordisplay.client.LocatorDisplayConfig;
import net.minecraft.client.gui.Hud;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Hud.class)
public class ExperienceRendererMixin {

    @Inject(
            method = "nextContextualInfoState",
            at = @At("RETURN"),
            cancellable = true
    )
    private void forceXP(CallbackInfoReturnable<Hud.ContextualInfo> cir) {
        if (LocatorDisplayConfig.disableLocatorBar) {
            if (cir.getReturnValue() == Hud.ContextualInfo.LOCATOR) {
                cir.setReturnValue(Hud.ContextualInfo.EXPERIENCE);
            }
        }
    }
}
