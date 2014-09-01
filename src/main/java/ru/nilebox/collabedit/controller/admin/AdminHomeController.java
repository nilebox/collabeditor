/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nilebox.collabedit.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author nile
 */
@Controller
@RequestMapping("admin")
public class AdminHomeController {
	@RequestMapping("home.html")
	public ModelAndView index(){
		return new ModelAndView("admin/home");
	}
}
