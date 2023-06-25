package valoeghese.originsgacha.network.packet;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;

/**
 * Packet sent from the client to the server to indicate it wants to switch its origin to another unlocked origin.
 */
public class C2SSwitchOriginPacket implements Packet<C2SSwitchOriginPacket> {
	/**
	 * Create an origin switch packet for the given origin.
	 * @param origin the origin to switch to.
	 */
	public C2SSwitchOriginPacket(ResourceKey<Origin> origin) {
		this.origin = origin;
	}

	private final ResourceKey<Origin> origin;

	/**
	 * Get the origin the client wants to switch to.
	 * @return the origin the client wants to switch to.
	 */
	public ResourceKey<Origin> getOrigin() {
		return this.origin;
	}

	@Override
	public C2SSwitchOriginPacket decode(FriendlyByteBuf buf) {
		ResourceKey<Origin> origin = ResourceKey.create(
				OriginsDynamicRegistries.ORIGINS_REGISTRY,
				buf.readResourceLocation()
		);

		return new C2SSwitchOriginPacket(origin);
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeResourceLocation(this.origin.location());
	}

	@Override
	public @Nullable NetworkDirection getDirection() {
		return NetworkDirection.PLAY_TO_SERVER;
	}

	@Override
	public String toString() {
		return "C2SSwitchOriginPacket{" +
				"origin=" + this.origin +
				'}';
	}
}
