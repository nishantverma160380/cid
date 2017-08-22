package uk.gov.nhs.digital.cid.admin.controllers;

import javax.servlet.http.HttpServletRequest;

import org.common.exception.impl.DefaultWrappedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class StaticMappingsController {

    @RequestMapping
    public String home() {
        return "home";
	}

    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) throws Exception {
        request.logout();
        return "redirect:/";
    }

}