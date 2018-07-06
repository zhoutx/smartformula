package com.epri.dlsc.sbs.inter.loader;

import com.epri.dlsc.sbs.dataset.DataSetDefine;
import com.epri.dlsc.sbs.formula.FormulaDefine;
import com.epri.dlsc.sbs.function.FunctionDefine;

public interface DataSource {
	/**
	 * 计算用到的数据集定义信息
	 * @return CalDataSetDefine
	 */
	public DataSetDefine getDataSetDefine();
	/**
	 * 计算用到的公式定义信息
	 * @return CalFormulaDefine
	 */
	public FormulaDefine getFormulaDefine();
	/**
	 * 计算用到的函数算法定义信息
	 * @return CalFunctionDefine
	 */
	public FunctionDefine getFunctionDefine();
	/**
	 * 获取计算主体ID
	 * @return Set<String>
	 */
	public String[] getCalPhyIds();
	/**
	 * 重新加载数据
	 */
	public void loadOrUpdate(Object param);
}
