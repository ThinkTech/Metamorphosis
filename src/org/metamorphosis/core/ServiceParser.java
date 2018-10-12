package org.metamorphosis.core;

import java.io.File;

import org.apache.commons.digester.Digester;

public class ServiceParser implements ModuleParser {

	@Override
	public Module parse(File metadata) throws Exception {
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate("service", Module.class);
		digester.addBeanPropertySetter("service/id");
		digester.addBeanPropertySetter("service/name");
		digester.addBeanPropertySetter("service/type");
		digester.addBeanPropertySetter("service/url");
		digester.addBeanPropertySetter("service/script");
		digester.addBeanPropertySetter("service/author");
		digester.addBeanPropertySetter("service/authorEmail");
		digester.addBeanPropertySetter("service/authorUrl");
		digester.addBeanPropertySetter("service/description");
		digester.addBeanPropertySetter("service/details");
		digester.addBeanPropertySetter("service/creationDate");
		digester.addBeanPropertySetter("service/copyright");
		digester.addBeanPropertySetter("service/license");
		digester.addBeanPropertySetter("service/version");
		digester.addObjectCreate("service/actions/action", Action.class);
		digester.addSetProperties("service/actions/action");
		digester.addSetProperties("service/actions/action", "class", "className");
		digester.addObjectCreate("service/actions/action/result", Result.class);
		digester.addSetProperties("service/actions/action/result");
		digester.addSetNext("service/actions/action/result", "addResult");
		digester.addCallMethod("service/actions/action/result", "setValue", 0);
		digester.addSetNext("service/actions/action", "addAction");
		return (Module) digester.parse(metadata);
	}

}