package valoeghese.originsgacha.network;

import com.mojang.logging.LogUtils;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import valoeghese.originsgacha.network.packet.C2SRollOriginPacket;
import valoeghese.originsgacha.network.packet.Packet;

/**
 * Like {@link net.minecraft.network.protocol.game.ServerGamePacketListener} but for this mod.
 */
public final class ServerOriginsGachaPacketListener {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void onRoll(C2SRollOriginPacket packet, NetworkEvent.Context context) {
		LOGGER.info("Received " + packet);
	}
}
