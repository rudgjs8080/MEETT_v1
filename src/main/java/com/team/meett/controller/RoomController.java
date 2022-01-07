package com.team.meett.controller;

import com.team.meett.dto.SearchTeamResponseDto;
import com.team.meett.dto.TeamResponseDto;
import com.team.meett.model.Room;
import com.team.meett.service.RoomService;
import com.team.meett.service.SearchService;
import com.team.meett.service.TeamService;
import com.team.meett.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/group")
@RequiredArgsConstructor
public class RoomController {

    protected final RoomService roomService;
    protected final SearchService searchService;
    protected final TeamService teamService;
    protected final UserService userService;

//    protected final PasswordEncoder passwordEncoder;

    @GetMapping("/{username}")
    public String selectTeam(@PathVariable String username, Model model){
        List<Room> roomList = roomService.findByUsername(username);

        if(roomList.isEmpty()){
            model.addAttribute("error", "roomList가 존재하지 않습니다");
            return "error";
        }
        model.addAttribute("roomList", roomList);
        return  "home";
    }

    @GetMapping("/teamId/{teamId}")
    public String selectUser(@PathVariable String teamId, Model model){
        List<Room> roomList = roomService.findByTeamId(teamId);
        if(roomList.isEmpty()){
            model.addAttribute("error", "room 데이터 없음");
            return "error";
        }
        model.addAttribute("roomList", roomList);
        return "home";
    }

    @DeleteMapping("/exit/{username}/{teamId}")
    public String delete(@PathVariable String username, @PathVariable String teamId, Model model){
        Room room = roomService.findByUsernameAndTeamId(username, teamId);

        if(room.getId() == null){
            model.addAttribute("error", "해당 room은 존재하지 않습니다");
        }
        roomService.delete(room.getId());
        return "redirect:/";
    }

    @PostMapping("/join")
    public String joinRoom(@RequestBody Room room){
        roomService.insert(room);
        return "redirect:/";
    }

    /**
     * 방 제목(title)로 검색하는 method
     * 검색어와 정확히 일치하는 title이 있을때는
     * list 의 index 0값과 1값이 동일함
     *      (검색어와 정확히 일치하는 값 = index 0과 1, 포함하고 있는 값 2 ~ list.size() 까지)
     * 검색어와 정확히 일치하는 title이 없을 경우
     * 검색어를 포함하고 있는 결과값들이 출력됨
     *
     * url 경로 이름 수정해야함 (RoomController)
     */
    @GetMapping("/search/team")
    public String searchTeam(@RequestParam(value="title", required = false) String title, Model model){

        List<SearchTeamResponseDto> teamList;
        if(title != null){
            teamList = searchService.searchByContainTeamTitle(title);
            teamList.addAll(0,searchService.searchByTeamTitle(title));

            if(teamList.isEmpty()){
                model.addAttribute("error", "해당 title이 존재하지 않습니다");
                return "error";
            }
        } else {
            model.addAttribute("error", "검색어를 입력하지 않았습니다");
            return "error";
        }
        model.addAttribute("teamList", teamList);
        return "home";
    }
    /**
     * 새로운 팀 입장시 비밀번호 확인 method
     * url 경로 수정 필요
     */
    @PostMapping("/test3")
    public String searchPassword(@RequestParam(value = "teamId", required = false) String teamId,
                                            @RequestParam(value = "password", required = false) String password){
        //log.debug(teamService.findById(teamId).get().getPassword());
        //log.debug(password);
        // 암호화된 비밀번호 복호화 확인
        TeamResponseDto responseDto = teamService.findById(teamId).orElseThrow(()->new IllegalArgumentException(teamId));
//        if(!passwordEncoder.matches(password, responseDto.getPassword())){
//            return ResponseEntity.badRequest().body("비밀번호 불일치");
//        }
        return "redirect:/";
    }
}
