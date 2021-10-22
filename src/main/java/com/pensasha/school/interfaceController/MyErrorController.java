package com.pensasha.school.interfaceController;

import com.pensasha.school.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

public class MyErrorController implements ErrorController {
    @Autowired
    UserService userService;

    @RequestMapping(value={"/error"})
    public String handleError(Principal principal, Model model) {
        model.addAttribute("activeUser", this.userService.getByUsername(principal.getName()));
        return "error";
    }

    public String getErrorPath() {
        return null;
    }
}