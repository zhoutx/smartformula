package com.epri.dlsc.sbs.formula;


public class FormulaExps2 {
	// 数据集完整表达式
	private String exps;
	// 公式id
	private String formulaId;

	public FormulaExps2(String exps, String formulaId) {
		this.exps = exps;
		this.formulaId = formulaId;
	}

	public String getExps() {
		return exps;
	}

	public void setExps(String exps) {
		this.exps = exps;
	}

	public String getFormulaId() {
		return formulaId;
	}

	public void setFormulaId(String formulaId) {
		this.formulaId = formulaId;
	}

}
