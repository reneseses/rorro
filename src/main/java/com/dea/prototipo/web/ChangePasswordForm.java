package com.dea.prototipo.web;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class ChangePasswordForm {

	@NotNull
	private String oldPassword;

	@NotNull
    private String newPassword;

	@NotNull
    private String newPasswordAgain;
	
}
