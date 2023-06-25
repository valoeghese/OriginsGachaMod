package valoeghese.originsgacha;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The config for origins gacha.
 */
public class OriginsGachaConfig {
	OriginsGachaConfig(ForgeConfigSpec.Builder builder) {
		this.switchCooldownSeconds = builder.defineInRange("switchCooldownSeconds", 60 * 20.0,
				0.0, 60 * 60.0);
	}

	private final ForgeConfigSpec.DoubleValue switchCooldownSeconds;

	public double getCoolDownSeconds() {
		return this.switchCooldownSeconds.get();
	}

	public static final OriginsGachaConfig CONFIG;
	public static final ForgeConfigSpec CONFIG_SPEC;

	static {
		Pair<OriginsGachaConfig, ForgeConfigSpec> configSpecPair = new ForgeConfigSpec.Builder()
				.configure(OriginsGachaConfig::new);

		CONFIG = configSpecPair.getLeft();
		CONFIG_SPEC = configSpecPair.getRight();
	}

}
