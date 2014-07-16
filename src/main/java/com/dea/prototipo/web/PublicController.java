package com.dea.prototipo.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dea.prototipo.domain.Usuario;

@RequestMapping("/")
@Controller
public class PublicController {
	
	@Autowired
	private MessageDigestPasswordEncoder messageDigestPasswordEncoder;
	
	@RequestMapping(value="signup", params= "form")
	public String signupForm(Model uiModel){
		uiModel.addAttribute("signupForm", new SignupForm());
		return "signup";
	}
	
	@RequestMapping(value="signup",method= RequestMethod.POST)
	public String signup(@Valid SignupForm signupForm, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		if (bindingResult.hasErrors()) {
			uiModel.addAttribute("signupForm",  signupForm);
            return "signup";
        }
        uiModel.asMap().clear();
        Usuario usuario= signupForm.getUsuario();
        usuario.setPassword(messageDigestPasswordEncoder.encodePassword(usuario.getPassword(), null));
        usuario.persist();
        
        return "redirect:/login";
	}

}
