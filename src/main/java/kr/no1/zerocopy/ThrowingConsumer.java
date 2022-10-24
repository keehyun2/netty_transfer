package kr.no1.zerocopy;

import kr.no1.zerocopy.util.ReadSockUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T> extends Consumer<T> {

	Logger LOGGER = LoggerFactory.getLogger(ThrowingConsumer.class);

	@Override
	default void accept(final T elem) {
		try {
			acceptThrows(elem);
		} catch (final Exception e) {
			LOGGER.error("handling an exception...", e);
		}
	}

	void acceptThrows(T elem) throws Exception;

}