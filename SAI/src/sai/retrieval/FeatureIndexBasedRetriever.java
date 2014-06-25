package sai.retrieval;

import java.util.Iterator;
import java.util.Set;

import sai.db.DBInterface;
import sai.graph.Feature;

public interface FeatureIndexBasedRetriever {
    public abstract Iterator<Integer> retrieve(DBInterface db, Set<Feature> indices);
}
