package net.controller;

import net.domain.Employer;
import net.service.EmployerDaoManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/employer")
public class EmployerController {
	
	@Autowired
	EmployerDaoManager employerDaoManager;
	
	@Transactional
	@RequestMapping(method=RequestMethod.GET, value="/get/{employerId}")
	public @ResponseBody Employer getEmployer (@PathVariable int employerId) {	
		
		Employer employer = employerDaoManager.getEmployerById(employerId);
		
		return employer;
	}	
}
