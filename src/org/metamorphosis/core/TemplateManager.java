package org.metamorphosis.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.digester.Digester;

public class TemplateManager {

	private List<Template> templates = new ArrayList<Template>();
	private Logger logger = Logger.getLogger(TemplateManager.class.getName());
	private static TemplateManager instance;
	private static final String TEMPLATE_METADATA = "template.xml";

	public TemplateManager() {
		instance = this;
	}

	public void loadTemplates(final File root) {
		File[] files = root.listFiles();
		if (files != null) {
			for (File folder : root.listFiles()) {
				if (folder.isDirectory()) {
					loadTemplate(folder);
				}
			}
			String reload = System.getenv("metamorphosis.reload");
			if("true".equals(reload)){
				monitorRoot(root);
			}
		}
	}

	public Template loadTemplate(File folder) {
		File metadata = new File(folder + "/" + TEMPLATE_METADATA);
		if(metadata.exists()) {
			try {
				Template template = parse(metadata);
				template.setFolder(folder);
				addTemplate(template);
				monitorTemplate(template);
				return template;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			Template template = new Template();
			template.setName(folder.getName());
			template.setType("front-end");
			template.setFolder(folder);
			addTemplate(template);
			monitorTemplate(template);
			return template;
		}
		return null;
	}

	private Template parse(File metadata) throws Exception {
		Digester digester = new Digester();
		digester.setValidating(false);
		digester.addObjectCreate("template", Template.class);
		digester.addBeanPropertySetter("template/id");
		digester.addBeanPropertySetter("template/name");
		digester.addBeanPropertySetter("template/type");
		digester.addBeanPropertySetter("template/selected");
		digester.addBeanPropertySetter("template/author");
		digester.addBeanPropertySetter("template/authorEmail");
		digester.addBeanPropertySetter("template/authorUrl");
		digester.addBeanPropertySetter("template/description");
		digester.addBeanPropertySetter("template/details");
		digester.addBeanPropertySetter("template/creationDate");
		digester.addBeanPropertySetter("template/copyright");
		digester.addBeanPropertySetter("template/license");
		digester.addBeanPropertySetter("template/version");
		return (Template) digester.parse(metadata);
	}

	private void monitorTemplate(final Template template) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)){
			FileMonitor monitor = new FileMonitor(template.getFolder());
			monitor.addListener(new FileListener() {
	
				@Override
				public void onCreated(String file) {
					if (file.equals(TEMPLATE_METADATA)) {
						updateTemplate(template);
					}
				}
	
				@Override
				public void onDeleted(String file) {
	
				}
	
			});
			monitor.watch();
		}
	}

	private void monitorRoot(final File root) {
		String reload = System.getenv("metamorphosis.reload");
		if("true".equals(reload)){
			FileMonitor monitor = new FileMonitor(root);
			monitor.addListener(new FileListener() {
	
				@Override
				public void onCreated(String file) {
					File folder = new File(root + "/" + file);
					if (folder.isDirectory()) {
						logger.log(Level.INFO, "adding template  : " + folder.getName());
						final Template template = new Template();
						template.setFolder(folder);
						addTemplate(template);
						monitorTemplate(template);
					}
	
				}
	
				@Override
				public void onDeleted(String file) {
					Template template = getTemplate(file);
					if (template != null) {
						logger.log(Level.INFO, "removing template  : " + template.getName());
						removeTemplate(template);
					}
				}
	
			});
			monitor.watch();
		}
	}

	private void updateTemplate(Template template) {
		try {
			logger.log(Level.INFO, "updating template  : " + template.getId());
			int index = template.getIndex();
			File folder = template.getFolder();
			template = parse(new File(folder + "/" + TEMPLATE_METADATA));
			template.setFolder(folder);
			template.setIndex(index);
			templates.set(index, template);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addTemplate(Template template) {
		template.setIndex(templates.size());
		templates.add(template);
	}

	public void removeTemplate(Template template) {
		templates.remove(template.getIndex());
	}

	public Template getTemplate(String id) {
		for (Template template : templates) {
			if (template.getId().equals(id))
				return template;
		}
		return null;
	}

	public Template getBackendTemplate(String id) {
		Template template = getTemplate(id);
		if (template != null && template.isBackend())
			return template;
		for (Template current : templates) {
			if (current.isSelected() && current.isBackend())
				return current;
		}
		for (Template current : templates) {
			if (current.isBackend())
				return current;
		}
		return null;
	}

	public Template getFrontendTemplate(String id) {
		Template template = getTemplate(id);
		if (template != null && template.isFrontend())
			return template;
		for (Template current : templates) {
			if (current.isSelected() && current.isFrontend())
				return current;
		}
		for (Template current : templates) {
			if (current.isFrontend())
				return current;
		}
		return null;
	}

	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}

	public List<Template> getTemplates() {
		return templates;
	}
	
	public List<Template> getBackendTemplates() {
		List<Template> list = new ArrayList<Template>();
		for (Template current : templates) {
			if (current.isBackend()) list.add(current);
		}
		return list;
	}
	
	public List<Template> getFrontendTemplates() {
		List<Template> list = new ArrayList<Template>();
		for (Template current : templates) {
			if (current.isFrontend()) list.add(current);
		}
		return list;
	}

	public static TemplateManager getInstance() {
		return instance;
	}

}