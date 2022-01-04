package com.team.meett.controller;

import com.team.meett.dto.UserRequestDto;
import com.team.meett.model.Users;
import com.team.meett.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FirstPageController {

    protected final UserService userService;

    @GetMapping("/")
    public String firstPage(){
        return "user/main";
    }

    @GetMapping("/user/login")
    public String login(){
        return "user/login";
    }

    @GetMapping("/user/register")
    public String register(Model model){
        UserRequestDto user = new UserRequestDto();
        model.addAttribute("USER", user);
        return "user/register";
    }

    @PostMapping("/user/register")
    public String register(UserRequestDto users){
        userService.insert(users);
        return "redirect:/";
    }

    @GetMapping("/home")
    public String home(HttpSession session){
        log.debug("session : {}", session.getId());
        return "home";
    }
}
