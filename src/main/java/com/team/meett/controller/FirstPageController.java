package com.team.meett.controller;

import com.team.meett.dto.TeamResponseDto;
import com.team.meett.dto.UserRequestDto;
import com.team.meett.service.TeamService;
import com.team.meett.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class FirstPageController {

    protected final UserService userService;
    protected final TeamService teamService;

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
        return "user/login";
    }

    @GetMapping("/home")
    public String home(HttpSession session, Model model){
        log.debug("session : {}", session.getId());

        List<TeamResponseDto> teamList = teamService.selectAll();
        model.addAttribute("ROOMS", teamList);

        return "home";
    }
}
