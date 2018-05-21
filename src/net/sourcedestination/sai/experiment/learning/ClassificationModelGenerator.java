package net.sourcedestination.sai.experiment.learning;

import net.sourcedestination.funcles.function.Function2;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.db.graph.Graph;

import java.util.function.Function;

public interface ClassificationModelGenerator extends Function2<
        DBInterface,
        Function<Graph,String>,
        ClassificationModel> {
}
