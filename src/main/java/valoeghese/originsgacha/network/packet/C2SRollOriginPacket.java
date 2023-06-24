package valoeghese.originsgacha.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;

/**
 * Sent from the client to the server when the client wants to roll for a new origin.
 */
public class C2SRollOriginPacket implements Packet<C2SRollOriginPacket> {
	@Override
	public C2SRollOriginPacket decode(FriendlyByteBuf buf) {
		return new C2SRollOriginPacket();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
	}

	@Override
	public @Nullable NetworkDirection getDirection() {
		return NetworkDirection.PLAY_TO_SERVER;
	}

	@Override
	public String toString() {
		return "C2SRollOriginPacket{}";
	}
}
