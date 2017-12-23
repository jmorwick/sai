package net.sourcedestination.sai.util;

import com.google.common.collect.Lists;

import java.util.List;
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

	/** concatenates 2 lists together
	 * TODO: consider a more efficient, linked-list based implementation, or 3rd party lib
	 */
	public static <T> List<T> concatenateLists(List<T> a, List<T> b) {
		List<T> c = Lists.newArrayList(a);
		c.addAll(b);
		return c;
	}
}
