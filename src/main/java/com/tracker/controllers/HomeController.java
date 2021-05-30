package com.tracker.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.tracker.services.CoronaVirusDataService;

@Controller
public class HomeController {
	
	@Autowired
	CoronaVirusDataService 	coronaVirusDataService;
	@GetMapping(path = "/")
	public String home(Model model) {
		int totalNewCases = coronaVirusDataService.getAllStats().stream().mapToInt(stat->stat.getDiffFromPreDay()).sum();
		model.addAttribute("locationStats",coronaVirusDataService.getAllStats());
		model.addAttribute("totalReportedCases",coronaVirusDataService.getTotalCases());
		model.addAttribute("totalNewCases",totalNewCases);

		return "home";
	}
}
