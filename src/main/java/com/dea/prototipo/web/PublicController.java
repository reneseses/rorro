package com.dea.prototipo.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.dea.prototipo.domain.Warehouse;
import com.dea.prototipo.web.forms.SignupForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dea.prototipo.domain.User;
import org.springframework.web.bind.annotation.RequestParam;

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

}
