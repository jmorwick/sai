package net.sourcedestination.sai.analysis;

import net.sourcedestination.sai.analysis.metrics.PathLength;
import net.sourcedestination.sai.db.graph.SampleGraphs;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PathLengthTest {
    @Test
    public void testPathLength() {
        var stat = new PathLength();

        // Graph  a -> b -> c -> d
        //         \_______/^
        // length 1:  a->b, b->c, c->d, a->c           (4)
        // length 2:  a->b->c,  b->c->d, a->c->d       (3)
        // length 3:  a->b->c->d                       (1)
        // 4*1 + 3*2 + 3*1 = 13
        // 10/8 =   1.625  (avg path length)
        assertEquals(1.625, stat.apply(SampleGraphs.getSmallGraph1()), 0.005);


        // Graph:
        //   1 --- 2
        //   |   /
        //   |  /
        //   | /
        //   3 --- 4
        // length 1: 1->2, 1->3, 2->1, 2->3, 3->1, 3->2, 3->4, 4->3    (8)
        // length 2: 1->2->3, 1->3->2, 1->3->4, 2->1->3, 2->3->1, 2->3->4, 3->1->2, 3->2->1, 4->3->1, 4->3->2  (10)
        // length 3: 1->2->3->4, 2->1->3->4, 4->3->1->2, 4->3->2->1  (4)
        // 1*8 + 2*10 + 3*4 == 40
        // 40 / 22 = 1.8181818181818
        assertEquals(1.8181818181818, stat.apply(SampleGraphs.getSmallGraph9()), 0.005);
    }

}
