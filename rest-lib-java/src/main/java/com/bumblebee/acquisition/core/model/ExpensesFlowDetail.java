package com.bumblebee.acquisition.core.model;

import java.io.Serializable;
import java.util.Date;

public class ExpensesFlowDetail implements Serializable {
    private String msgid;

    private String interfaceId;

    private String pluginType;

    private String pluginId;

    private String validFlg;

    private String expenseFlg;

    private String iscached;

    private String userId;

    private Date createDt;

    private String systemId;

    private String customerId;

    private String customerName;

    private String quryReason;

    private String tranUserId;

    private String orgCode;

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getQuryReason() {
        return quryReason;
    }

    public void setQuryReason(String quryReason) {
        this.quryReason = quryReason;
    }

    public ExpensesFlowDetail() {
    }

    public String getTranUserId() {
        return tranUserId;
    }

    public void setTranUserId(String tranUserId) {
        this.tranUserId = tranUserId;
    }

    public ExpensesFlowDetail(String msgid, String interfaceId, String pluginType, String pluginId, String validFlg, String expenseFlg, String iscached, String userId, Date createDt, String systemId, String customerId, String customerName, String quryReason, String tranUserId, String orgCode) {
        this.msgid = msgid;
        this.interfaceId = interfaceId;
        this.pluginType = pluginType;
        this.pluginId = pluginId;
        this.validFlg = validFlg;
        this.expenseFlg = expenseFlg;
        this.iscached = iscached;
        this.userId = userId;
        this.createDt = createDt;
        this.systemId = systemId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.quryReason = quryReason;
        this.tranUserId = tranUserId;
        this.orgCode=orgCode;
    }

    public String getCustomerId() {

        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid == null ? null : msgid.trim();
    }

    public String getInterfaceId() {
        return interfaceId;
    }

    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId == null ? null : interfaceId.trim();
    }

    public String getPluginType() {
        return pluginType;
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType == null ? null : pluginType.trim();
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId == null ? null : pluginId.trim();
    }

    public String getValidFlg() {
        return validFlg;
    }

    public void setValidFlg(String validFlg) {
        this.validFlg = validFlg == null ? null : validFlg.trim();
    }

    public String getExpenseFlg() {
        return expenseFlg;
    }

    public void setExpenseFlg(String expenseFlg) {
        this.expenseFlg = expenseFlg == null ? null : expenseFlg.trim();
    }

    public String getIscached() {
        return iscached;
    }

    public void setIscached(String iscached) {
        this.iscached = iscached == null ? null : iscached.trim();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public Date getCreateDt() {
        return createDt;
    }

    public void setCreateDt(Date createDt) {
        this.createDt = createDt;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }
}
