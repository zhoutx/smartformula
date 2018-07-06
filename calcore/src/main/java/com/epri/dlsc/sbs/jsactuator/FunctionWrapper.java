package com.epri.dlsc.sbs.jsactuator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.epri.dlsc.sbs.exception.CalEngineException;
import com.epri.dlsc.sbs.exception.DataNotUnique;
import com.epri.dlsc.sbs.dataset.DataSetExpression;
import com.epri.dlsc.sbs.function.FunctionExpression;
import com.epri.dlsc.sbs.inter.datasource.BaseDataSource;
import com.epri.dlsc.sbs.inter.loader.Loader;
import com.epri.dlsc.sbs.dataset.DataSet;
import com.epri.dlsc.sbs.util.FastJSONUtil;
import com.epri.dlsc.sbs.function.Function;
import com.epri.dlsc.sbs.dataset.ResultSet;

/**
 * 函数包装器
 *
 * @author Zhong.Weijian
 */
public class FunctionWrapper {

    // 数据中转站
    private Loader calculateDriver;
    // 函数类实例化对象
    private Map<String, Object> clzInstanceMap = new HashMap<String, Object>();
    // 函数结果缓存{缓存id:缓存结果值}
    private Map<String, String> functionResultCache = new HashMap<String, String>();

    public FunctionWrapper(Loader calculateDriver) {
        this.calculateDriver = calculateDriver;
    }

    public double eval(FunctionExpression functionExpression, ResultSet.Row calRow, String... variables) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            NoSuchMethodException, IllegalArgumentException,
            InvocationTargetException {

        /** 获取函数信息 */

        // 获取函数名称
        String functionCode = functionExpression.getFunctionId();
        // 获取函数参数
        FunctionExpression.ArgumentValue[] argExpsList = functionExpression.getFunctionArgs();

        Function.FunctionArg[] args = calculateDriver.getFunctionDefine().getFunction(functionCode).getArgs();

        // 检查函数个数是否匹配
        if (argExpsList != null && args != null
                && argExpsList.length != 0 && args.length != 0) {
            if (argExpsList.length != args.length) {
                String msg = "函数[" + functionCode + "]参数个数不匹配";
                throw new CalEngineException(msg);
            }
        }

        /** 检查函数是否存在、如果不存在则抛出异常 */

        if (calculateDriver.getFunctionDefine() == null || !calculateDriver.getFunctionDefine().hasFunction(functionCode)) {
            String msg = "未查询到函数[" + functionCode + "]的相关信息，请检查函数配置项!";
            throw new CalEngineException(msg);
        }

        /** 检查该函数是否缓存了结果值，如果是则直接返回 */

        // 拼接函数缓存id（形成一个方法generateCacheKey）
        String functionCacheKey = generateCacheKey(functionExpression, calRow, variables);

        // 判断函数缓存池中是否包含该缓存id的值
        if (functionResultCache != null
                && functionResultCache.containsKey(functionCacheKey)) {

            // 根据函数缓存id搜索缓存池
            String resultVal = functionResultCache.get(functionCacheKey);
            // 缓存返回值为空，则返回0
            if (resultVal == null) {
                return 0;
            }
            // 缓存返回值为数字，则返回该数字
            else {
                return Double.valueOf(resultVal);
            }
        }

        /** 如果函数没有缓存结果值，则执行函数 */

        // 获取函数java全路径名，实例化java对象，获取java对象的method对象
        String functionFullName = calculateDriver.getFunctionDefine().getFunction(functionCode).getFunctionFullName();
        String className = functionFullName.substring(0, functionFullName.lastIndexOf("."));
        String methodName = functionFullName.substring(functionFullName.lastIndexOf(".") + 1);

        // 获取类的方法
        Method method = getMethod(className, methodName, args.length);

        Class[] paramTypes = method.getParameterTypes();

        Object[] paramValue = new Object[args.length];

        // 循环参数类型列表
        for (int i = 0; i < args.length; i++) {
            Function.FunctionArg arg = args[i];

            FunctionExpression.ArgumentValue argExps = argExpsList[i];

            // 常量类型
            if (arg.getType() == Function.FunctionArg.CONSTANT) {
                // 数据集
                if (argExps.dataType == Function.FunctionArg.DATASET) {
                    // 获取函数中的数据集
                    DataSetExpression datasetExps = (DataSetExpression) argExps.value;
                    String datasetId = datasetExps.getDatasetId();
                    BaseDataSource calDatasource = (BaseDataSource) calculateDriver.getDataSetDefine().getDataSource(datasetId);
                    String uniqueConstraintId = calDatasource.getUniqueConstraintId(calRow, datasetExps);
                    String resultVal = null;
                    try {
                        resultVal = calDatasource.getUniqueDataValue(uniqueConstraintId, datasetExps.getFiledId());
                    } catch (DataNotUnique dataNotUnique) {
                        dataNotUnique.printStackTrace();
                    }
                    // 判断方法类型是否为BigDecimal
                    if (paramTypes[i].equals(BigDecimal.class)) {
                        paramValue[i] = resultVal == null ? null : new BigDecimal(resultVal);
                    }
                    // 判断方法类型是否为String
                    else if (paramTypes[i].equals(String.class)) {
                        paramValue[i] = resultVal;
                    }
                    // 判断算法实际参数类型是否为int
                    else if (paramTypes[i] == int.class || paramTypes[i] == Integer.class) {
                        paramValue[i] = (resultVal == null) ? -1 : Integer.parseInt(resultVal);
                    }
                }
                // 数字
                else if (argExps.dataType == Function.FunctionArg.NUMBER) {
                    // 判断方法类型是否为BigDecimal
                    if (paramTypes[i].equals(BigDecimal.class)) {
                        paramValue[i] = new BigDecimal((Double) argExps.value);
                    }
                    // 判断方法类型是否为String
                    else if (paramTypes[i].equals(String.class)) {
                        paramValue[i] = String.valueOf((Double) argExps.value);
                    }
                    // 判断算法实际参数类型是否为int
                    else if (paramTypes[i] == int.class || paramTypes[i] == Integer.class) {
                        paramValue[i] = ((Double) argExps.value).intValue();
                    }
                }
                // 字符串
                else if (argExps.dataType == Function.FunctionArg.STRING) {
                    // 判断方法类型是否为BigDecimal
                    if (paramTypes[i].equals(BigDecimal.class)) {
                        paramValue[i] = new BigDecimal((String) argExps.value);
                    }
                    // 判断方法类型是否为String
                    else if (paramTypes[i].equals(String.class)) {
                        paramValue[i] = (String) argExps.value;
                    }
                    // 判断算法实际参数类型是否为int
                    else if (paramTypes[i] == int.class || paramTypes[i] == Integer.class) {
                        paramValue[i] = (argExps.value == null || "".equals(((String) argExps.value).trim()))
                                ? -1 : Integer.parseInt((String) argExps.value);
                    }
                }
                // 变量
                else if (argExps.dataType == Function.FunctionArg.VARIABLE) {
                    // 判断方法类型是否为BigDecimal
                    if (paramTypes[i].equals(BigDecimal.class)) {
                        paramValue[i] = variables[i] == null ? null : new BigDecimal(variables[i]);
                    }
                    // 判断方法类型是否为String
                    else if (paramTypes[i].equals(String.class)) {
                        paramValue[i] = String.valueOf(variables[i]);
                    }
                }
            }
            // 数据集类型
            else if (arg.getType() == Function.FunctionArg.DATASET) {
                // 数据集
                if (argExps.dataType == Function.FunctionArg.DATASET) {
                    DataSetExpression datasetExps = (DataSetExpression) argExps.value;
                    String datasetId = datasetExps.getDataSetId();
                    String filedId = datasetExps.getValueFieldId();
                    BaseDataSource calDatasource = (BaseDataSource) calculateDriver.getDataSetDefine().getDataSource(datasetId);
                    Map<String, String> filers = calDatasource.getUniqueConstraintFilters(calRow, datasetExps);
                    List<ResultSet.Row> datas = calDatasource.getDatas(filers);
                    if (datas != null && datas.size() > 0) {
                        Map<String, Map<String, String>> rows = new HashMap<String, Map<String, String>>();
                        for (ResultSet.Row row : datas) {
                            rows.put(row.getId(), row.RowTransformMap());
                        }
                        DataSet ds = new DataSet();
                        ds.setOperateKey(filedId);
                        ds.setDataSets(rows);
                        ds.setSize(datas == null ? 0 : datas.size());
                        paramValue[i] = ds;
                    }
                }
            }
            // 其他，将参数类型作为参数值
            else {
                // 判断方法类型是否为BigDecimal
                if (paramTypes[i].equals(BigDecimal.class)) {
                    paramValue[i] = new BigDecimal(arg.getType());
                }
                // 判断方法类型是否为String
                else if (paramTypes[i].equals(String.class)) {
                    paramValue[i] = arg.getType();
                }
            }

        }

        // 加载java类
        Class clz = Class.forName(className);

        // 判断该类是否已经实例化，如果没有实例化则实例化该类，并将该类的实例化对象放入对象实例化池中
        if (!clzInstanceMap.containsKey(className)) {
            Object object = clz.newInstance();
            clzInstanceMap.put(className, object);
        }

        // 获取类的实例化对象
        Object object = clzInstanceMap.get(className);

        Object result = method.invoke(object, paramValue);

        if (result != null) {
            functionResultCache.put(functionCacheKey, result.toString());
            return Double.valueOf(result.toString());
        } else {
            functionResultCache.put(functionCacheKey, "0");
            return 0;
        }
    }

    // 生成函数缓存id
    public String generateCacheKey(FunctionExpression functionExps,
                                   ResultSet.Row calRow, String... variables) {
        String functionCode = functionExps.getFunctionId();
        FunctionExpression.ArgumentValue[] argExpsList = functionExps.getFunctionArgs();
        String functionCacheKey = functionCode;
        if (argExpsList != null && argExpsList.length > 0) {
            functionCacheKey += argExpsList.length;
            for (int j = 0; j < argExpsList.length; j++) {
                FunctionExpression.ArgumentValue argExps = argExpsList[j];
                // 数据集
                if (argExps.dataType == Function.FunctionArg.DATASET) {
                    DataSetExpression datasetExps = (DataSetExpression) argExps.value;
                    String datasetId = datasetExps.getDatasetId();
                    String filedId = datasetExps.getFiledId();
                    functionCacheKey += datasetId;
                    functionCacheKey += filedId;
                    Set<String> wheres = datasetExps.getWheres();
                    Map<String, String> filters = datasetExps.getFilters();

                    if (wheres != null && wheres.size() > 0) {
                        Map<String, String> wheresMap = new HashMap<String, String>();
                        for (String k : wheres) {
                            wheresMap.put(k, calRow.getFieldValue(k));
                        }
                        if (wheresMap != null && wheresMap.size() > 0) {
                            functionCacheKey += FastJSONUtil
                                    .toJSONString(wheresMap);
                        }
                    }
                    if (filters != null && filters.size() > 0) {
                        functionCacheKey += FastJSONUtil.toJSONString(filters);
                    }
                }
                // 数字
                else if (argExps.dataType == Function.FunctionArg.NUMBER) {
                    functionCacheKey += argExps.value;
                }
                // 字符串
                else if (argExps.dataType == Function.FunctionArg.STRING) {
                    functionCacheKey += argExps.value;
                }
                // 变量
                else if (argExps.dataType == Function.FunctionArg.VARIABLE) {
                    functionCacheKey += variables[j];
                }
            }
        }
        return functionCacheKey;
    }

    // 获取对象的方法
    public Method getMethod(String className, String methodName,
                            int argsCount) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException,
            NoSuchMethodException {

        // 加载java类
        Class clz = Class.forName(className);

        Method[] methods = clz.getMethods();

        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            // 方法名称
            String _methodName = method.getName();
            // 方法参数列表类型
            Class[] paramTypes = method.getParameterTypes();
            // 匹配方法名称和方法参数列表个数
            if (_methodName.equals(methodName)
                    && argsCount == paramTypes.length) {
                return method;
            }
        }

        String msg = "未找到[" + className + "." + methodName + "()]方法!";
        throw new NoSuchMethodException(msg);
    }
}
