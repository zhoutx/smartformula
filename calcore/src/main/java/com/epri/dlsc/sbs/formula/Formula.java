package com.epri.dlsc.sbs.formula;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import com.epri.dlsc.sbs.dataset.DataSetExpression;
import com.epri.dlsc.sbs.dataset.DataSetJSObject;
import com.epri.dlsc.sbs.engine.ScriptEngine;
import com.epri.dlsc.sbs.exception.CalEngineException;
import com.epri.dlsc.sbs.parser.BaseParser;
import com.epri.dlsc.sbs.function.Function;
import com.epri.dlsc.sbs.function.FunctionExpression;
import com.epri.dlsc.sbs.util.CommonUtil;

//公式定义
public class Formula implements Serializable {

    private FormulaDefine formulaDefine;

    private String formulaId;// 公式定义id

    private String formulaName;// 公式定义名称

    private String datasetId;// 数据集id，该公式定义关联的数据集

    private String phyIdField;// 主体字段，该公式定义指定数据集主体字段

    private Map<String, String> filter;// 数据源过滤条件，json格式

    private List<FormulaItem> formulaItems;//公式计算子项

    private HashMap<String, FormulaItem> formulaItemsMapping;//公式子项Map

    private Set<String> relatedPhyIDs;//公式对应主体

    public Formula(FormulaDefine formulaDefine, String formulaId,
                   String formulaName, String datasetId, String phyIdField,
                   Map<String, String> filter) {
        this.formulaDefine = formulaDefine;
        this.formulaId = formulaId;
        this.formulaName = formulaName;
        this.datasetId = datasetId;
        this.phyIdField = phyIdField;
        this.filter = filter;
        this.formulaDefine.addUsedDataSetID(datasetId);
    }

    public void addFormulaItems(List<FormulaItem> formulaItems) {
        if (this.formulaItems == null) {
            this.formulaItems = new ArrayList<FormulaItem>();
        }
        if (formulaItems != null) {
            if (formulaItemsMapping == null) {
                formulaItemsMapping = new HashMap<String, FormulaItem>();
            }
            for (FormulaItem item : formulaItems) {
                item.setFormula(this);
                this.formulaItems.add(item);
                formulaItemsMapping.put(item.getID(), item);
            }
        }
    }

    public void addRelatedPhyID(String phyID) {
        if (relatedPhyIDs == null) {
            relatedPhyIDs = new HashSet<String>();
        }
        relatedPhyIDs.add(phyID);
        formulaDefine.phyFormulaRelation(this, phyID);
    }

    public List<FormulaItem> getFormulaItems() {
        return formulaItems;
    }

    public FormulaItem getFormualItem(String ID) {
        return formulaItemsMapping == null ? null : formulaItemsMapping.get(ID);
    }

    public String getFormulaId() {
        return formulaId;
    }

    public String getFormulaName() {
        return formulaName;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public String getPhyIdField() {
        return phyIdField;
    }

    public Map<String, String> getFilter() {
        return filter;
    }

    public FormulaDefine getFormulaDefine() {
        return this.formulaDefine;
    }

    public Set<String> getRelatedPhyIDs() {
        return relatedPhyIDs;
    }

    /**
     * 公式项
     *
     * @author zhoutx
     */
    public static class FormulaItem implements Serializable {

        private Formula formula;

        private String ID;// 子公式ID

        private String calculateField;// 公式计算项

        private String calculateFieldName;// 公式项名称

        private String formulaContent;// 公式内容

        private String formulaText;// 公式中文内容

        private String formulaScript;// 公式脚本

        private transient volatile CompiledScript compiledScript;// 编译结果

        private boolean isConstant;// 公式为常量否?

        private int datasetVarCounter = 1;// 【辅助参数】数据集变量计数器

        private int formulaVarCounter = 1;// 【辅助参数】公式变量计数器

        private int functionVarCounter = 1;// 【辅助参数】函数变量计数器

        private Map<String, Object> runtimeContext = new HashMap<String, Object>();

        public FormulaItem(String ID, String calculateField, String calculateFieldName, String formulaContent, String formulaText) {
            this.ID = ID;
            this.calculateField = calculateField;
            this.calculateFieldName = calculateFieldName;
            this.formulaContent = formulaContent;
            this.formulaText = formulaText;
        }

        public boolean isCompiled() {
            return compiledScript != null;
        }

        // 公式编译
        public synchronized void compile() {

            if (formulaContent == null || formulaContent.equals("")) {
                String msg = "公式[" + this.ID + "]内容不能为空";
                throw new CalEngineException(msg);
            }
            formulaScript = formulaContent;
            // 公式为常量否?
            isConstant = BaseParser.constant.matches(formulaScript);

            // 公式为常量
            if (isConstant) {
                // 编译脚本
                compileScript();
            }
            // 公式不为常量
            else {
                // 替换嵌套公式
                replaceFormula();
                // 包装函数
                WrapFunction();
                // 包装数据集
                WrapDataSet();
                WrapDataSetJSObject();
                // 编译脚本
                compileScript();
            }
        }

        // 替换公式
        private void replaceFormula() {
            List<FormulaExps> formulaExpsList = BaseParser.formula.getAllList(formulaScript);
            if (formulaExpsList != null && formulaExpsList.size() > 0) {
                for (int i = 0; i < formulaExpsList.size(); i++, formulaVarCounter++) {
                    FormulaExps formulaExps = formulaExpsList.get(i);
                    String formulaId = formulaExps.getFormulaId();
                    String exps = formulaExps.getExps();
                    String var = CommonUtil.toFormulaVar(formulaVarCounter);
                    String formulaContent = getFormulaContent();
                    if (formulaContent == null) {
                        String msg = "引用的公式[" + formulaId + "]不存在或内容为空！";
                        throw new NullPointerException(msg);
                    }
                    formulaScript = formulaScript.replace(exps, var);
                    StringBuffer script = new StringBuffer();
                    script.append(" var " + var);
                    FormulaItem formulaItem = getFormula().getFormulaDefine().getFormulaItem(formulaId);
                    script.append(" = (function(){" + formulaItem.getFormulaContent() + "})();");
                    formulaScript = script.toString() + formulaScript;
                }
            }
            if (BaseParser.formula.contains(formulaScript)) {
                replaceFormula();
            }
        }

        // 提取数据集
        private void WrapDataSet() {
            List<DataSetExpression> datasetExpsList = BaseParser.dataset.getAllList(formulaScript);
            if (datasetExpsList != null && datasetExpsList.size() > 0) {
                for (int i = 0; i < datasetExpsList.size(); i++, datasetVarCounter++) {
                    DataSetExpression datasetExps = datasetExpsList.get(i);
                    String dataSetID = datasetExps.getDataSetId();
                    String var = CommonUtil.toDatasetVar(datasetVarCounter);
                    String exps = datasetExps.getExpressionText();
                    formulaScript = formulaScript.replace(exps, "$datasetWrapper.eval(" + var + ",$calRow,$formulaItemContext)");
                    runtimeContext.put(var, datasetExps);
                    getFormula().formulaDefine.addUsedDataSetID(dataSetID);
                }
            }
        }

        /**
         * 数据集对象
         * 格式:var netEnergy = $("上网电量数据源").get("上网电量");
         */
        private void WrapDataSetJSObject() {
            List<DataSetJSObject> dataSetJSObjectList = BaseParser.dataSetJSObject.getAllList(formulaScript);
            if (dataSetJSObjectList != null && dataSetJSObjectList.size() > 0) {
                for (DataSetJSObject jsObject : dataSetJSObjectList) {
                    String expression = jsObject.getExpression();
                    String dataSetID = jsObject.getDataSetID();
                    formulaScript = formulaScript.replace(expression, "$formulaItemContext.returnDataSetJsObject(" + dataSetID + ")");
                }
            }
        }

        // 提取函数
        private void WrapFunction() {
            List<FunctionExpression> functionExpsList = BaseParser.function.getAllList(formulaContent);
            if (functionExpsList != null && functionExpsList.size() > 0) {
                for (int i = 0; i < functionExpsList.size(); i++, functionVarCounter++) {
                    FunctionExpression functionExps = functionExpsList.get(i);
                    // 变量名称
                    String var = CommonUtil.toFunctionVar(functionVarCounter);
                    // 表达式
                    String expression = functionExps.getExpression();
                    FunctionExpression.ArgumentValue[] functionArgList = functionExps.getFunctionArgs();
                    String functionWrapper = "$functionWrapper.eval(" + var + ",$calRow,[";
                    if (functionArgList != null && functionArgList.length > 0) {
                        String variables = "";
                        for (int j = 0; j < functionArgList.length; j++) {
                            FunctionExpression.ArgumentValue argExps = functionArgList[j];

                            if (argExps.dataType == Function.FunctionArg.VARIABLE) {// 变量
                                variables += "," + argExps.value;
                            } else {
                                variables += ",";
                            }
                        }
                        if (variables != null && variables.length() > 0) {
                            variables = variables.substring(1);
                            functionWrapper += variables;
                        }
                    }
                    functionWrapper += "])";
                    formulaScript = formulaScript.replace(expression, functionWrapper);
                    runtimeContext.put(var, functionExps);
                    FunctionExpression.ArgumentValue[] argValues = functionExps.getFunctionArgs();
                    if (argValues != null) {
                        for (FunctionExpression.ArgumentValue argValue : argValues) {
                            if (argValue.dataType == Function.FunctionArg.DATASET) {
                                getFormula().formulaDefine.addUsedDataSetID(((DataSetExpression) argValue.value).getDatasetId());
                            }
                        }
                    }
                }
            }
        }

        // 编译脚本
        private void compileScript() {
            StringBuffer script = new StringBuffer();
            script.append("(function(){")
                    .append(formulaScript)
                    .append("})()");
            formulaScript = script.toString();
            try {
                compiledScript = (CompiledScript) ScriptEngine.getCompilable().compile(formulaScript);
            } catch (ScriptException e) {
                String msg = "公式[" + ID + "]编译失败[" + formulaScript + "]";
                throw new CalEngineException(msg);
            }
        }

        private void setFormula(Formula formula) {
            this.formula = formula;
        }

        public Formula getFormula() {
            return this.formula;
        }

        public String getID() {
            return this.ID;
        }

        public String getCalculateField() {
            return calculateField;
        }

        public String getCalculateFieldName() {
            return this.calculateFieldName;
        }

        public String getFormulaContent() {
            return formulaContent;
        }

        public String getFormulaText() {
            return formulaText;
        }

        public String getFormulaScript() {
            return formulaScript;
        }

        public CompiledScript getCompiledScript() {
            return compiledScript;
        }

        public boolean isConstant() {
            return isConstant;
        }

        public Map<String, Object> getRuntimeContext() {
            return runtimeContext;
        }

    }
}
