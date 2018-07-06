package com.epri.dlsc.sbs.formula;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.epri.dlsc.sbs.formula.Formula.FormulaItem;



/**
 * 有关公式信息
 */
public class FormulaDefine implements Serializable{
	
	private Map<String, Formula> formulaMap;
	private Map<String, PhyFormula> phyAndFormulaMapping;//主体对应公式
	private List<Formula> orderedFormula;//有序的所有公式
	private Set<String> usedDataSetIDs;
	
	public FormulaItem getFormulaItem(String ID){
		if(orderedFormula != null){
			for(Formula formula : orderedFormula){
				FormulaItem formulaItem = formula.getFormualItem(ID);
				if(formulaItem != null){
					return formulaItem;
				}
			}
		}
		return null;
	}

	public boolean hasFormula(String formulaId){
		return !(formulaMap == null) && formulaMap.containsKey(formulaId);
	}
	//添加公式
	public void addFormula(Formula formula, String phyID){
		if(formulaMap == null){
			formulaMap = new HashMap<String, Formula>();
		}
		formulaMap.put(formula.getFormulaId(), formula);
		//与公式作关联
		formula.addRelatedPhyID(phyID);
		if(orderedFormula == null){
			orderedFormula = new ArrayList<Formula>();
		}
		//有序的公式集
		orderedFormula.add(formula);
        //维护主体对应公式的关系
        phyFormulaRelation(formula, phyID);
	}

	void phyFormulaRelation(Formula formula, String phyID){
        //关联主体对应的公式关系
        if (phyAndFormulaMapping == null) {
            phyAndFormulaMapping = new HashMap<String, PhyFormula>();
        }
        if (!phyAndFormulaMapping.containsKey(phyID)) {
            phyAndFormulaMapping.put(phyID, new PhyFormula());
        }
        PhyFormula phyFormula = phyAndFormulaMapping.get(phyID);
        phyFormula.add(formula.getFormulaId());
    }

	/**
	 * 根据主体获取公式集
	 * @param phyId
	 * @return
	 */
	public List<Formula> getFormulasByPhyId(String phyId){
		if(phyAndFormulaMapping == null){
			return null;
		}
		PhyFormula phyFormula = phyAndFormulaMapping.get(phyId);
		if(phyFormula == null){
			return null;
		}
		return phyFormula.formulas;
	}
	/**
	 * 获取所有公式
	 * @return
	 */
	public List<Formula> getFormulas(){
		return orderedFormula;
	}
	
	void addUsedDataSetID(String dataSetId){
		if(usedDataSetIDs == null){
			usedDataSetIDs = new HashSet<String>();
		}
		usedDataSetIDs.add(dataSetId);
	}

	public Set<String> getUsedDataSetIDS() {
		return usedDataSetIDs;
	}
	
	/**
	 * 根据公式ID获取公式
	 * @param formulaId
	 * @return
	 */
	public Formula getFormula(String formulaId){
		if(formulaMap == null){
			return null;
		}
		return formulaMap.get(formulaId);
				
	}

	private class PhyFormula implements Serializable{
		Set<String> formulaIdSet = new HashSet<String>();
		List<Formula> formulas = new ArrayList<Formula>();
		void add(String formulaId){
			if(!formulaIdSet.contains(formulaId)){
				Formula formula = formulaMap.get(formulaId);
				if(formula != null){
					formulaIdSet.add(formulaId);
					formulas.add(formula);
				}
			}
		}
	}
}
