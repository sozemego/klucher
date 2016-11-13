package com.soze.hashtag.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.soze.common.exceptions.NotLoggedInException;
import com.soze.hashtag.service.HashtagAnalysisService;
import com.soze.hashtag.service.analysis.AnalysisFactory;
import com.soze.hashtag.service.analysis.AnalysisFactory.AnalysisType;
import com.soze.hashtag.service.analysis.HashtagAnalysis;

@Controller
public class HashtagController {
	
	private final HashtagAnalysisService analysisService;
	private final AnalysisFactory analysisFactory;
	
	@Autowired
  public HashtagController(HashtagAnalysisService analysisService, AnalysisFactory analysisFactory) {
		this.analysisService = analysisService;
		this.analysisFactory = analysisFactory;
	}
	
	@PostConstruct
	public void setUp() {
		analysisService.setAnalysisStrategy(analysisFactory.getAnalysis(AnalysisType.SIMPLE));
	}

	@RequestMapping(value = "/hashtag/{hashtag}", method = RequestMethod.GET)
  public String getHashtag(@PathVariable String hashtag, Authentication authentication, Model model) {
  	
    model.addAttribute("hashtag", hashtag.toLowerCase());
    
    boolean loggedIn = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    model.addAttribute("loggedIn", loggedIn);
    if(loggedIn) {
      model.addAttribute("username", authentication.getName());
    }
    
    return "hashtag";
  }
  
  @RequestMapping(value = "/hashtag", method = RequestMethod.GET)
  public String handleRedirect() {
    return "redirect:/dashboard";
  }
  
  @RequestMapping(value = "/hashtag/analysis/type", method = RequestMethod.POST)
  public ResponseEntity<Object> setAnalysisType(Authentication authentication, @RequestParam("name") String name) {
  	if(authentication == null) {
  		throw new NotLoggedInException();
  	}
  	if(!authentication.getAuthorities().contains("ADMIN")) {
  		return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
  	}
  	AnalysisType type = getType(name);
  	HashtagAnalysis analysis = analysisFactory.getAnalysis(type);
		analysisService.setAnalysisStrategy(analysis);
		return new ResponseEntity<Object>(HttpStatus.OK);
  }
  
  private AnalysisType getType(String name) {
  	if("simple".equalsIgnoreCase(name)) {
  		return AnalysisType.SIMPLE;
  	}
  	if("statistical".equalsIgnoreCase(name)) {
  		return AnalysisType.STATISTICAL;
  	}
  	return null;
  }

}
