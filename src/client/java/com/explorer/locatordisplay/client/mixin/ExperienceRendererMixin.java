package com.explorer.locatordisplay.client.mixin;

import com.explorer.locatordisplay.client.LocatorDisplayConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// for survival and adventure
@Mixin(Hud.class)
public class ExperienceRendererMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "nextContextualInfoState",
            at = @At("RETURN"),
            cancellable = true
    )
    private void forceXP(CallbackInfoReturnable<Hud.ContextualInfo> cir) {

        if (LocatorDisplayConfig.disableLocatorBar && LocatorDisplayConfig.showXP()) {
            if (cir.getReturnValue() == Hud.ContextualInfo.LOCATOR) {
                cir.setReturnValue(Hud.ContextualInfo.EXPERIENCE);
            }
        }
    }
}
