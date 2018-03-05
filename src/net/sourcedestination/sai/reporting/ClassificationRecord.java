package net.sourcedestination.sai.reporting;

import net.sourcedestination.funcles.tuple.Tuple4;
import net.sourcedestination.sai.db.DBInterface;

public class ClassificationRecord extends Tuple4<DBInterface,Integer,String,String> {

    public ClassificationRecord(DBInterface db, int gid, String result, String expected) {
        super(db, gid, result, expected);
    }

    public DBInterface getDb() {
        return this._1;
    }

    public int getGraphId() {
        return this._2;
    }

    public String getResult() {
        return this._3;
    }

    public String getExpected() {
        return this._4;
    }
}
