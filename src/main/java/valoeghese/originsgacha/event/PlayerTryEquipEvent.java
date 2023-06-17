package valoeghese.originsgacha.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Fired when a player tries to equip an item stack.
 * This event is {@link Cancelable}.
 * If this event is canceled, the player will not equip the item stack.
 */
@Cancelable
public class PlayerTryEquipEvent extends PlayerEvent {
	/**
	 * Construct a {@link PlayerTryEquipEvent} for the given player, slot, and stack.
	 * @param player the player.
	 * @param stack the item stack the player is trying to equip.
	 */
	public PlayerTryEquipEvent(Player player, ItemStack stack, Reason reason) {
		super(player);

		this.stack = stack;
		this.reason = reason;
	}

	private final ItemStack stack;
	private final Reason reason;

	/**
	 * Get the item stack that the player is trying to equip.
	 * @return the item stack that the player is trying to equip.
	 */
	public ItemStack getStack() {
		return this.stack;
	}

	/**
	 * Get the reason for player equip.
	 * @return the reason for player equip.
	 */
	public Reason getReason() {
		return this.reason;
	}

	/**
	 * The reason for player equip.
	 */
	public enum Reason {
		/**
		 * The player has right clicked an armour item.
		 */
		ARMOUR_RIGHT_CLICK,
		/**
		 * General case. May be triggered after another, more specific reason due to the implementation.
		 */
		GENERAL,
		/**
		 * The player is in the inventory and trying to place it in a slot.
		 */
		PLACE_IN_SLOT
	}
}
