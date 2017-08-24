package com.dea.prototipo.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.dea.prototipo.domain.Warehouse;
import com.dea.prototipo.domain.WarehouseData;


import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@RequestMapping("/member/warehouse/{warehouseId}/data")
@Controller
public class WarehouseDataController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@PathVariable("warehouseId") Long warehouseId, @Valid WarehouseData warehouseData, BindingResult bindingResult, Model uiModel) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, warehouseData);
            return "member/warehouse/data/create";
        }
        uiModel.asMap().clear();
        warehouseData.persist();
        System.out.println(warehouseData.toString());
        return "redirect:/member/warehouse/" + warehouseId;
    }

    //-Djava.library.path=D:\Documentos\git\rorro\OSDEASolver-v0.287\
    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new WarehouseData());
        return "member/warehouse/data/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("warehouseData", WarehouseData.findWarehouseData(id));
        uiModel.addAttribute("itemId", id);
        return "member/warehouse/data/show";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@PathVariable("warehouseId") Long warehouseId, @Valid WarehouseData warehouseData, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, warehouseData);
            return "member/warehouse/data/update";
        }
        uiModel.asMap().clear();
        warehouseData.merge();
        return "redirect:/member/warehouse/" + warehouseId;
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, WarehouseData.findWarehouseData(id));
        return "member/warehouse/data/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("warehouseId") Long warehouseId, @PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        WarehouseData warehouseData = WarehouseData.findWarehouseData(id);
        warehouseData.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/member/warehouse/" + warehouseId;
    }

    private void populateEditForm(Model uiModel, WarehouseData warehouseData) {
        uiModel.addAttribute("warehouseData", warehouseData);
    }

    private String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
        String enc = httpServletRequest.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        } catch (UnsupportedEncodingException uee) {
        }
        return pathSegment;
    }
}
