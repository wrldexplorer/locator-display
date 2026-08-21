package com.explorer.locatordisplay.client.mixin;

import com.explorer.locatordisplay.client.LocatorColorUtil;
import com.explorer.locatordisplay.client.LocatorDisplayConfig;
import com.explorer.locatordisplay.client.UuidResolver;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(PlayerInfo.class)
public abstract class PlayerListEntryMixin {

	private static final Logger LOGGER = LoggerFactory.getLogger("LocatorDisplay");

	// to track which player names have already been logged (to avoid console spam | what oop does to a man ^=^)
	private static final Set<String> loggedPlayerNames = new HashSet<>();

	@Shadow
	public abstract GameProfile getProfile();

	@Inject(method = "getTabListDisplayName", at = @At("RETURN"), cancellable = true)
	private void addLocatorColorDot(CallbackInfoReturnable<Component> cir) {
		if (!LocatorDisplayConfig.enabled) {
			return;
		}

		GameProfile profile = getProfile();
		String playerName = profile.name();

		// skip unnamed entries - npc? etc? whatever server have
		if (playerName == null || playerName.isEmpty()) {
			return;
		}

		// Yay, we get the Mojang UUID (async). If the future is not done yet, we wait. TODO:add a limit
		CompletableFuture<UUID> uuidFuture = UuidResolver.getUuid(playerName);
		UUID uuid = null;

		if (uuidFuture.isDone()) {
			uuid = uuidFuture.getNow(null);
			if (uuid == null) {
				// API returned null ~a.k.a~ player is not in Mojang database (cracked/offline)
				// deterministic offline UUID so their color is stable
				uuid = UUID.nameUUIDFromBytes(
						("OfflinePlayer:" + playerName).getBytes(StandardCharsets.UTF_8)
				);
			}
		} else {
			// Future not complete yet -> no dot for this player (will appear later).
			// TODO: sth happens here, some tracker that activates the limit (in the future ~ maybe)
			return;
		}

		int rgbColor = LocatorColorUtil.getColorFromUuid(uuid);

		if (loggedPlayerNames.add(playerName.toLowerCase())) {
			LOGGER.info("Added locator dot for '{}' with UUID '{}'", playerName, uuid);
			LOGGER.info("Calculated hex code is: #{}", String.format("%06X", rgbColor));
		}

		/**
		 * Modifies the player name, by inserting the 'colored dot' in the beginning
		 * Might not be the best/safest implementation :\
		 */
		Component originalName = cir.getReturnValue();
		if (originalName == null) {
			originalName = Component.literal(playerName);
		}

		String pickedSymbol = LocatorDisplayConfig.getCurrentSymbol() + " ";
		Component nameSymbol = Component.literal(pickedSymbol)
				.setStyle(Style.EMPTY.withColor(rgbColor));

		MutableComponent modifiedName = Component.empty()
				.append(nameSymbol)
				.append(originalName);

		cir.setReturnValue(modifiedName);
	}
}