package com.bumblebee.acquisition.core.model;

public class PersisitModel {
	private String parentKey;
	private String currentKey;
	private Object currentvalue;

	public String getParentKey() {
		return parentKey;
	}

	public String getCurrentKey() {
		return currentKey;
	}

	public Object getCurrentvalue() {
		return currentvalue;
	}

	public void setParentKey(String parentKey) {
		this.parentKey = parentKey;
	}

	public void setCurrentKey(String currentKey) {
		this.currentKey = currentKey;
	}

	public void setCurrentvalue(Object currentvalue) {
		this.currentvalue = currentvalue;
	}

}
