package com.example.demo;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller

public class SampleController {
	@RequestMapping("/sample")

	@GetMapping("/test")
	public String test(Model model) {

		model.getAttribute("title");
		return "users/test";
	}

}

