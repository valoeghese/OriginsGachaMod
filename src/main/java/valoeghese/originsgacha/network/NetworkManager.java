package valoeghese.originsgacha.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import valoeghese.originsgacha.network.packet.Packet;
import valoeghese.originsgacha.network.packet.S2CUnlockOriginsSyncPacket;
import valoeghese.originsgacha.util.Utils;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * Handles the networking for this mod.
 */
public class NetworkManager {
	private static final int PROTOCOL_VERSION = 0;

	private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
			new ResourceLocation("origins_gacha", "sync"),
			() -> String.valueOf(PROTOCOL_VERSION),
			ver -> Utils.apply(Integer::parseInt, ver).map(i -> i <= PROTOCOL_VERSION).orElse(false),
			ver -> Utils.apply(Integer::parseInt, ver).map(i -> i <= PROTOCOL_VERSION).orElse(false)
	);

	public static void setup() {
		registerPacket(0, new S2CUnlockOriginsSyncPacket(null, null), ClientOriginsGachaPacketListener::onUnlockedOriginsSync);
	}

	public static <T extends Packet<T>> void sendToPlayer(ServerPlayer player, T packet) {
		CHANNEL.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
	}

	public static <T extends Packet<T>> void sendToServer(T packet) {
		CHANNEL.sendToServer(packet);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Packet<T>> void registerPacket(int id, T packet, BiConsumer<T, NetworkEvent.Context> handler) {
		CHANNEL.registerMessage(
				id,
				(Class<T>) packet.getClass(),
				Packet::encode,
				packet::decode,
				(msg, ctxSupplier) -> handler.accept(msg, ctxSupplier.get()),
				Optional.ofNullable(packet.getDirection())
		);
	}
}
