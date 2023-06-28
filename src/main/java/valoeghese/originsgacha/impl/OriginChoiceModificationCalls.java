package valoeghese.originsgacha.impl;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import valoeghese.originsgacha.OriginsGacha;
import valoeghese.originsgacha.capabilities.IUnlockedOrigins;

import java.util.List;
import java.util.Objects;

/**
 * Impl of various mixin methods related to origin gacha.
 */
public class OriginChoiceModificationCalls {
	/**
	 * Get whether the current layer is the default origin layer.
	 * @param layerList the list of origin layers for the screen.
	 * @param currentLayerIndex the current origin layer index in use.
	 * @return whether the layer at the current index is the default origin layer.
	 */
	public static boolean isDefaultOriginLayer(final List<Holder<OriginLayer>> layerList, final int currentLayerIndex) {
		return layerList.get(currentLayerIndex).is(OriginsGacha.ORIGIN_LAYER.location());
	}

	/**
	 * Edit the choose origin screen to only have the randomly selected initial unlocked origins.
	 * @param screen the choose origin screen.
	 * @param originSelection the list of selectable origins.
	 */
	public static <T extends Screen & ChooseOriginScreenAccess> void updateSelectableOrigins(final T screen, final List<Holder<Origin>> originSelection) {
		// Don't modify the screen if it's already modified
		// This is also checked in the mixin but it's better to be redundantly clear than screw something up later.
		// Especially for such a simple operation.
		if (screen.isModified()) {
			return;
		}

		// Holder.is(ResourceKey) in Holder.Reference uses reference comparison so we need to compare resource location
		// Ensure the current layer is the origin layer
		if (screen.isDefaultOriginLayer()) {
			Minecraft minecraft = Minecraft.getInstance();

			assert minecraft.player != null; // the player should never be null in game. Why tf would it be null?
			IUnlockedOrigins unlockedOrigins = IUnlockedOrigins.getUnlockedOrigins(minecraft.player);

			// remove origins if origin is not unlocked yet
			originSelection.removeIf(originHolder -> !unlockedOrigins.hasOrigin(
					Objects.requireNonNull(originHolder.unwrapKey().orElse(null), "Missing Origin Key")
			));

			// TODO stop cheesing on server side by intercepting packets and checking if the player has the origin unlocked
			// TODO if origin list is empty wait until server sends us the data
		}

		// Mark the screen as modified
		screen.setModified(true);
	}
}
