package net.sourcedestination.sai.experiment.learning;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.sourcedestination.funcles.function.Function2;

import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;
import net.sourcedestination.sai.experiment.retrieval.Retriever;

import java.util.Comparator;
import java.util.function.Function;

public interface ClassificationModelGenerator extends Function2<
        DBInterface,
        Function<Graph,String>,
        ClassificationModel> {

    public static ClassificationModelGenerator knnClassifierGenerator(
            Function<DBInterface,Retriever> retrieverGen,
            int k) {
        return (db, classes) -> generateKnnClassifier(db, retrieverGen.apply(db), k, classes);
    }
    public static ClassificationModel generateMajorityClassifier(
            DBInterface db,
            Function<Graph,String> classes) {
        // find majority classification
        var results = HashMultiset.<String>create();
        db.getGraphIDStream()
                .map(db::retrieveGraph) // retrieve full graphs
                .map(classes::apply)    // find the labels for these graphs
                .forEach(results::add);
        var topResult = results.entrySet().stream()
                .sorted(Comparator.comparing(Multiset.Entry::getCount)) // sort by occurrences
                .map(Multiset.Entry::getElement)
                .findFirst().orElse("none");    // return top result
        return g -> topResult;
    }

    public static ClassificationModel generateKnnClassifier(
            DBInterface db,
            Retriever<Graph> retriever,
            int k,
            Function<Graph,String> classes) {
        return g -> {
            var results = HashMultiset.<String>create();
            // add results from k-nearest-neighbors to multiset
            retriever.retrieve(g)
                    .limit(k)               // limit top k results
                    .map(db::retrieveGraph) // retrieve full graphs
                    .map(classes::apply)    // find the labels for these graphs
                    .forEach(results::add); // add labels to multiset

            // return top voted result
            return results.entrySet().stream()
                    .sorted(Comparator.comparing(Multiset.Entry::getCount)) // sort by occurrences
                    .map(Multiset.Entry::getElement)
                    .findFirst().orElse("none");    // return top result
        };
    }
}
