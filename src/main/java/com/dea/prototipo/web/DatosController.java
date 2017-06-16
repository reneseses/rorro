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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@RequestMapping("/member/datos")
@Controller
@RooWebScaffold(path = "datos", formBackingObject = Datos.class)
public class DatosController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid DetalleForm detalleForm, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, new Datos());
            uiModel.addAttribute("form", detalleForm);
            return "datos/create";
        }
        uiModel.asMap().clear();
        Datos dato= detalleForm.getDatos();
        dato.persist();
        System.out.println(dato);
        return "redirect:/member/datos/"; //+ encodeUrlPathSegment(dato.getId().toString(), httpServletRequest);
    }
    //-Djava.library.path=D:\Documentos\git\rorro\OSDEASolver-v0.287\
    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Datos());
        List<String[]> dependencies = new ArrayList<String[]>();
        if (Bodega.countBodegas() == 0) {
            dependencies.add(new String[] { "bodega", "bodegas" });
        }
        uiModel.addAttribute("dependencies", dependencies);
        uiModel.addAttribute("form", new DetalleForm());
        return "datos/create";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("datos", Datos.findDatosEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) Datos.countDatoses() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            System.out.println(Datos.findAllDatoses());
            uiModel.addAttribute("datos", Datos.findAllDatoses(sortFieldName, sortOrder));
        }
        addDateTimeFormatPatterns(uiModel);
        return "datos/list";
    }
}
