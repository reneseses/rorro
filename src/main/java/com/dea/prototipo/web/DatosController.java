package com.dea.prototipo.web;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.dea.prototipo.domain.Bodega;
import com.dea.prototipo.domain.Datos;
import com.dea.prototipo.web.DetalleForm;


import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.opensourcedea.dea.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@RequestMapping("/member/datoses")
@Controller
@RooWebScaffold(path = "datoses", formBackingObject = Datos.class)
public class DatosController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid DetalleForm detalleForm, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, new Datos());
            uiModel.addAttribute("form", detalleForm);
            return "datoses/create";
        }
        uiModel.asMap().clear();
        Datos dato= detalleForm.getDatos();
        //dato.persist();
        System.out.println(dato);
        return "redirect:/member/datoses/"; //+ encodeUrlPathSegment(dato.getId().toString(), httpServletRequest);
    }
    
    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Datos());
        List<String[]> dependencies = new ArrayList<String[]>();
        if (Bodega.countBodegas() == 0) {
            dependencies.add(new String[] { "bodega", "bodegas" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        uiModel.addAttribute("form", new DetalleForm());
        return "datoses/create";
    }
}
