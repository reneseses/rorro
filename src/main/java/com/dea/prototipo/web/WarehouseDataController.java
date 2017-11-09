package com.dea.prototipo.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.dea.prototipo.domain.InputWeightEnum;
import com.dea.prototipo.domain.Warehouse;
import com.dea.prototipo.domain.WarehouseData;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
@RequestMapping("/member/warehouse/{warehouseId}/data")
@Controller
public class WarehouseDataController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(@PathVariable("warehouseId") Long warehouseId, @Valid WarehouseData warehouseData, BindingResult bindingResult, Model uiModel) {
        Warehouse warehouse = Warehouse.findWarehouse(warehouseId);
        warehouseData.setWarehouse(warehouse);

        if (bindingResult.hasErrors() && !(bindingResult.getAllErrors().size() == 1 && bindingResult.hasFieldErrors("warehouse"))) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                System.out.println(error);
            }

            populateEditForm(uiModel, warehouseData);
            uiModel.addAttribute("warehouse", warehouse);
            return "member/warehouse/data/create";
        }
        uiModel.asMap().clear();
        warehouseData.getOutput().persist();
        warehouseData.getVehicles().persist();
        warehouseData.getStorage().persist();
        warehouseData.getConveyor().persist();
        warehouseData.persist();
        return "redirect:/member/warehouse/" + warehouseId;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    ResponseEntity<String> list(@PathVariable Long warehouseId, Model uiModel) {
        Warehouse warehouse = Warehouse.findWarehouse(warehouseId);
        List<WarehouseData> warehouseData = WarehouseData.findWarehouseDataByWarehouse(warehouse);

        JSONArray response = new JSONArray();
        for (WarehouseData current : warehouseData) {
            response.add(current.toString());
        }

        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    @RequestMapping(params = "form", produces = "text/html")
    public String createForm(@PathVariable("warehouseId") Long warehouseId, Model uiModel) {
        populateEditForm(uiModel, new WarehouseData());
        uiModel.addAttribute("warehouse", Warehouse.findWarehouse(warehouseId));
        return "member/warehouse/data/create";
    }

    @RequestMapping(value = "/{id}", produces = "text/html")
    public String show(@PathVariable("id") Long id, Model uiModel) {
        uiModel.addAttribute("warehouseData", WarehouseData.findWarehouseData(id));
        uiModel.addAttribute("itemId", id);
        return "member/warehouse/data/show";
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST, produces = "text/html")
    public String update(@PathVariable("warehouseId") Long warehouseId, @PathVariable("id") Long id, @Valid WarehouseData warehouseData, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        Warehouse warehouse = Warehouse.findWarehouse(warehouseId);
        warehouseData.setWarehouse(warehouse);

        if (bindingResult.hasErrors() && !(bindingResult.getAllErrors().size() == 1 && bindingResult.hasFieldErrors("warehouse"))) {
            populateEditForm(uiModel, warehouseData);
            uiModel.addAttribute("warehouse", warehouse);
            return "member/warehouse/data/create";
        }

        uiModel.asMap().clear();
        warehouseData.getStorage().merge();
        warehouseData.getVehicles().merge();
        warehouseData.getConveyor().merge();
        warehouseData.getOutput().merge();

        warehouseData.update();
        return "redirect:/member/warehouse/" + warehouseId;
    }

    @RequestMapping(value = "/{id}", params = "form", produces = "text/html")
    public String updateForm(@PathVariable("warehouseId") Long warehouseId, @PathVariable("id") Long id, Model uiModel) {
        populateEditForm(uiModel, WarehouseData.findWarehouseData(id));
        uiModel.addAttribute("warehouse", Warehouse.findWarehouse(warehouseId));
        return "member/warehouse/data/create";
    }

    @RequestMapping(value = "/remove/{id}", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody
    ResponseEntity<String> delete(@PathVariable("warehouseId") Long warehouseId, @PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model uiModel) {
        WarehouseData warehouseData = WarehouseData.findWarehouseData(id);
        warehouseData.remove();
        JSONObject response = new JSONObject();
        return new ResponseEntity<>(response.toString(), HttpStatus.OK);
    }

    private void populateEditForm(Model uiModel, WarehouseData warehouseData) {
        uiModel.addAttribute("warehouseData", warehouseData);
        uiModel.addAttribute("inputWeight", Arrays.asList(InputWeightEnum.values()));
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
