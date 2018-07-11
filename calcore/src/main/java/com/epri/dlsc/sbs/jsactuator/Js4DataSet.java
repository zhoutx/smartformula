package com.epri.dlsc.sbs.jsactuator;

import com.epri.dlsc.sbs.dataset.DataSetExpression;
import com.epri.dlsc.sbs.dataset.ResultSet2;

public class Js4DataSet {

    public DataSetJSObject toObject(DataSetExpression dataSetExpression, ResultSet2.Row row){
        DataSetJSObject[] objs = toArrayObject(dataSetExpression, row);
        return objs == null ? null : objs[0];
    }

    public DataSetJSObject[] toArrayObject(DataSetExpression dataSetExpression, ResultSet2.Row row){
        return null;
    }

    public boolean isExist(DataSetExpression dataSetExpression, ResultSet2.Row row){
        DataSetJSObject[] objArr = toArrayObject(dataSetExpression, row);
        if(objArr == null || objArr.length == 0){
            return false;
        }
        return true;
    }
}