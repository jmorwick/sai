package net.sourcedestination.sai.db.indexing;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.experiment.retrieval.Retriever;
import net.sourcedestination.sai.util.Task;

import java.util.function.Function;

public interface IndexMiner<DB extends DBInterface, I> extends Function<DB,Task<Retriever<I>>> {
}
