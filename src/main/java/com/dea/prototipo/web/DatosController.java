package com.dea.prototipo.web;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import com.dea.prototipo.domain.Datos;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.opensourcedea.dea.*;
import java.util.ArrayList;

@SuppressWarnings("unused")
@RequestMapping("/member/datoses")
@Controller
@RooWebScaffold(path = "datoses", formBackingObject = Datos.class)
public class DatosController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Datos dato, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, dato);
            return "datoses/create";
        }
        uiModel.asMap().clear();
        dato.persist();
        return "redirect:/member/datoses/" + encodeUrlPathSegment(dato.getId().toString(), httpServletRequest);
    }
}
