package com.epri.dlsc.sbs.inter.loader;

import java.io.Serializable;
import java.util.List;

import com.epri.dlsc.sbs.dataset.DataSetDefine;
import com.epri.dlsc.sbs.formula.FormulaDefine;
import com.epri.dlsc.sbs.function.FunctionDefine;
import com.epri.dlsc.sbs.formula.Formula;
import com.epri.dlsc.sbs.inter.db.DBCollection;
import com.epri.dlsc.sbs.inter.db.DBManager;

public abstract class BaseLoader implements DataSource, Serializable {
    //	final static public ReentrantReadWriteLock calLock = new ReentrantReadWriteLock();
    private FormulaDefine formulaDefine;// 计算公式定义
    private FunctionDefine functionDefine;// 函数算法定义
    private DataSetDefine dataSetDefine;// 计算数据集定义

    /**
     * 初始化（必须在子类显式调用）
     */
    public void loadInit(LoaderFunction function, DBCollection dbCollection) {
        //1、注册数据库连接
        DBManager.registerDBCollection(dbCollection);
        //2、计算基础数据加载
        formulaDefine = function.loadFormula();
        if (formulaDefine == null) {
            System.out.println("没有找到待计算的公式定义信息");
        }
        List<Formula> formulas = formulaDefine.getFormulas();
        if(formulas != null){
            for(Formula formula : formulas){
                List<Formula.FormulaItem> formulaItems = formula.getFormulaItems();
                if(formulaItems != null){
                    for(Formula.FormulaItem item : formulaItems){
                        if(!item.isCompiled()){
                            item.compile();
                        }
                    }
                }
            }
        }
        functionDefine = function.loadFunction();
        dataSetDefine = function.loadDataSet(formulaDefine.getUsedDataSetIDS());
        if (dataSetDefine == null) {
            System.out.println("没有找到待计算的数据源定义信息");
        } else {
            dataSetDefine.loadOrUpdate();
        }
    }

    /**
     * 注册数据库连接
     * @param dbCollection
     */
    public void registerDBCollection(DBCollection dbCollection){
        DBManager.registerDBCollection(dbCollection);
    }


    @Override
    /**
     * 重新加载数据源数据
     * 需要时此方法需要在派生类中重写
     */
    public void loadOrUpdate(Object param) {
    }

    @Override
    public DataSetDefine getDataSetDefine() {
        return dataSetDefine == null ? new DataSetDefine() : dataSetDefine;
    }

    @Override
    public FormulaDefine getFormulaDefine() {
        return formulaDefine == null ? new FormulaDefine() : formulaDefine;
    }

    @Override
    public FunctionDefine getFunctionDefine() {
        return functionDefine == null ? new FunctionDefine() : functionDefine;
    }
}
