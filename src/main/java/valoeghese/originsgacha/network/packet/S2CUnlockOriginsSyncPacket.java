package valoeghese.originsgacha.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;

/**
 * Packet for synchronising unlocked origins from the server to the client.
 */
public class S2CUnlockOriginsSyncPacket implements Packet<S2CUnlockOriginsSyncPacket> {
	@Override
	public S2CUnlockOriginsSyncPacket decode(FriendlyByteBuf buf) {
		return new S2CUnlockOriginsSyncPacket();
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
	}

	@Override
	public @Nullable NetworkDirection getDirection() {
		return NetworkDirection.PLAY_TO_CLIENT;
	}
}
