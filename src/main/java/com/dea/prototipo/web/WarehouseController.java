package com.dea.prototipo.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.dea.prototipo.domain.*;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

@RequestMapping("/member/warehouse")
@Controller
public class WarehouseController {

    @RequestMapping(method = RequestMethod.POST, produces = "text/html")
    public String create(
            @Valid Warehouse warehouse,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest httpServletRequest) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                System.out.println(error);
            }
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
        Warehouse warehouse = Warehouse.findWarehouse(id);
        uiModel.addAttribute("warehouse", warehouse);
        uiModel.addAttribute("warehouseData", WarehouseData.findWarehouseDataByWarehouse(warehouse));
        uiModel.addAttribute("itemId", id);
        return "member/warehouse/show";
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "text/html")
    public String update(
            @Valid Warehouse warehouse,
            BindingResult bindingResult,
            Model uiModel,
            HttpServletRequest httpServletRequest) {

        System.out.println("update");
        System.out.println(warehouse);

        if (bindingResult.hasErrors()) {
            populateEditForm(uiModel, warehouse);
            return "member/warehouse/update";
        }
        uiModel.asMap().clear();
        warehouse.merge();
        return "redirect:/member/warehouse/" + encodeUrlPathSegment(warehouse.getId().toString(), httpServletRequest);
    }

    @RequestMapping(value = "/{id}/image", method = RequestMethod.POST, produces = "text/html")
    @ResponseBody
    public ResponseEntity<String> saveImage(
            @PathVariable("id") Long id,
            @RequestParam(value = "image") MultipartFile file,
            @RequestParam(required = false) Integer x,
            @RequestParam(required = false) Integer y,
            @RequestParam(required = false) Integer w,
            @RequestParam(required = false) Integer h) {

        Warehouse warehouse = Warehouse.findWarehouse(id);
        saveImage(warehouse, file, x, y, w, h);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json; charset=utf-8");
        return new ResponseEntity<>("", headers, HttpStatus.OK);
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
        return "redirect:/member";
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

    private void saveImage(Warehouse warehouse, MultipartFile file, Integer x, Integer y, Integer w, Integer h) {
        try {
            WarehouseImage warehouseImage = new WarehouseImage();
            if (x == null || y == null || w == null || h == null) {
                warehouseImage.setContent(file.getBytes());
            } else {
                warehouseImage.setContent(file.getBytes(), x, y, w, h);
            }

            warehouseImage.setId(warehouse.getId());
            warehouseImage.merge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
