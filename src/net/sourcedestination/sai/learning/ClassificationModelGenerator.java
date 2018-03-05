package net.sourcedestination.sai.learning;

import net.sourcedestination.funcles.function.Function2;
import net.sourcedestination.sai.db.DBInterface;
import net.sourcedestination.sai.graph.Graph;
import net.sourcedestination.sai.learning.ClassificationModel;

import java.util.function.Function;

public interface ClassificationModelGenerator extends Function2<
        DBInterface,
        Function<Graph,String>,
        ClassificationModel> {
}
