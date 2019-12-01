package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.analysis.metrics.EigenvalueCount;
import net.sourcedestination.sai.db.graph.SampleGraphs;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EigenValueTest {

    @Test
    public void testPathLength() {
        var stat = new EigenvalueCount();

        // Graph  a -> b -> c -> d
        //         \_______/^
        // 0, 1, 0, 0
        // 0, 0, 1, 1
        // 0, 0, 0, 1
        // 0, 0, 0, 0
        //
        // \mathrm{Solve\:}\:λ^4=0:\quad λ=0\mathrm{\:with\:multiplicity\:of\:}4
        //assertEquals(0.25, stat.apply(SampleGraphs.getSmallGraph1()), 0.005);


        // Graph:
        //   1 --- 2
        //   |   /
        //   |  /
        //   | /
        //   3 --- 4
        // \mathrm{Solve\:}\:λ^4-4λ^2-2λ+1=0:\quad λ=-1,\:λ\approx \:0.31110\dots ,\:λ\approx \:-1.48119\dots ,\:λ\approx \:2.17008\dots
        assertEquals(1.0, stat.apply(SampleGraphs.getSmallGraph9()), 0.005);
    }

}
