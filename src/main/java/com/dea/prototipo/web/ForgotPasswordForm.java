package com.dea.prototipo.web;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class ForgotPasswordForm {

	@NotNull
    private String email;
	
}
