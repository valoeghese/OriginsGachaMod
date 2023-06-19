package valoeghese.originsgacha.mixin.equip;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import valoeghese.originsgacha.impl.PlayerTryEquipCalls;

@Mixin(InventoryMenu.class)
public class InventoryMenuMixin {
	@Redirect(at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/Mob;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"
	), method = "quickMoveStack")
	private EquipmentSlot modifyEquipmentSlotForQuickMove(ItemStack itemStack, Player pPlayer) {
		return PlayerTryEquipCalls.onInventoryQuickMove(pPlayer, Mob.getEquipmentSlotForItem(itemStack), itemStack);
	}
}
