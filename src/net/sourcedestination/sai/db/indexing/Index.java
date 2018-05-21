package net.sourcedestination.sai.db.indexing;

import java.util.stream.Stream;

@FunctionalInterface
public interface Index<I> {
    Stream<Integer> getRelatedGraphIds(I index);
}