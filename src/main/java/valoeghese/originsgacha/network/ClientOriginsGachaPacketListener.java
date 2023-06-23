package valoeghese.originsgacha.network;

import com.mojang.logging.LogUtils;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import valoeghese.originsgacha.network.packet.S2CUnlockOriginsSyncPacket;

/**
 * Like {@link net.minecraft.client.multiplayer.ClientPacketListener} but for this mod.
 */
public class ClientOriginsGachaPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void onUnlockedOriginsSync(S2CUnlockOriginsSyncPacket packet, NetworkEvent.Context context) {
		LOGGER.info("Received " + packet);
	}
}
