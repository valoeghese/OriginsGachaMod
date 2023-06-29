package valoeghese.originsgacha.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import valoeghese.originsgacha.network.packet.C2SRollOriginPacket;
import valoeghese.originsgacha.network.packet.C2SSwitchOriginPacket;
import valoeghese.originsgacha.network.packet.Packet;
import valoeghese.originsgacha.network.packet.S2CUnlockedOriginsSyncPacket;
import valoeghese.originsgacha.util.Utils;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

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
		// 0XX: Client to Server Packets
		registerServerboundPacket(0, new C2SRollOriginPacket(), ServerOriginsGachaPacketListener::onRoll);
		registerServerboundPacket(1, new C2SSwitchOriginPacket(null), ServerOriginsGachaPacketListener::onSwitchOrigin);

		// 1XX: Server to Client Packets
		// stuipid frog modloader transformer means i have to do this shit instead of using a method reference
		registerClientboundPacket(100, new S2CUnlockedOriginsSyncPacket(null, null), p -> ClientOriginsGachaPacketListener.onUnlockedOriginsSync(p));
	}

	public static <T extends Packet<T>> void sendToPlayer(ServerPlayer player, T packet) {
		CHANNEL.sendTo(packet, player.connection.getConnection(), NetworkDirection.PLAY_TO_CLIENT);
	}

	public static <T extends Packet<T>> void sendToServer(T packet) {
		CHANNEL.sendToServer(packet);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Packet<T>> void registerServerboundPacket(int id, T packet, BiConsumer<T, NetworkEvent.Context> handler) {
		if (packet.getDirection() != NetworkDirection.PLAY_TO_SERVER) {
			throw new IllegalArgumentException("Can only register Play-To-Server with registerServerboundPacket");
		}

		CHANNEL.registerMessage(
				id,
				(Class<T>) packet.getClass(),
				Packet::encode,
				packet::decode,
				(msg, ctxSupplier) -> {
					handler.accept(msg, ctxSupplier.get());
					ctxSupplier.get().setPacketHandled(true);
				},
				Optional.ofNullable(packet.getDirection())
		);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Packet<T>> void registerClientboundPacket(int id, T packet, Consumer<T> handler) {
		if (packet.getDirection() != NetworkDirection.PLAY_TO_CLIENT) {
			throw new IllegalArgumentException("Can only register Play-To-Client with registerClientboundPacket");
		}

		CHANNEL.registerMessage(
				id,
				(Class<T>) packet.getClass(),
				Packet::encode,
				packet::decode,
				(msg, ctxSupplier) -> {
					ctxSupplier.get().enqueueWork(() -> {
						DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handler.accept(msg));
					});
					ctxSupplier.get().setPacketHandled(true);
				},
				Optional.ofNullable(packet.getDirection())
		);
	}
}
