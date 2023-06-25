package valoeghese.originsgacha.capabilities;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.resources.ResourceKey;

/**
 * An interface to interact with data for a given unlocked origin.
 */
public interface IUnlockedOriginData {
	/**
	 * Get the origin unlocked by the player which this {@link IUnlockedOriginData} stores data on.
	 * @return the origin that the player has unlocked.
	 */
	ResourceKey<Origin> getOrigin();

	/**
	 * Get the time in ticks when the player can next switch to this origin.
	 * @return the time in ticks when the player can next switch to this origin.
	 */
	long getUnlockTimeTicks();

	/**
	 * Set the cooldown time in seconds until the player can next switch to this origin.
	 * @param cooldownTime the time in seconds until the player can next switch to this origin.
	 */
	void setCooldown(double cooldownTime);
}
