package com.epri.dlsc.sbs.params;

import java.util.Map;

/**
 * 数据源参数类
 */
final public class DataSourceParam {
    //源参数
    private String source;
    //目标参数
    private String result;
    //脚本内容中参数,如${paramName}
    private Map scriptParams;

    private DataSourceParam(){}

    /**
     * 数据集参数
     * @param source 源参数
     * @param result 存储目标参数
     * @param scriptParams 数据源脚本参数,如${paramName}表示为Map(paramsName, value)
     */
    public DataSourceParam(String source, String result, Map scriptParams){
        this.source = source;
        this.result = result;
        this.scriptParams = scriptParams;
    }

    public String getSource() {
        return source;
    }

    public String getResult() {
        return result;
    }

    public Map getScriptParams() {
        return scriptParams;
    }
}
