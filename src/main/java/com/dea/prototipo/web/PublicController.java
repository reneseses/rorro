package com.dea.prototipo.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.dea.prototipo.domain.*;
import com.dea.prototipo.web.forms.SignupForm;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

@RequestMapping("/")
@Controller
public class PublicController {

    @Autowired
    private MessageDigestPasswordEncoder messageDigestPasswordEncoder;

    @RequestMapping(value = "/")
    public String index(Model uiModel) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof User) {
            return "redirect:/member";
        }

        uiModel.addAttribute("signupForm", new SignupForm());

        return "index";
    }

    @RequestMapping(value = "/member")
    public String member(Model uiModel) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        uiModel.addAttribute("warehouses", Warehouse.findWarehousesByUser(user).getResultList());
        return "member/index";
    }

    @RequestMapping(value = "signup", params = "form")
    public String signupForm(Model uiModel) {
        uiModel.addAttribute("signupForm", new SignupForm());
        return "signup";
    }

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    public String signup(@Valid SignupForm signupForm, BindingResult bindingResult, Model uiModel, HttpServletRequest httpServletRequest) {
        if (bindingResult.hasErrors()) {
            uiModel.addAttribute("signupForm", signupForm);
            return "signup";
        }
        uiModel.asMap().clear();
        User user = signupForm.getUser();
        user.setPassword(messageDigestPasswordEncoder.encodePassword(user.getPassword(), null));
        user.persist();

        return "redirect:/login";
    }

    @RequestMapping(value = "/testData", produces = "text/json")
    public @ResponseBody
    ResponseEntity<String> createTestData() {
        OperationType[] oTypes = OperationType.values();
        ProductType[] pTypes = ProductType.values();
        TILevel[] tiLevels = TILevel.values();

        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setName("User" + i);
            user.setPassword(messageDigestPasswordEncoder.encodePassword("passpass", null));
            user.setEmail("user" + i + "@test.com");
            user.persist();

            for (int j = 0; j < 10; j++) {
                Random rand = new Random();
                int oType = rand.nextInt(oTypes.length);
                int pType = rand.nextInt(pTypes.length);
                int tiLevel = rand.nextInt(tiLevels.length);
                Warehouse warehouse = new Warehouse();
                warehouse.setName("Warehouse-" + i + "-" + j);
                warehouse.setOperationType(oTypes[oType]);
                warehouse.setProductType(pTypes[pType]);
                warehouse.setTiLevel(tiLevels[tiLevel]);
                warehouse.setUser(user);
                warehouse.persist();

                for (int k = 0; k < 10; k++) {
                    WarehouseDataConveyor conveyor = new WarehouseDataConveyor();
                    conveyor.persist();

                    WarehouseDataStorage storage = new WarehouseDataStorage();
                    storage.persist();

                    WarehouseDataVehicles vehicles = new WarehouseDataVehicles();
                    vehicles.setPalletTruck(rand.nextInt(3) + 1);
                    vehicles.setWalkieStacker(rand.nextInt(1));
                    vehicles.persist();

                    WarehouseDataOutput output = new WarehouseDataOutput();
                    output.setTotalOrders(rand.nextInt(1000) + 1000);
                    output.setBrokenCaseLines(rand.nextInt(1000) + 500);
                    output.setFullCaseLines(rand.nextInt(1000) + 500);

                    int diff = output.getTotalOrders() - output.getBrokenCaseLines() - output.getFullCaseLines();
                    output.setPalletLines(diff > 0 ? rand.nextInt(1000) + diff : rand.nextInt(1000) + 500);

                    output.setFloorStacking(rand.nextInt(200) + 50);
                    output.setBrokenCasePickSlots(rand.nextInt(200) + 50);
                    output.setPalletRackLocations(rand.nextInt(200) + 50);
                    output.persist();

                    WarehouseData wData = new WarehouseData();
                    wData.setDirectWorkforce(rand.nextInt(200000));
                    wData.setIndirectWorkforce(rand.nextInt(200000));
                    wData.setPeriod(2007 + k);
                    wData.setSquareMeters(rand.nextInt(400) + 100);
                    wData.setWarehouse(warehouse);
                    wData.setConveyor(conveyor);
                    wData.setStorage(storage);
                    wData.setVehicles(vehicles);
                    wData.setOutput(output);
                    wData.persist();
                }
            }
        }

        return new ResponseEntity<>(new JSONObject().toString(), HttpStatus.OK);
    }

}
