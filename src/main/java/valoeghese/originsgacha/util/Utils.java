package valoeghese.originsgacha.util;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class Utils {
	public static <T, U> Optional<U> apply(FallableFunction<T, @Nullable U> function, T value) {
		try {
			return Optional.ofNullable(function.apply(value));
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	@FunctionalInterface
	public interface FallableFunction<I, O> {
		O apply(I input);
	}
}
