package valoeghese.originsgacha.capabilities;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Container for unlocked origins of a player.
 */
public interface IUnlockedOrigins extends INBTSerializable<CompoundTag> {
	/**
	 * Called every server tick.
	 */
	void tick();

	/**
	 * Called to make the unlocked origins sync from server -> client.
	 */
	void sync();

	/**
	 * Get a list of unlocked origins.
	 * @return a copy of the list containing the resource keys of all unlocked origins.
	 */
	List<IUnlockedOriginData> getUnlockedOrigins();

	/**
	 * Get the unlocked origin data for the given origin.
	 * @param data the origin to get the unlocked origin data for.
	 * @return the unlocked origin data for the given origin, or null if the origin is not unlocked.
	 */
	@Nullable
	IUnlockedOriginData getUnlockedOrigin(ResourceKey<Origin> data);

	/**
	 * Check if the given origin is unlocked.
	 * @param origin the origin to check.
	 *               This should be the origin's registry key.
	 * @return true if the origin is unlocked.
	 */
	boolean hasOrigin(ResourceKey<Origin> origin);

	/**
	 * Unlock the given origin.
	 * @param origin the origin to unlock.
	 */
	void unlockOrigin(ResourceKey<Origin> origin);

	/**
	 * Get the player owner.
	 * @return the player that owns this instance. The unlocked origins belong to them.
	 */
	Player getOwner();

	/**
	 * Get the number of orbs of origin required for the next roll of the origin gacha wheel.
	 * @return the number of orbs of origin required for the next roll.
	 */
	int getRequiredOrbsForNextRoll();

	/**
	 * Get the page the player was last viewing. This does not persist across respawns and sessions.
	 * @return the page the player was last viewing.
	 */
	int getPage();

	/**
	 * Set the page the player was last viewing. This does not persist across respawns and sessions.
	 * @param page the page the player was last viewing.
	 */
	void setPage(int page);

	/**
	 * Get the unlocked origins instance for a given player.
	 * @param player the player to get the unlocked origins instance for.
	 * @return the instance associated with the given player.
	 */
	static IUnlockedOrigins getUnlockedOrigins(Player player) {
		return player.getCapability(UnlockedOrigins.CAPABILITY).resolve().orElseThrow(
				() -> new IllegalStateException("No unlocked origins capability for " + player.getName().getString())
		);
	}
}
