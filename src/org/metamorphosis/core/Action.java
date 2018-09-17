package org.metamorphosis.core;

import java.util.ArrayList;
import java.util.List;

public class Action {

	private String title;
	private String url;
	private String className="";
	private String method ="execute";
	private String page;
	private String script = "module.groovy";
	private List<Result> results = new ArrayList<Result>();
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
		if(page!=null) {
			Result result = new Result();
			result.setValue(this.page);
			results.add(result);
		}
	}
	public String getScript() {
		return script;
	}
	public void setScript(String script) {
		this.script = script;
	}
	public void addResult(Result result) {
		results.add(result);
	}
	public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
}