package org.metamorphosis.core;

import java.util.ArrayList;
import java.util.List;

public class Action {

	private String title;
	private String url;
	private String method;
	private String page;
	private String script;
	private String httpMethod;
	private final List<Result> results;
	
	public Action() {
		results = new ArrayList<Result>();;
	}

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
		this.url = url.startsWith("/")?url.substring(1):url;
	}

	public String getMethod() {
		return method != null ? method : "execute";
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
		if(page != null) results.add(new Result(page));	
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		if(!script.endsWith(".groovy")) script+=".groovy";
		this.script = script;
	}

	public void addResult(Result result) {
		results.add(result);
	}

	public List<Result> getResults() {
		return results;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	
}