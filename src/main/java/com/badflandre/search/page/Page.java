package com.badflandre.search.page;

public class Page {
	private String url;
	private String title;
	private String summary;
	private String context;
	private int score;

	public Page() {
		url = null;
		title = null;
		summary = null;
		context = null;
		score = 10;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getContext() {
		return this.context;
	}

	public void SetContext(String context) {
		this.context = context;
	}

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}