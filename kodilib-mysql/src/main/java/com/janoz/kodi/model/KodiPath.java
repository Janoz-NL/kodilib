package com.janoz.kodi.model;

public class KodiPath {

	private Integer id;
	private String path;
	private Integer parentId;
	private KodiPath parent;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public KodiPath getParent() {
		return parent;
	}
	public void setParent(KodiPath parent) {
		this.parent = parent;
	}
	public boolean hasParent() {
		return parentId != null || parent != null;
	}
}
