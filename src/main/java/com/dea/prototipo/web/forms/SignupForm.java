package com.dea.prototipo.web.forms;

import javax.validation.constraints.NotNull;

import com.dea.prototipo.domain.User;
import org.springframework.roo.addon.javabean.RooJavaBean;

@RooJavaBean
public class SignupForm {

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String password;

    public User getUser() {
        User user = new User();
        user.setEmail(this.email);
        user.setPassword(this.getPassword());
        user.setName(this.name);
        user.setEnabled(true);
        return user;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
