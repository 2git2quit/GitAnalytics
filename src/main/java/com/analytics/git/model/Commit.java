package com.analytics.git.model;

public class Commit {

	Author author;
	Committer committer;
	
	public Author getAuthor() {
		return author;
	}
	public void setAuthor(Author author) {
		this.author = author;
	}
	public Committer getCommitter() {
		return committer;
	}
	public void setCommitter(Committer committer) {
		this.committer = committer;
	}
}
