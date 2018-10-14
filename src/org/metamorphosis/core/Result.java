package org.metamorphosis.core;

public class Result {

	private String name;
	private String type;
	private String value;
	
	public Result() {
	}
	
	public Result(String value) {
		this.value = value;
	}
	
	public Result(String name,String type) {
		this.name = name;
		this.type = type;
	}

	public Result(String name,String type,String value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}
	
	public String getName() {
		return name != null ? name : "success";
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type != null ? type : "tiles";
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value != null ? value : "";
	}

	public void setValue(String value) {
		this.value = value;
	}
}