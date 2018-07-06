package com.epri.dlsc.sbs.engine;

import java.util.*;

import com.epri.dlsc.sbs.exception.CalEngineException;
import com.epri.dlsc.sbs.formula.Formula;
import com.epri.dlsc.sbs.inter.datasource.DataSource;
import com.epri.dlsc.sbs.inter.loader.Loader;
import com.epri.dlsc.sbs.jsactuator.DatasetWrapper;
import com.epri.dlsc.sbs.jsactuator.FormulaItemContext;
import com.epri.dlsc.sbs.jsactuator.FunctionWrapper;
import com.epri.dlsc.sbs.jsactuator.Js4DB;
import com.epri.dlsc.sbs.log.Logger;
import com.epri.dlsc.sbs.params.CalculateParam;
import com.epri.dlsc.sbs.params.DataSourceParam;
import com.epri.dlsc.sbs.util.CommonUtil;
import com.epri.dlsc.sbs.dataset.ResultSet;

/**
 * 计算引擎
 */
public class CalculateEngine {

    private static Logger logger = Logger.getLogger(CalculateEngine.class);
    // 函数包装器类
    private FunctionWrapper functionWrapper;
    //数据集包装类
    private DatasetWrapper datasetWrapper;
    // 数据源参数
    private DataSourceParam dataSourceParam;
    //计算参数
    private CalculateParam calculateParam;

    //计算执行状态（不包括保存环节）
    volatile boolean CALCULATE_RUNNING;
    //保存服务执行状态
    volatile boolean SAVE_RUNNING;

    public CalculateEngine(DataSourceParam dataSourceParam, CalculateParam calculateParam) {
        //数据源参数正确性检查
        checkDataSourceParam(dataSourceParam);
        //计算参数正确性检查
        checkCalculateParam(calculateParam);
        //公式解析
        formulaAnalysis(calculateParam);



//        if (calculateLoader == null) {
//            logger.debug("CalculateEngine(Loader)参数为null");
//            throw new RuntimeException("CalculateEngine()参数为null");
//        }
//        if(calculateLoader.getDataSetDefine().dataSources() == null){
//            logger.debug("dataSources()获取的内容为null");
//            throw new RuntimeException("dataSources()获取的内容为null");
//        }
//        logger.debug("数据源总数量为:" + calculateLoader.getDataSetDefine().dataSources().size());
//        if(calculateLoader.getFormulaDefine().getFormulas() == null){
//            logger.debug("getFormulas()获取的内容为null");
//            throw new RuntimeException("getFormulas()获取的内容为null");
//        }
//        logger.debug("公式总数量为:" + calculateLoader.getFormulaDefine().getFormulas().size());
//
//        if(calculateLoader.getFunctionDefine().getFunctions() != null){
//            logger.debug("函数算法总数量为:" + calculateLoader.getFunctionDefine().getFunctions().size());
//        }
//
//        this.calculateLoader = calculateLoader;
        functionWrapper = new FunctionWrapper(calculateLoader);
        datasetWrapper = new DatasetWrapper(calculateLoader);
    }

    private void checkCalculateParam(CalculateParam calculateParam) {
        if(calculateParam == null){
            throw new RuntimeException("计算参数不能为空");
        }
        if(calculateParam.getFormulaID() == null){
            if(calculateParam.getDateTime() == null){
                throw new RuntimeException("计算参数设置不正确");
            }
        }
    }

    private void checkDataSourceParam(DataSourceParam dataSourceParam) {
        if(dataSourceParam == null){
            throw new RuntimeException("数据源参数不能为空");
        }
        if(dataSourceParam.getSource() == null){
            throw new RuntimeException("数据源source参数不能为空");
        }
        if(dataSourceParam.getResult() == null){
            throw new RuntimeException("数据源result参数不能为空");
        }
    }

    public CalculateResult calculate() {
        printCalculateProgress();
        CalculateResult calResult = new CalculateResult();
        try {
            // 参与计算的公式定义信息
            List<Formula> defs = this.calculateLoader.getFormulaDefine().getFormulas();
            if (defs != null && defs.size() > 0) {
                exeCal(defs, this.calculateLoader.getCalPhyIds(), calResult);
            }
        } finally {
            CALCULATE_RUNNING = false;
        }
        logger.debug("保存的结果总数量:" + calResult.size());
        return calResult;
    }

    public CalculateResult calculate(String[] calPhyIds) {
        CalculateResult calResult = new CalculateResult();
        Map<String, Object> calFormula = new HashMap<String, Object>();

        if (calPhyIds != null && calPhyIds.length > 0) {
            List<Formula> defs = new ArrayList<Formula>();
            for (String phyId : calPhyIds) {
                List<Formula> list = this.calculateLoader.getFormulaDefine().getFormulasByPhyId(phyId);
                if (list != null) {
                    for (Formula formula : list) {
                        if (!calFormula.containsKey(formula.getFormulaId())) {
                            calFormula.put(formula.getFormulaId(), null);
                            defs.add(formula);
                        }
                    }
                }
            }
            if (defs.size() > 0) {
                exeCal(defs, calPhyIds, calResult);
                logger.debug("计算公式数量:" + defs.size());
            } else {
                logger.debug("计算公式数量:0");
            }
        }
        if (calPhyIds == null) {
            logger.debug("计算主体数量:0");
        } else {
            logger.debug("计算主体数量:" + calPhyIds.length);
        }
        logger.debug("保存的结果总数量:" + calResult.size());
        return calResult;
    }

    private void exeCal(List<Formula> defs, String[] phyIds, CalculateResult calResult) {
        for (int i = 0; i < defs.size(); i++) {
            Formula formula = defs.get(i);// 公式定义信息
            String formulaDefId = formula.getFormulaId();// 公式定义id
            String datasetId = formula.getDatasetId();// 数据集id
            String phyIdField = formula.getPhyIdField();// 主体列字段
            Map<String, String> filter = formula.getFilter();// 过滤条件

            if (phyIdField == null || phyIdField.equals("")) {
                String msg = "公式[" + formulaDefId + "]未指定主体列";
                throw new CalEngineException(msg);
            }
            // 根据【数据集id】获取数据源
            DataSource calDatasource = this.calculateLoader.getDataSetDefine().getDataSource(datasetId);
            // 判断数据源是否为空
            if (calDatasource == null) {
                continue;
            }
            List<ResultSet.Row> datas = null;
            if (filter != null) {
                datas = calDatasource.getDatas(filter);
            } else {
                datas = calDatasource.getResultSet().getDatas();
            }
            if (datas == null) {
                continue;
            }

            List<Formula.FormulaItem> items = formula.getFormulaItems();

            if (items == null || items.size() == 0) {
                continue;
            }
            //计算主体
            Set<String> phyIdsSet = new HashSet<String>();
            Collections.addAll(phyIdsSet, phyIds);
            Set<String> calPhys = CommonUtil.intersect(phyIdsSet, formula.getRelatedPhyIDs());

            for (ResultSet.Row row : datas) {
                if (calPhys.contains(row.getFieldValue(phyIdField))) {
                    for (int j = 0; j < items.size(); j++) {
                        Formula.FormulaItem item = items.get(j);
                        // 数据集表达式
                        Map runtimeContext = item.getRuntimeContext();
                        Map<String, Object> params = new HashMap<>();
                        params.putAll(runtimeContext);
                        params.put("$formulaItemContext", new FormulaItemContext());
                        params.put("$datasetWrapper", datasetWrapper);
                        params.put("$functionWrapper", functionWrapper);
                        params.put("$calRow", row);
                        try {
                            String result = ScriptEngine.evel(item.getCompiledScript(), params);
                            logger.debug("计算的结果:" + result);
                            //赋值给等号左边计算项
                            if (!"void".equals(result)) {
                                row.setFieldValue(item.getCalculateField(), result);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //需要保存的
                    if (row.isChanged()) {
                        calResult.addCalculateResults(row, calDatasource, formula);
                        row.clearChangedFlag();
                    }
                }
            }
        }
        Js4DB.savingTablesToDB();
    }


    /**
     * 计算结果数据
     * 按数据源区分
     */
    private class ResultDataSource {
        List<ResultSet.Row> calculateResults;
        DataSource calDatasource;

        public ResultDataSource(DataSource calDatasource) {
            this.calDatasource = calDatasource;
        }

        void addResult(ResultSet.Row data) {
            if (calculateResults == null) {
                calculateResults = new ArrayList<ResultSet.Row>();
            }
            calculateResults.add(data);
        }

        void save() {
            if (calDatasource != null & calculateResults != null & calculateResults.size() > 0) {
                calDatasource.save(calculateResults);
            }
        }
    }

    //计算结果处理类
    public class CalculateResult {
        private Map<String, ResultDataSource> results;

        {
            SAVE_RUNNING = true;
        }

        private void addCalculateResults(ResultSet.Row row, DataSource calDatasource, Formula formula) {
            if (calDatasource == null) {
                throw new RuntimeException("数据源参数不能为空");
            }
            String key = calDatasource.getDataSetId() + "~" + formula.getFormulaId();
            if (results == null) {
                results = new HashMap<String, ResultDataSource>();
            }
            ResultDataSource resultDatasource = results.get(key);
            if (resultDatasource == null) {
                resultDatasource = new ResultDataSource(calDatasource);
                results.put(key, resultDatasource);
            }
            resultDatasource.addResult(row);
        }

        public int size(){
            if(results == null){
                return 0;
            }
            return results.size();
        }

        public void save() {
            try {
                if (results != null && results.size() > 0) {
                    for (ResultDataSource result : results.values()) {
                        result.save();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                SAVE_RUNNING = false;
            }
        }
    }

    private void printCalculateProgress() {
        CALCULATE_RUNNING = true;
        System.out.println("Calculate Service Running!");
        new Thread(new Runnable() {
            public void run() {
                while (CALCULATE_RUNNING | SAVE_RUNNING) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.print(">>");
                }
                System.out.println();
                System.out.println("Calculate Service Done!");
            }
        }).start();
    }
}
