package valoeghese.originsgacha.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for a generic packet that also acts as a decoder for its own type.
 * @param <T> the packet type to manage.
 */
public interface Packet<T extends Packet<T>> {
	/**
	 * Decode the contents of this byte buffer into a new packet.
	 * @param buf the buffer containing data to read and decode.
	 * @return a new packet instance with the decoded data.
	 */
	T decode(FriendlyByteBuf buf);

	/**
	 * Encode the data of this packet into the given byte buffer.
	 * @param buf the buffer to encode the data into.
	 */
	void encode(FriendlyByteBuf buf);

	/**
	 * Get the direction this packet is sent in.
	 * @return the direction to send this packet in.
	 */
	@Nullable
	NetworkDirection getDirection();
}
