package org.metamorphosis.core;

import java.io.File;

public abstract class Extension {

	protected String id;
	protected String name;
	protected String type="back-end";
	protected String author;
	protected String authorEmail;
	protected String authorUrl;
	protected String creationDate;
	protected String copyright;
	protected String license;
	protected String version;
	protected String description;
	protected String details;
	protected File folder;
	private boolean visible = true;
	protected int index;
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name!=null ? name : folder.getName();
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
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthorEmail() {
		return authorEmail;
	}
	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}
	public String getAuthorUrl() {
		return authorUrl;
	}
	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDetails() {
		return details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	public File getFolder() {
		return folder;
	}
	public void setFolder(File folder) {
		this.folder = folder;
		id = folder.getName();
	}
	
	public boolean isBackend() {
		return type.equals("back-end") || type.equals("both");
	}
	
	public boolean isFrontend() {
		return type.equals("front-end") || type.equals("both");
	}
	
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
}
