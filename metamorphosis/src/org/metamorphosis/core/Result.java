package org.metamorphosis.core;

import java.util.ArrayList;
import java.util.List;

public class Result {

	private String name="success";
	private String type="tiles";
	private String value="";
	private List<Parameter> parameters = new ArrayList<Parameter>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}
	public List<Parameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}
}
