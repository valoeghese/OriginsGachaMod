package valoeghese.originsgacha.impl;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import valoeghese.originsgacha.event.PlayerTryEquipEvent;

/**
 * Implementations of complex mixin logic.
 */
public class PlayerTryEquipCalls {
	/**
	 * Called when the player clicks in an inventory.
	 * @param menu the menu in which the player clicked.
	 * @param player the player.
	 * @param slotId the id of the slot clicked.
	 * @param info the info allowing this to be cancelled.
	 */
	public static void onInventoryClick(InventoryMenu menu, Player player, int slotId, CallbackInfo info) {
		// Check whether this is legal.
		if (slotId < 0) {
			return;
		}

		Slot slot = menu.getSlot(slotId);

		// Equipment slot is an anonymous class in InventoryMenu
		// This slot is characterised by its max stack size of 1.
		boolean isEquipmentSlot = slot.getMaxStackSize() == 1;

		if (isEquipmentSlot) {
			if (MinecraftForge.EVENT_BUS.post(
					new PlayerTryEquipEvent(
							player,
							menu.getCarried(),
							PlayerTryEquipEvent.Reason.PLACE_IN_SLOT
					))) {
				info.cancel();
			}
		}
	}

	/**
	 * Called on inventory quick move to potentially override the slot the item is going to.
	 * @param player the player doing the move.
	 * @param originalSlot the slot the item wants to go to.
	 * @param itemStack the stack being moved.
	 * @return the slot it should actually move to.
	 */
	public static EquipmentSlot onInventoryQuickMove(Player player, EquipmentSlot originalSlot, ItemStack itemStack) {
		if (originalSlot == EquipmentSlot.HEAD || originalSlot == EquipmentSlot.CHEST
			|| originalSlot == EquipmentSlot.LEGS || originalSlot == EquipmentSlot.FEET) {
			if (MinecraftForge.EVENT_BUS.post(
					new PlayerTryEquipEvent(
							player,
							itemStack,
							PlayerTryEquipEvent.Reason.QUICK_MOVE_ITEM
					))) {
				// replace with main hand if cannot quick move.
				return EquipmentSlot.MAINHAND;
			}
		}

		// in most cases, just return the original slot.
		return originalSlot;
	}
}
