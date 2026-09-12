package com.explorer.locatordisplay.client;

import org.jspecify.annotations.NonNull;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LocatorDisplayConfigScreen extends Screen {
    private final Screen parent;
    private Button enabledButton;
    private Button iconTypeButton;
    private Button symbolButton;
    private EditBox customBox;
    private Button defaultsButton;

    public LocatorDisplayConfigScreen(Screen parent) {
        super(Component.literal("Locator Display Options"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2 - 90;
        int startY = this.height / 2 - 90;
        int width = 180;
        int height = 20;
        int spacing = 24;

        // toggle on/off button
        this.enabledButton = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> {
                    LocatorDisplayConfig.enabled = !LocatorDisplayConfig.enabled;
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates(); // refreshes the buttons
                }).bounds(centerX, startY, width, height).build()
        );

        // icon type button
        this.iconTypeButton = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> {
                    LocatorDisplayConfig.imageIcon = !LocatorDisplayConfig.imageIcon;
                    LocatorDisplayConfig.selectIndex = 0;
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).bounds(centerX, startY + spacing, width, height).build()
        );

        // symbol customizer button
        this.symbolButton = this.addRenderableWidget(
                Button.builder(Component.empty(), button -> {
                    LocatorDisplayConfig.selectIndex = (LocatorDisplayConfig.selectIndex + 1)
                            % (LocatorDisplayConfig.imageIcon
                            ? LocatorDisplayConfig.ICONS.length
                            : LocatorDisplayConfig.SYMBOLS.length);
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).bounds(centerX, startY + (spacing * 2), width, height).build()
        );

        // custom symbol/icon textbox
        this.customBox = new EditBox(this.font, centerX, startY + (spacing * 3), width, height, Component.literal("Custom"));
        this.addRenderableWidget(this.customBox);

        // advanced options button
        this.addRenderableWidget(
                Button.builder(Component.literal("Advanced Options"), button -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new LocatorDisplayAdvancedConfigScreen(this));
                    }
                }).bounds(centerX, startY + (spacing * 4), width, height).build()
        );

        // splitting bottom area for defaults & done buttons to be side-by-side
        int halfWidth = (width - 4) / 2; // 88px each w/ 4px gap

        // defaults button
        this.defaultsButton = this.addRenderableWidget(
                Button.builder(Component.literal("Defaults"), button -> {
                    LocatorDisplayConfig.resetToDefaults();
                    this.updateWidgetStates();
                }).bounds(centerX, startY + (spacing * 5) + 15, halfWidth, height).build()
        );

        // done button
        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), button -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(this.parent);
                    }
                }).bounds(centerX + halfWidth + 4, startY + (spacing * 5) + 15, halfWidth, height).build()
        );

        this.updateWidgetStates(); // updates/refreshes the contents when a change occurs
    }

    /**
     * Experimenting with a single source of truth to update every label,
     * state, and interaction rule based on the configuration
     * so the UI feels more interactive
     * (less static and buggy)
     */
    private void updateWidgetStates() {
        boolean isLocatorEnabled = LocatorDisplayConfig.enabled;

        this.enabledButton.setMessage(Component.literal("Enabled: " + (isLocatorEnabled ? "ON" : "OFF")));

        this.iconTypeButton.setMessage(Component.literal(
                "Icon Type: " + (LocatorDisplayConfig.imageIcon ? "Texture" : "Symbol")
        ));

        this.symbolButton.setMessage(Component.literal(LocatorDisplayConfig.imageIcon
                ? ("Icon: "  + LocatorDisplayConfig.ICONS[LocatorDisplayConfig.selectIndex])
                : ("Symbol: " + LocatorDisplayConfig.SYMBOLS[LocatorDisplayConfig.selectIndex])
        ));

        this.iconTypeButton.active = this.symbolButton.active
                                   = isLocatorEnabled;

        boolean customActive = isLocatorEnabled && LocatorDisplayConfig.isCustomSelected() && !LocatorDisplayConfig.proximity;

        if(LocatorDisplayConfig.proximity) {
            this.iconTypeButton.active = this.symbolButton.active = false;
        }

        this.customBox.setEditable(customActive);
        this.customBox.active = customActive;
        if (!customActive) {
            this.customBox.setFocused(false);
        }
        this.customBox.setResponder(text -> {});

        if (LocatorDisplayConfig.imageIcon) {
            this.customBox.setMaxLength(256);
            this.customBox.setHint(Component.literal("> Custom texture PATH"));
            this.customBox.setValue(LocatorDisplayConfig.customDir == null ? "" : LocatorDisplayConfig.customDir);
        } else {
            this.customBox.setMaxLength(3);
            this.customBox.setHint(Component.literal("> Custom (max length 3)"));
            this.customBox.setValue(LocatorDisplayConfig.customSymbol == null ? "" : LocatorDisplayConfig.customSymbol);
        }

        this.customBox.setResponder(text -> {
            if (LocatorDisplayConfig.imageIcon) {
                LocatorDisplayConfig.customDir = text.isEmpty() ? "" : text;
            } else {
                LocatorDisplayConfig.customSymbol = (!text.isEmpty() && text.length() <= 3) ? text : "⬤";
            }
            LocatorDisplayConfig.save();
        });

        this.defaultsButton.active = !LocatorDisplayConfig.isDefault();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
    }
}