package valoeghese.originsgacha.network;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import valoeghese.originsgacha.capabilities.IUnlockedOrigins;
import valoeghese.originsgacha.capabilities.UnlockedOrigins;
import valoeghese.originsgacha.impl.ChooseOriginScreenAccess;
import valoeghese.originsgacha.network.packet.S2CUnlockedOriginsSyncPacket;

/**
 * Like {@link net.minecraft.client.multiplayer.ClientPacketListener} but for this mod.
 */
public final class ClientOriginsGachaPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void onUnlockedOriginsSync(S2CUnlockedOriginsSyncPacket packet, NetworkEvent.Context context) {
		LOGGER.info("Received " + packet);

		Minecraft.getInstance().tell(() -> {
			assert Minecraft.getInstance().player != null;
			IUnlockedOrigins container = IUnlockedOrigins.getUnlockedOrigins(Minecraft.getInstance().player);

			if (container instanceof UnlockedOrigins impl) {
				impl.onSync(packet);
			}

			// if the screen is a choose origin screen and it's not yet been modified (and should be), modify it.
			if (Minecraft.getInstance().screen instanceof ChooseOriginScreenAccess chooseOriginScreen) {
				chooseOriginScreen.correctDisplayedOrigins(true);
			}
		});
	}
}
