package valoeghese.originsgacha.network.packet;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Packet for synchronising unlocked origins from the server to the client.
 */
public class S2CUnlockOriginsSyncPacket implements Packet<S2CUnlockOriginsSyncPacket> {
	/**
	 * Refers to a type of update that could occur.
	 */
	public enum UpdateType {
		/**
		 * Replace origins. This should be the standard for synchronisation.
		 */
		REPLACE_ORIGINS,
		/**
		 * Add origins. This is used when spinning the wheel to unlock new origins. The wheel screen will open
		 * and select the first origin in the list.
		 */
		ADD_ORIGINS
	}

	/**
	 * Create a packet for the given update type, with the given unlocked origins.
	 * @param updateType the type of update to perform.
	 * @param origins the list of unlocked origins.
	 */
	public S2CUnlockOriginsSyncPacket(UpdateType updateType, List<ResourceKey<Origin>> origins) {
		this.updateType = updateType;
		this.origins = origins;
	}

	private final UpdateType updateType;
	private final List<ResourceKey<Origin>> origins;

	/**
	 * Get the update type. This determines how the packet should modify the client origin list.
	 * @return the update type.
	 */
	public UpdateType getUpdateType() {
		return this.updateType;
	}

	/**
	 * Get the list of unlocked origins.
	 * @return the list of unlocked origins.
	 */
	public List<ResourceKey<Origin>> getOrigins() {
		return this.origins;
	}

	@Override
	public S2CUnlockOriginsSyncPacket decode(FriendlyByteBuf buf) {
		// Read Update Type
		UpdateType type = UpdateType.values()[buf.readByte()];

		// Read Origins
		List<ResourceKey<Origin>> origins = new LinkedList<>();

		int nElements = buf.readShort();

		for (int i = 0; i < nElements; i++) {
			origins.add(ResourceKey.create(
					OriginsDynamicRegistries.ORIGINS_REGISTRY,
					buf.readResourceLocation()
			));
		}

		return new S2CUnlockOriginsSyncPacket(type, origins);
	}

	@Override
	public void encode(FriendlyByteBuf buf) {
		// Write Update Type
		buf.writeByte(this.updateType.ordinal());

		// Write Origins
		buf.writeShort(this.origins.size());

		for (ResourceKey<Origin> originKey : this.origins) {
			buf.writeResourceLocation(originKey.location());
		}
	}

	@Override
	public @Nullable NetworkDirection getDirection() {
		return NetworkDirection.PLAY_TO_CLIENT;
	}

	@Override
	public String toString() {
		return "S2CUnlockOriginsSyncPacket{" +
				"updateType=" + updateType +
				", origins=" + origins +
				'}';
	}
}
