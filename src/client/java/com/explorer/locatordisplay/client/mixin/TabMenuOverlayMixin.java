package com.explorer.locatordisplay.client.mixin;

import com.explorer.locatordisplay.client.LocatorColorUtil;
import com.explorer.locatordisplay.client.LocatorDisplayConfig;
import com.explorer.locatordisplay.client.LocatorDisplayTabLayout;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.gui.DrawContext; //GuiGraphicsExtractor
import net.minecraft.client.gui.hud.PlayerListHud; //PlayerTabOverlay
import net.minecraft.client.network.PlayerListEntry; //PlayerInfo
import net.minecraft.client.gl.RenderPipelines; //RenderPipelines
import net.minecraft.util.Identifier;
import net.minecraft.scoreboard.ScoreboardObjective; //objective
import net.minecraft.scoreboard.Scoreboard; //scoreboard

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerListHud.class)
public class TabMenuOverlayMixin {

    @ModifyConstant(method = "render", constant = @Constant(intValue = 13))
    private int widenRowForIcon(int vanillaPingWidth) {
        if (!LocatorDisplayConfig.enabled || !LocatorDisplayConfig.imageIcon) {
            return vanillaPingWidth;
        }
        return vanillaPingWidth + LocatorDisplayTabLayout.ICON_SLOT;
    }

    // Rendering the texture between player head and player name
    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"
            )
    )
    private void drawTextureIcon(
            DrawContext graphics,
            int screenWidth,
            Scoreboard scoreboard,
            ScoreboardObjective displayObjective,
            CallbackInfo ci,
            @Local PlayerListEntry playerEntry,
            @Local(ordinal = 0) int contentXOffset,
            @Local(ordinal = 1) int contentYOffset
    ) {
        if (!LocatorDisplayConfig.enabled || !LocatorDisplayConfig.imageIcon) {
            return;
        }

        Identifier iconIdentifier;

        boolean useProximity = LocatorDisplayConfig.proximity
                && !LocatorDisplayConfig.isCustomSelected()
                && !LocatorDisplayConfig.getCurrentSelection().equals("Bowtie");

        if (useProximity) {
            iconIdentifier = LocatorDisplayConfig.getProximityIcon(playerEntry.getProfile().id());
        } else {
            iconIdentifier = LocatorDisplayConfig.getSelectedIconIdentifier();
        }

        if (iconIdentifier == null || iconIdentifier.getPath().isEmpty()) return;

        // if player head is rendering, place the icon right after it @9px offset
        int rgbColor = LocatorColorUtil.getColorFromUuid(playerEntry.getProfile().id());
        int iconColorTint = 0xFF000000 | (rgbColor & 0x00FFFFFF);

        String path = iconIdentifier.getPath();
        if (path.startsWith("hud/")) {
            // Built-in HUD Sprites (from sprite atlas)
            graphics.drawGuiTexture(
                    RenderPipelines.GUI_TEXTURED,
                    iconIdentifier,
                    contentXOffset,
                    contentYOffset,
                    LocatorDisplayTabLayout.ICON_SIZE,
                    LocatorDisplayTabLayout.ICON_SIZE,
                    iconColorTint
            );
        } else {
            // Standard Textures in live instance/active mods (blit)
            // U and V offsets are 0, texture dimensions == icon size
            // should scale specified texture to the 8x8
            graphics.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    iconIdentifier,
                    contentXOffset,
                    contentYOffset,
                    0.0f, // uOffset
                    0.0f, // vOffset
                    LocatorDisplayTabLayout.ICON_SIZE,
                    LocatorDisplayTabLayout.ICON_SIZE,
                    LocatorDisplayTabLayout.ICON_SIZE, // textureWidth
                    LocatorDisplayTabLayout.ICON_SIZE, // textureHeight
                    iconColorTint
            );
        }
    }

    // push player name text to the right of the custom icon slot
    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"
            ),
            index = 2
    )
    private int shiftNamePastIcon(int textXPosition) {
        if (LocatorDisplayConfig.enabled && LocatorDisplayConfig.imageIcon) {
            return textXPosition + LocatorDisplayTabLayout.ICON_SLOT;
        }
        return textXPosition;
    }
}