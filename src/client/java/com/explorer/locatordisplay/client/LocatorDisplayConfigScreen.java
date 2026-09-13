package com.explorer.locatordisplay.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class LocatorDisplayConfigScreen extends Screen {
    private final Screen parent;
    private ButtonWidget enabledButton;
    private ButtonWidget iconTypeButton;
    private ButtonWidget symbolButton;
    private TextFieldWidget customBox;
    private ButtonWidget defaultsButton;

    public LocatorDisplayConfigScreen(Screen parent) {
        super(Text.literal("Locator Display Config"));
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
        this.enabledButton = this.addDrawableChild(
                ButtonWidget.builder(Text.empty(), button -> {
                    LocatorDisplayConfig.enabled = !LocatorDisplayConfig.enabled;
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates(); // refreshes the buttons
                }).dimensions(centerX, startY, width, height).build()
        );

        // icon type button
        this.iconTypeButton = this.addDrawableChild(
                ButtonWidget.builder(Text.empty(), button -> {
                    LocatorDisplayConfig.imageIcon = !LocatorDisplayConfig.imageIcon;
                    LocatorDisplayConfig.selectIndex = 0;
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).dimensions(centerX, startY + spacing, width, height).build()
        );

        // symbol customizer button
        this.symbolButton = this.addDrawableChild(
                ButtonWidget.builder(Text.empty(), button -> {
                    LocatorDisplayConfig.selectIndex = (LocatorDisplayConfig.selectIndex + 1)
                            % (LocatorDisplayConfig.imageIcon
                            ? LocatorDisplayConfig.ICONS.length
                            : LocatorDisplayConfig.SYMBOLS.length);
                    LocatorDisplayConfig.save();
                    this.updateWidgetStates();
                }).dimensions(centerX, startY + (spacing * 2), width, height).build()
        );

        // custom symbol/icon textbox
        this.customBox = new TextFieldWidget(
                this.textRenderer,
                centerX,
                startY + (spacing * 3),
                width,
                height,
                Text.literal("Custom")
        );
        this.addDrawableChild(this.customBox);

        // advanced options button
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Advanced Options"), button -> {
                    if (this.client != null) {
                        this.client.setScreen(new LocatorDisplayAdvancedConfigScreen(this));
                    }
                }).dimensions(centerX, startY + (spacing * 4), width, height).build()
        );

        // splitting bottom area for defaults & done buttons to be side-by-side
        int halfWidth = (width - 4) / 2; // 88px each w/ 4px gap

        // defaults button
        this.defaultsButton = this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Defaults"), button -> {
                    LocatorDisplayConfig.resetToDefaults();
                    this.updateWidgetStates();
                }).dimensions(centerX, startY + (spacing * 5) + 15, halfWidth, height).build()
        );

        // done button
        this.addDrawableChild(
                ButtonWidget.builder(Text.literal("Done"), button -> {
                    if (this.client != null) {
                        this.client.setScreen(this.parent);
                    }
                }).dimensions(centerX + halfWidth + 4, startY + (spacing * 5) + 15, halfWidth, height).build()
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

        this.enabledButton.setMessage(Text.literal("Enabled: " + (isLocatorEnabled ? "ON" : "OFF")));

        this.iconTypeButton.setMessage(Text.literal(
                "Icon Type: " + (LocatorDisplayConfig.imageIcon ? "Texture" : "Symbol")
        ));

        this.symbolButton.setMessage(Text.literal(LocatorDisplayConfig.imageIcon
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
        this.customBox.setChangedListener(text -> {});

        if (LocatorDisplayConfig.imageIcon) {
            this.customBox.setMaxLength(256);
            this.customBox.setPlaceholder(Text.literal("> Custom texture PATH"));
            this.customBox.setText(LocatorDisplayConfig.customDir == null ? "" : LocatorDisplayConfig.customDir);
        } else {
            this.customBox.setMaxLength(3);
            this.customBox.setPlaceholder(Text.literal("> Custom (max length 3)"));
            this.customBox.setText(LocatorDisplayConfig.customSymbol == null ? "" : LocatorDisplayConfig.customSymbol);
        }

        this.customBox.setChangedListener(text -> {
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
    public void render(DrawContext guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick); // This draws the widgets
    }
}