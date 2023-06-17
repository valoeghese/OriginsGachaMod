package valoeghese.originsgacha.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

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
	 * @param slot the equipment slot the player is trying to equip to.
	 * @param stack the item stack the player is trying to equip.
	 */
	public PlayerTryEquipEvent(Player player, EquipmentSlot slot, ItemStack stack, Reason reason) {
		super(player);

		this.slot = slot;
		this.stack = stack;
		this.reason = reason;
	}

	private final EquipmentSlot slot;
	private final ItemStack stack;
	private final Reason reason;

	/**
	 * Get the equipment slot the player is trying to equip the item stack to.
	 * @return the equipment slot that the player is trying to equip the item stack to.
	 */
	public EquipmentSlot getSlot() {
		return this.slot;
	}

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
		ARMOUR_RIGHT_CLICK,
		GENERAL,
		PLACE_IN_SLOT
	}
}
