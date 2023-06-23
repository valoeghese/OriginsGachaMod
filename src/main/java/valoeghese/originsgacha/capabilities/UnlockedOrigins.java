package valoeghese.originsgacha.capabilities;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import valoeghese.originsgacha.network.NetworkManager;
import valoeghese.originsgacha.network.packet.S2CUnlockOriginsSyncPacket;

import java.util.ArrayList;
import java.util.List;

public class UnlockedOrigins implements IUnlockedOrigins, ICapabilitySerializable<CompoundTag> {
	public UnlockedOrigins(Player player) {
		this.player = player;
		this.unlockedOrigins = new ArrayList<>();

		// sync server-client upon player login.
		// this field is unused client side, however this is done rather than "true" to make the purpose more clear.
		// and prevent any issues should any changes be made to synchronisation for some reason.
		this.shouldSync = player instanceof ServerPlayer;
	}

	private final transient Player player;
	private final transient LazyOptional<IUnlockedOrigins> lazyOptionalOfThis = LazyOptional.of(() -> this);
	private transient boolean shouldSync;

	private final List<ResourceKey<Origin>> unlockedOrigins;

	// IUnlockedOrigins

	@Override
	public void tick() {
		if (this.shouldSync && this.player instanceof ServerPlayer player) {
			this.shouldSync = false;

			NetworkManager.sendToPlayer(player, new S2CUnlockOriginsSyncPacket(
					S2CUnlockOriginsSyncPacket.UpdateType.REPLACE_ORIGINS,
					this.unlockedOrigins
			));
		}
	}

	@Override
	public List<ResourceKey<Origin>> getUnlockedOrigins() {
		return List.of();
	}

	@Override
	public Player getOwner() {
		return this.player;
	}


	// Capability and Serialisation

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return CAPABILITY.orEmpty(cap, this.lazyOptionalOfThis);
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
	}

	public static final ResourceLocation ID = new ResourceLocation("origins_gacha", "unlocked_origins");
	public static final Capability<IUnlockedOrigins> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
}
