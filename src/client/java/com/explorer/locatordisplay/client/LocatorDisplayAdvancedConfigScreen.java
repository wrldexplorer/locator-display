package com.explorer.locatordisplay.client;

import org.jspecify.annotations.NonNull;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LocatorDisplayAdvancedConfigScreen extends Screen {
    private final Screen parent;
    private Button uuidSourceButton;
    private Button proximityDetectionButton;
    private Button colorNameButton;
    private Button disableLocatorBarButton;

    public LocatorDisplayAdvancedConfigScreen(Screen parent) {
        super(Component.literal("Advanced Locator Display Options"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2 - 90;
        int startY = this.height / 2 - 90;
        int width = 180;
        int height = 20;
        int spacing = 24;

        // UUID fetching button
        this.uuidSourceButton = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> {
                    LocatorDisplayConfig.onlineUUID = !LocatorDisplayConfig.onlineUUID;
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).bounds(centerX, startY, width, height).build()
        );

        // proximity detection button
        this.proximityDetectionButton = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> {
                    LocatorDisplayConfig.proximity = !LocatorDisplayConfig.proximity;
                    if (LocatorDisplayConfig.proximity) {
                        LocatorDisplayConfig.imageIcon = true;
                        LocatorDisplayConfig.selectIndex = 0;
                    }
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).bounds(centerX, startY + spacing, width, height).build()
        );

        // color name button
        this.colorNameButton = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> {
                    LocatorDisplayConfig.colorName = !LocatorDisplayConfig.colorName;
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).bounds(centerX, startY + (spacing * 2), width, height).build()
        );

        // locator bar visibility button
        this.disableLocatorBarButton = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> {
                    LocatorDisplayConfig.disableLocatorBar = !LocatorDisplayConfig.disableLocatorBar;
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).bounds(centerX, startY + (spacing * 3), width, height).build()
        );

        // back button
        this.addRenderableWidget(
                Button.builder(Component.literal("Back"), button -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(this.parent);
                    }
                }).bounds(centerX, startY + (spacing * 4) + 15, width, height).build()
        );

        this.updateWidgetStates();
    }

    private void updateWidgetStates() {
        this.uuidSourceButton.setMessage(Component.literal(
                "UUID Source: " + (LocatorDisplayConfig.onlineUUID ? "Official Mojang" : "Server-Provided")
        ));
        this.proximityDetectionButton.setMessage(Component.literal(
                "Proximity Detection: " + (LocatorDisplayConfig.proximity ? "ON" : "OFF")
        ));
        this.colorNameButton.setMessage(Component.literal(
                "Color Name: " + (LocatorDisplayConfig.colorName ? "ON" : "OFF")
        ));
        this.disableLocatorBarButton.setMessage(Component.literal(
                "Disable Locator Bar: " + (LocatorDisplayConfig.disableLocatorBar ? "ON" : "OFF")
        ));

        this.uuidSourceButton.active = this.colorNameButton.active
                                     = this.proximityDetectionButton.active
                                     = LocatorDisplayConfig.enabled;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}