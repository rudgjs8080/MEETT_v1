package com.team.meett.controller;

import com.team.meett.dto.UsRequestDto;
import com.team.meett.dto.UsResponseDto;
import com.team.meett.model.UserSchedule;
import com.team.meett.service.SearchService;
import com.team.meett.service.UserScheduleService;
import com.team.meett.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:3000")
public class UserScheduleController {

    protected final UserScheduleService UsService;
    protected final UserService userService;
    protected final SearchService searchService;

    //    select findByUsername
    @GetMapping("/user/{username}")
    public String selectUsername(@PathVariable String username, Model model) {

        List<UsResponseDto> UsList = UsService.findByUsername(username);
        if (UsList.isEmpty()) {
            model.addAttribute("error", "List가 존재하지 않습니다");
            return "error";
        }
        model.addAttribute("usList", UsList);
        return "home";
    }
    /**
     * 지정한 날짜를 통해 현재 페이지의 유저의 일정을 조회하는 method
     * 날짜 하루를 조회하려면 start 와 end를 같은 값으로 보내줘야함 ex) start = 2021-12-23, end = 2021-12-23
     */
    @GetMapping("/user/search/date")
    public String searchUserDate(@RequestParam(value = "username", required = false) String username,
                                            @RequestParam(value = "start", required = false) String start,
                                            @RequestParam(value = "end", required = false) String end, Model model) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if((username != null) && (start != null) && (end != null)){
            if(userService.findById(username) != null){
                Date dStart = formatter.parse(start);
                Date dEnd = formatter.parse(end);
                if(dStart.equals(dEnd) || dStart.before(dEnd)){
                    List<UserSchedule> usList = searchService.searchByUserDate(username, dStart, dEnd);
                    model.addAttribute("usDateList", usList);
                    return "home";
                } else {
                    model.addAttribute("error", "start 값이 end 값보다 큽니다 다시 확인해주세요");
                    return "error";
                }
            } else {
                model.addAttribute("error", "username이 존재하지 않습니다");
                return "error";
            }
        }
        model.addAttribute("error", "username, start, end 값 중 하나가 없습니다");
        return "error";
    }

    /**
     * UserSchedule에서 Role 에 맞는 일정 조회
     */
    @GetMapping("/user/search/role")
    public String searchUserScheduleRole(@RequestParam(value = "username", required = false) String username,
                                                    @RequestParam(value = "role", required = false) Integer role, Model model){
        if((username != null) && (role != null)){
            if(userService.findById(username) != null){
                if(role == 0 || role == 1){
                    List<UsResponseDto> usList = searchService.searchByUserScheduleRole(username, role);
                    model.addAttribute("usRoleList", usList);
                    return "home";
                } else {
                    model.addAttribute("error", "role 값이 옳지 않습니다");
                    return "error";
                }
            } else {
                model.addAttribute("error", "해당 user는 존재하지 않습니다");
                return "error";
            }
        }
        model.addAttribute("error", "username 또는 role 값이 null 입니다");
        return "error";
    }

    @PostMapping("/user")
    public String insert(@RequestBody UsRequestDto userSchedule) {
        UsService.insert(userSchedule);
        return "redirect:/schedule/user";
    }

    //    update
    @PutMapping("/user/{seq}")
    public String update(@RequestBody UsRequestDto updateUserSchedule, @PathVariable Long seq) {
        UsService.update(updateUserSchedule, seq);
        return "redirect:/schedule/user";
    }

    //    delete
    @DeleteMapping("/user/{seq}")
    public String delete(@PathVariable Long seq) {

        UsService.delete(seq);
        return "redirect:/schedule/user";
    }

}
