package valoeghese.originsgacha.impl;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.core.Holder;

import java.util.List;

/**
 * Access internals to do very specific things to what this mod does.
 */
public interface ChooseOriginScreenAccess extends ModifiableScreen {
	/**
	 * Get whether the screen is prompting the player to pick the default origin layer.
	 * @return whether the current layer of the screen is the default origin layer.
	 */
	boolean isDefaultOriginLayer();

	/**
	 * Get the origins available to be selected. Changes to this list will reflect on the screen.
	 * @return the list of origins available to be selected.
	 */
	List<Holder<Origin>> getSelectableOrigins();

	/**
	 * Modify this screen to correct the displayed origins to only encompass unlocked ones.
	 * @param refreshScreen whether to refresh the screen.
	 */
	void correctDisplayedOrigins(boolean refreshScreen);
}
