package com.bumblebee.acquisition.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Result implements Serializable {


    private List<ExpensesFlowDetail> expensesFlowDetails;
    private boolean isSuccess;
    private String message;
    private Object data;
    private String persisitData;
    private String fetchReturnCode;
    private String fetchReturnMessage;
    private Object htmlData;


    public Result(List<ExpensesFlowDetail> expensesFlowDetails, boolean isSuccess, String message, Object data) {
        this.expensesFlowDetails = expensesFlowDetails;
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public String getFetchReturnCode() {
        return fetchReturnCode;
    }

    public void setFetchReturnCode(String fetchReturnCode) {
        this.fetchReturnCode = fetchReturnCode;
    }

    public String getFetchReturnMessage() {
        return fetchReturnMessage;
    }

    public void setFetchReturnMessage(String fetchReturnMessage) {
        this.fetchReturnMessage = fetchReturnMessage;
    }

    public Result(List<ExpensesFlowDetail> expensesFlowDetails, boolean isSuccess, String message, Object data, String persisitData, String fetchReturnCode, String fetchReturnMessage) {

        this.expensesFlowDetails = expensesFlowDetails;
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
        this.persisitData = persisitData;
        this.fetchReturnCode = fetchReturnCode;
        this.fetchReturnMessage = fetchReturnMessage;
    }


    public String getPersisitData() {
        return persisitData;
    }

    public void setPersisitData(String persisitData) {
        this.persisitData = persisitData;
    }

    public void setValidFlgForAll(String validFlg) {
        List<ExpensesFlowDetail> expensesFlowDetails1 = new ArrayList<>();
        for (ExpensesFlowDetail expensesFlowDetail : expensesFlowDetails) {
            expensesFlowDetail.setValidFlg(validFlg);
            expensesFlowDetails1.add(expensesFlowDetail);
        }
        this.expensesFlowDetails = expensesFlowDetails1;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<ExpensesFlowDetail> getExpensesFlowDetails() {
        return expensesFlowDetails;
    }

    public void setExpensesFlowDetails(List<ExpensesFlowDetail> expensesFlowDetails) {
        this.expensesFlowDetails = expensesFlowDetails;
    }

    public void addExpensesFlowDetails(ExpensesFlowDetail expensesFlowDetails) {
        this.expensesFlowDetails.add(expensesFlowDetails);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getHtmlData() {
        return htmlData;
    }

    public void setHtmlData(Object htmlData) {
        this.htmlData = htmlData;
    }
 }
