package org.thingsboard.server.dao.util.sql;

import java.io.Serializable;

/**
 *
 */
public class SimplePageInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private int page;
	private int size;

	private String sortStr;

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getSortStr() {
		return sortStr;
	}

	public void setSortStr(String sortStr) {
		this.sortStr = sortStr;
	}

	public SimplePageInfo(int page, int size, String sortStr) {
		this.page = page;
		this.size = size;
		this.sortStr = sortStr;
	}

	public SimplePageInfo(){

	}
}
