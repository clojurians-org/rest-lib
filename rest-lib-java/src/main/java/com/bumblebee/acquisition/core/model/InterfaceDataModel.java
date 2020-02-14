package com.bumblebee.acquisition.core.model;

import java.util.List;
import java.util.Map;

/**
 * 接口数据模型类
 * 
 * @author jeremychen
 *
 */
public class InterfaceDataModel {
	/**
	 * 接口ID
	 */
	private String interfaceId;

	/**
	 * 表清单
	 */
	private Map<String, String> tableNames;

	/**
	 * 列名称信息
	 */
	private Map<String, String> columns;

	/**
	 * 数据
	 */
	private List<DataModel> values;

    public InterfaceDataModel() {
    }

    public InterfaceDataModel(Map<String, String> tableNames, Map<String, String> columns, List<DataModel> values) {
		this.tableNames = tableNames;
		this.columns = columns;
		this.values = values;
	}

	public String getInterfaceId() {
		return interfaceId;
	}

	public Map<String, String> getColumns() {
		return columns;
	}

	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}

	public List<DataModel> getValues() {
		return values;
	}

	public Map<String, String> getTableNames() {
		return tableNames;
	}

    @Override
    public String toString() {
        return "InterfaceDataModel{" +
                "interfaceId='" + interfaceId + '\'' +
                ", tableNames=" + tableNames +
                ", columns=" + columns +
                ", values=" + values +
                '}';
    }
}
