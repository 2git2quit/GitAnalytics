package com.analytics.git.controllers;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;

import com.analytics.git.model.Analytics;
import com.analytics.git.model.Commits;
import com.analytics.git.model.Committer;
import com.analytics.git.model.Projects;
import com.analytics.git.model.SortedAnalytics;

@Controller
public class RepositoryController {

    @Value("${git.authentication.enabled}")
    String gitAuth;

    @Value("${git.basicauth.enabled}")
    String gitBasicAuth;

    @Value("${git.oauth2.enabled}")
    String gitOAuth2;

  	@Value("${git.user}")
	String gitUser;

	@Value("${git.password}")
	String gitPassword;

	@Value("${git.token}")
	String gitToken;

	@GetMapping({ "", "/", "/index" })
	public String getIndexPage() {
		return "index";
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public String searchRepositories(@RequestParam("q") String q, Model model) {
		String url = "https://api.github.com/search/repositories?q=is:public " + q + "&per_page=100";
		RestTemplate tmpl = new RestTemplate();
        ResponseEntity<Projects> entities = tmpl.exchange(url, HttpMethod.GET,
                new HttpEntity<Projects>(createHeaders()), Projects.class);
        Projects projects = entities.getBody();

		//Projects projects = tmpl.getForObject(url, Projects.class);

		model.addAttribute("projects", projects.getItems());
		return "project";

	}

	private HttpHeaders createHeaders() {
		return new HttpHeaders() {
			{
				String auth = gitUser + ":" + gitPassword;
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
                String token = "token " + gitToken;
                if (new Boolean(gitAuth)) {
				    if (new Boolean(gitBasicAuth)) {
                        set("Authorization", authHeader);
                    } else  if (new Boolean(gitOAuth2)) {
                        set("Authorization", token);
                    }
                }
				set("Accept", "application/vnd.github.v3+json");
			}
		};
	}

	@GetMapping("/commits")
	public String listCommits(@RequestParam("owner") String owner, @RequestParam("repo") String repo, Model model) {
		String url = "https://api.github.com/repos/" + owner + "/" + repo + "/commits?per_page=100";

		RestTemplate tmpl = new RestTemplate();
		ResponseEntity<Commits[]> entities = tmpl.exchange(url, HttpMethod.GET,
				new HttpEntity<Commits>(createHeaders()), Commits[].class);
		List<Commits> commits = Arrays.asList(entities.getBody());
		List<Analytics> analytics = generateAnalytics(commits);
        model.addAttribute("repo", repo);
		model.addAttribute("commits", commits);
		Collections.sort(analytics, new SortedAnalytics());
		model.addAttribute("analytics", analytics);
		return "committer";
	}


	private List<Analytics> generateAnalytics(List<Commits> commits) {

		Map<String, Integer> map = new HashMap<>();
		Map<String, String> map2 = new HashMap<>();

		for (Commits c : commits) {
			c.getCommit().getCommitter().setDate(formatDate(c.getCommit().getCommitter().getDate()));
			if (map.get(c.getCommit().getCommitter().getName()) != null) {

				map.put(c.getCommit().getCommitter().getName(), map.get(c.getCommit().getCommitter().getName()) + 1);
				if (map2.get(c.getCommit().getCommitter().getName())
						.compareTo(c.getCommit().getCommitter().getDate()) < 0) {
					map2.put(c.getCommit().getCommitter().getName(), c.getCommit().getCommitter().getDate());
				}
			} else {
				map.put(c.getCommit().getCommitter().getName(), 1);
				map2.put(c.getCommit().getCommitter().getName(), c.getCommit().getCommitter().getDate());
			}
		}

		List<Analytics> analytics = new ArrayList<>();

		for (Entry<String, Integer> entry : map.entrySet()) {
			String key = entry.getKey();
			Integer value = (Integer) entry.getValue();
			String lastUpdate = map2.get(key);
			Analytics analytic = new Analytics();
			Committer committer = new Committer();
			committer.setName(key);
			analytic.setCommitter(committer);
			analytic.setTotal_commits(value);
			analytic.setLatest_commit(formatDate(lastUpdate));
			analytics.add(analytic);
		}

		return analytics;
	}
	
	private String formatDate(String d) {
		SimpleDateFormat DateFor = new SimpleDateFormat("yyyy-MM-dd");
		Date dd;
		try {
			dd = DateFor.parse(d);
			String stringDate= DateFor.format(dd);
			return stringDate;
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return "";
	}

}
