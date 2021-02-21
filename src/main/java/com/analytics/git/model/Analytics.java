package com.analytics.git.model;

import java.util.Date;

public class Analytics {
	public  Committer committer;
	public Integer total_commits;
	public String latest_commit;
	
	public Committer getCommitter() {
		return committer;
	}
	public void setCommitter(Committer committer) {
		this.committer = committer;
	}
	public Integer getTotal_commits() {
		return total_commits;
	}
	public void setTotal_commits(Integer total_commits) {
		this.total_commits = total_commits;
	}
	public String getLatest_commit() {
		return latest_commit;
	}
	public void setLatest_commit(String latest_commit) {
		this.latest_commit = latest_commit;
	}
	
}
