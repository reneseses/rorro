package com.dea.prototipo.web;

import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;

import com.dea.prototipo.domain.Usuario;

@RooJavaBean
public class SignupForm {

	@NotNull
    private String nombre;

    @NotNull
    private String email;

    @NotNull
    private String password;
    
    public Usuario getUsuario(){
    	Usuario user= new Usuario();
    	user.setEmail(this.email);
    	user.setPassword(this.getPassword());
    	user.setNombre(this.nombre);
    	return user;
    }
    
}
