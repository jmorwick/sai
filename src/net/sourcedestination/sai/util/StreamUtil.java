package net.sourcedestination.sai.util;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamUtil {
	
	public static <T> Stream<T> listen(Stream<T> s, Consumer<T> listener) {
		Consumer<T> listenerSafe = !s.isParallel() ? 
		new Consumer<T>() {
			@Override
			public synchronized void accept(T t) {
				listener.accept(t);
			}				
		} : listener;
		
		return StreamSupport.stream(listen(s.spliterator(), listenerSafe), s.isParallel());
	}
	
	public static <T> Spliterator<T> listen(Spliterator<T> s, Consumer<T> listener) {
		return new Spliterator<T>() {

			@Override
			public boolean tryAdvance(Consumer<? super T> action) {
				return  s.tryAdvance(listener.andThen(action));
			}

			@Override
			public Spliterator<T> trySplit() {
				Spliterator<T> split = s.trySplit();
				if(split == null) return null;
				return listen(split, listener);
			}

			@Override
			public long estimateSize() {
				return s.estimateSize();
			}

			@Override
			public int characteristics() {
				return s.characteristics();
			}
			
		};
	}
}
