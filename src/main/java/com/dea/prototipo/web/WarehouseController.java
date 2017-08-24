package com.dea.prototipo.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.dea.prototipo.domain.WarehouseData;
import com.dea.prototipo.domain.OperationType;
import com.dea.prototipo.domain.User;
import com.dea.prototipo.domain.Warehouse;

import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.Arrays;

@RequestMapping("/member/warehouse")
@Controller
public class WarehouseController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@Valid Warehouse warehouse, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, warehouse);
            return "member/warehouse/create";
        }
        uiModel.asMap().clear();
        warehouse.setUser(user);
        warehouse.persist();
        return "redirect:/member/warehouse/" + encodeUrlPathSegment(warehouse.getId().toString(), httpServletRequest);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(Model uiModel) {
        populateEditForm(uiModel, new Warehouse());
        return "member/warehouse/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("warehouse", Warehouse.findWarehouse(id));
        uiModel.addAttribute("itemId", id);
        return "member/warehouse/show";
    }

    @RequestMapping(produces = "text/html")
    public String list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, @RequestParam(value = "sortFieldName", required = false) String sortFieldName, @RequestParam(value = "sortOrder", required = false) String sortOrder, Model uiModel) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            final int firstResult = page == null ? 0 : (page.intValue() - 1) * sizeNo;
            uiModel.addAttribute("warehouses", Warehouse.findWarehouseEntries(firstResult, sizeNo, sortFieldName, sortOrder));
            float nrOfPages = (float) Warehouse.countWarehouses() / sizeNo;
            uiModel.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            uiModel.addAttribute("warehouses", Warehouse.findAllWarehouses(sortFieldName, sortOrder));
        }
        return "member/warehouse/list";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(@Valid Warehouse warehouse, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, warehouse);
            return "member/warehouse/update";
        }
        uiModel.asMap().clear();
        warehouse.merge();
        return "redirect:/member/warehouse/" + encodeUrlPathSegment(warehouse.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, Warehouse.findWarehouse(id));
        return "member/warehouse/update";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = "text/html")
    public String delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        Warehouse warehouse = Warehouse.findWarehouse(id);
        warehouse.remove();
        uiModel.asMap().clear();
        uiModel.addAttribute("page", (page == null) ? "1" : page.toString());
        uiModel.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/member/warehouse";
    }

    void populateEditForm(Model uiModel, Warehouse warehouse) {
        uiModel.addAttribute("warehouse", warehouse);
        uiModel.addAttribute("operationTypes", Arrays.asList(OperationType.values()));
    }

    String encodeUrlPathSegment(String pathSegment, HttpServletRequest httpServletRequest) {
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
