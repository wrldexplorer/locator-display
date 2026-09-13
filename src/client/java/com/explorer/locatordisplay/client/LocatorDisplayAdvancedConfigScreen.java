package com.explorer.locatordisplay.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LocatorDisplayAdvancedConfigScreen extends Screen {
    private final Screen parent;
    private ButtonWidget uuidSourceButton;
    private ButtonWidget proximityDetectionButton;
    private ButtonWidget colorNameButton;
    private ButtonWidget disableLocatorBarButton;

    public LocatorDisplayAdvancedConfigScreen(Screen parent) {
        super(Text .literal("Advanced Locator Display Options"));
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
        this.uuidSourceButton = this.addDrawableChild(
                ButtonWidget.builder(Text .empty(), button -> {
                    LocatorDisplayConfig.onlineUUID = !LocatorDisplayConfig.onlineUUID;
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).dimensions(centerX, startY, width, height).build()
        );

        // proximity detection button
        this.proximityDetectionButton = this.addDrawableChild(
                ButtonWidget.builder(Text .empty(), button -> {
                    LocatorDisplayConfig.proximity = !LocatorDisplayConfig.proximity;
                    if (LocatorDisplayConfig.proximity) {
                        LocatorDisplayConfig.imageIcon = true;
                        LocatorDisplayConfig.selectIndex = 0;
                    }
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).dimensions(centerX, startY + spacing, width, height).build()
        );

        // color name button
        this.colorNameButton = this.addDrawableChild(
                ButtonWidget.builder(Text .empty(), button -> {
                    LocatorDisplayConfig.colorName = !LocatorDisplayConfig.colorName;
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).dimensions(centerX, startY + (spacing * 2), width, height).build()
        );

        // locator bar visibility button
        this.disableLocatorBarButton = this.addDrawableChild(
                ButtonWidget.builder(Text .empty(), button -> {
                    LocatorDisplayConfig.disableLocatorBar = !LocatorDisplayConfig.disableLocatorBar;
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).dimensions(centerX, startY + (spacing * 3), width, height).build()
        );

        // back button
        this.addDrawableChild(
                ButtonWidget.builder(Text .literal("Back"), button -> {
                    if (this.client != null) {
                        this.client.setScreen(this.parent);
                    }
                }).dimensions(centerX, startY + (spacing * 4) + 15, width, height).build()
        );

        this.updateWidgetStates();
    }

    private void updateWidgetStates() {
        this.uuidSourceButton.setMessage(Text .literal(
                "UUID Source: " + (LocatorDisplayConfig.onlineUUID ? "Official Mojang" : "Server-Provided")
        ));
        this.proximityDetectionButton.setMessage(Text .literal(
                "Proximity Detection: " + (LocatorDisplayConfig.proximity ? "ON" : "OFF")
        ));
        this.colorNameButton.setMessage(Text .literal(
                "Color Name: " + (LocatorDisplayConfig.colorName ? "ON" : "OFF")
        ));
        this.disableLocatorBarButton.setMessage(Text .literal(
                "Disable Locator Bar: " + (LocatorDisplayConfig.disableLocatorBar ? "ON" : "OFF")
        ));

        this.uuidSourceButton.active = this.colorNameButton.active
                                     = this.proximityDetectionButton.active
                                     = LocatorDisplayConfig.enabled;
    }

    @Override
    public void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick); // This draws the widgets
    }
}