package valoeghese.originsgacha.capabilities;

import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.registry.OriginsDynamicRegistries;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
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

		// Write unlocked origins
		ListTag unlockedOriginsList = new ListTag();

		for (ResourceKey<Origin> originKey : this.unlockedOrigins) {
			unlockedOriginsList.add(StringTag.valueOf(originKey.location().toString()));
		}

		tag.put("unlockedOrigins", unlockedOriginsList);

		return tag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		// Read Unlocked Origins
		if (nbt.contains("unlockedOrigins", Tag.TAG_LIST)) {
			this.unlockedOrigins.clear(); // just in case

			ListTag unlockedOriginsList = nbt.getList("unlockedOrigins", Tag.TAG_STRING);
			final int size = unlockedOriginsList.size();

			for (int i = 0; i < size; i++) {
				ResourceLocation originId = new ResourceLocation(unlockedOriginsList.getString(i));
				this.unlockedOrigins.add(ResourceKey.create(OriginsDynamicRegistries.ORIGINS_REGISTRY, originId));
			}
		}

		this.shouldSync = true;
	}

	public static final ResourceLocation ID = new ResourceLocation("origins_gacha", "unlocked_origins");
	public static final Capability<IUnlockedOrigins> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
}
