package com.analytics.git.model;

import java.util.List;

public class Commits {
  Commit commit;
  String url;

public String getUrl() {
	return url;
}

public void setUrl(String url) {
	this.url = url;
}

public Commit getCommit() {
	return commit;
}

public void setCommit(Commit commit) {
	this.commit = commit;
}
}
