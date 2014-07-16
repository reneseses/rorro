package com.dea.prototipo.web;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.dea.prototipo.domain.Bodega;
import com.dea.prototipo.domain.Usuario;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/member/bodegas")
@Controller
@RooWebScaffold(path = "bodegas", formBackingObject = Bodega.class)
public class BodegaController {
	
	@RequestMapping(method = RequestMethod.POST, produces = "text/html")
	public String create(@Valid Bodega bodega, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
		Usuario usuario= (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	    if (bindingResult.hasErrors()) {
	        populateEditForm(uiModel, bodega);
	        return "bodegas/create";
	    }
	    uiModel.asMap().clear();
	    bodega.setUsuario(usuario);
	    bodega.persist();
	    return "redirect:/member/bodegas/" + encodeUrlPathSegment(bodega.getId().toString(), httpServletRequest);
	}
	
}
