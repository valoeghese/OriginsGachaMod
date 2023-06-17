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
	public PlayerTryEquipEvent(Player player, EquipmentSlot slot, ItemStack stack) {
		super(player);

		this.slot = slot;
		this.stack = stack;
	}

	private final EquipmentSlot slot;
	private final ItemStack stack;

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
}
