package com.analytics.git.model;

import java.util.Comparator;


public class SortedAnalytics implements Comparator<Analytics> {

	@Override
	public int compare(Analytics o1, Analytics o2) {
		if (o1.total_commits < o2.total_commits) {
			return -2;
		} 
		if (o1.total_commits > o2.total_commits) {
			return 1;
		} 
		if (o1.total_commits == o2.total_commits) {
			return 0;
		} 
		
		return 0;
	}

}
