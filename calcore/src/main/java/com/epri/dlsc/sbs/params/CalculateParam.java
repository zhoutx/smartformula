package com.epri.dlsc.sbs.params;

import java.util.Date;

/**
 * 计算参数类
 */
final public class CalculateParam {
    //公式ID
    private String formulaID;
    //公式类型
    private String formulaType;
    //计算主体
    private String[] phyIDs;
    //场景
    private String marketID;
    //日期
    private Date dateTime;

    private CalculateParam(){}

    /**
     * 根据指定公式ID计算。所有跟该公式关联的主体数据都将一同计算
     *
     * @param formulaID 公式ID
     */
    public CalculateParam(String formulaID) {
        this.formulaID = formulaID;
    }

    /**
     * 根据指定公式ID和主体ID执行计算
     *
     * @param formulaID 公式ID
     * @param phyIDs    计算主体
     */
    public CalculateParam(String formulaID, String[] phyIDs) {
        this.formulaID = formulaID;
        this.phyIDs = phyIDs;
    }

    /**
     * 根据场景ID和计算日期执行计算。所有跟这些在生效日期内的公式相关的主体数据都将一同计算
     *
     * @param marketID 场景ID。特定需求参数
     * @param dateTime 日期
     */
    public CalculateParam(String marketID, Date dateTime) {
        this.marketID = marketID;
        this.dateTime = dateTime;
    }

    /**
     * 根据场景ID、计算日期、公式类型执行计算。所有跟这些在生效日期内且符合指定公式类型的公式相关的主体数据都将一同计算
     *
     * @param marketID    场景ID。特定需求参数
     * @param dateTime    日期
     * @param formulaType 公式类型
     */
    public CalculateParam(String marketID, Date dateTime, String formulaType) {
        this.marketID = marketID;
        this.dateTime = dateTime;
        this.formulaType = formulaType;
    }

    /**
     * 根据场景ID、计算日期、主体执行计算
     *
     * @param marketID 场景ID
     * @param dateTime 日期
     * @param phyIDs   主体
     */
    public CalculateParam(String marketID, Date dateTime, String[] phyIDs) {
        this.marketID = marketID;
        this.dateTime = dateTime;
        this.phyIDs = phyIDs;
    }

    /**
     * 根据场景ID、计算日期、公式类型、主体执行计算
     *
     * @param marketID    场景ID
     * @param dateTime    计算日期
     * @param formulaType 公式类型
     * @param phyIDs      主体
     */
    public CalculateParam(String marketID, Date dateTime, String formulaType, String[] phyIDs) {
        this.marketID = marketID;
        this.dateTime = dateTime;
        this.formulaType = formulaType;
        this.phyIDs = phyIDs;
    }

    public String getFormulaID() {
        return formulaID;
    }

    public String getFormulaType() {
        return formulaType;
    }

    public String[] getPhyIDs() {
        return phyIDs;
    }

    public String getMarketID() {
        return marketID;
    }

    public Date getDateTime() {
        return dateTime;
    }
}
