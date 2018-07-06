package com.epri.dlsc.sbs.inter.loader;

import com.epri.dlsc.sbs.dataset.DataSetDefine;
import com.epri.dlsc.sbs.formula.FormulaDefine;
import com.epri.dlsc.sbs.function.FunctionDefine;

import java.util.Set;

public interface LoaderFunction{
	public FormulaDefine loadFormula();
	public FunctionDefine loadFunction();
	public DataSetDefine loadDataSet(Set<String> dataSetIDs);
}
