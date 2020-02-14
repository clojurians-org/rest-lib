package com.bumblebee.acquisition.core.model;

/**
 * 接口数据基本信息类
 * 
 * @author jeremychen
 *
 */
public class DataModel {
	/**
	 * 接口输入参数组
	 */
	private String keys;
	
	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 字段名
	 */
	private String columnName;

	/**
	 * 值
	 */
	private String value;
	private Integer rowId;

	public Integer getRowId() {
		return rowId;
	}

	public void setRowId(Integer rowId) {
		this.rowId = rowId;
	}

	public String getTableName() {
		return tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public String getValue() {
		return value;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

    @Override
    public String toString() {
        return "DataModel{" +
                "keys='" + keys + '\'' +
                ", tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", value='" + value + '\'' +
                ", rowId=" + rowId +
                '}';
    }
}
