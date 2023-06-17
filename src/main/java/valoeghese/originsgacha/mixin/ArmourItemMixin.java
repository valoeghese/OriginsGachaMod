package valoeghese.originsgacha.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import valoeghese.originsgacha.event.PlayerTryEquipEvent;

@Mixin(ArmorItem.class)
public class ArmourItemMixin {
	@Inject(at = @At("HEAD"), cancellable = true, method = "use")
	private void onUse(Level pLevel, Player pPlayer, InteractionHand pHand,
					   CallbackInfoReturnable<InteractionResultHolder<ItemStack>> info) {
		ItemStack stack = pPlayer.getItemInHand(pHand);
		EquipmentSlot slot = Mob.getEquipmentSlotForItem(stack);

		if (MinecraftForge.EVENT_BUS.post(new PlayerTryEquipEvent(pPlayer, slot, stack,
				PlayerTryEquipEvent.Reason.ARMOUR_RIGHT_CLICK))) {
			info.setReturnValue(InteractionResultHolder.fail(stack));
		}
	}
}
