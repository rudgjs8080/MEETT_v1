package com.team.meett.controller;

import com.team.meett.dto.*;
import com.team.meett.model.Room;
import com.team.meett.model.Team;
import com.team.meett.model.TeamSchedule;
import com.team.meett.repository.TeamRepository;
import com.team.meett.service.*;
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
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/schedule")
@RequiredArgsConstructor
public class TeamScheduleController {

    protected final TeamScheduleService teamScheduleService;
    protected final UserScheduleService userScheduleService;
    protected final RoomService roomService;
    protected final TeamService teamService;
    protected final TeamRepository teamRepository;
    protected final SearchService searchService;
    protected final UserService userService;


    // team schedule 전체 조회
    @GetMapping("/team/{teamId}")
    public String selectUsername(@PathVariable String teamId, Model model){
        List<TsResponseDto> tsList = teamScheduleService.findByTeamId(teamId);

        if(tsList.isEmpty()){
            model.addAttribute("error", "스케줄이 없는 모임입니다");
            //잘못된 리퀘스트가 아닌 아직 정해진 스케줄이 없는 모임일 경우
            return "error";
        }
        model.addAttribute("tsList", tsList);
        return "home";
    }
    /**
     * 팀 스케줄의 상세내용으로 검색
     * 검색할 팀의 Id 값과 검색어를 받아서 조회함
     */
    @GetMapping("/team/search/detail")
    public String searchSchedule(@RequestParam(value="teamId", required = false) String teamId,
                                            @RequestParam(value="detail", required = false) String detail, Model model){
        List<TeamSchedule> teamScheduleList;
        if(detail != null && teamId != null){
            if(!teamService.findById(teamId).isEmpty()){
                teamScheduleList = searchService.searchByTeam_idAndDetailContains(teamId, detail);
                if(teamScheduleList.isEmpty()){
                    model.addAttribute("error", "detail 을 포함한 상세일정은 존재하지 않습니다");
                    return "error";
                }
            } else {
                model.addAttribute("error", "존재하지 않는 팀입니다");
                return "error";
            }
        } else {
            model.addAttribute("error", "teamId 또는 detail이 없습니다");
            return "error";
        }
        model.addAttribute("tsDetailList", teamScheduleList);
        return "home";
    }
    /**
     * 지정한 날짜를 통해 현재 페이지의 팀의 일정을 조회하는 method
     * 날짜 하루를 조회하려면 start 와 end를 같은 값으로 보내줘야함 ex) start = 2021-12-23, end = 2021-12-23
     */
    @GetMapping("/team/search/date")
    public String searchTeamDate(@RequestParam(value = "teamId", required = false)String teamId,
                                            @RequestParam(value = "start", required = false)String start,
                                            @RequestParam(value = "end", required = false) String end, Model model) throws ParseException {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if((teamId != null)&&(start != null) && (end != null)){
            if(!teamService.findById(teamId).isEmpty()){
                Date dStart = formatter.parse(start);
                Date dEnd = formatter.parse(end);
                if(dStart.equals(dEnd) || dStart.before(dEnd)){
                    List<TeamSchedule> teamScheduleList = searchService.searchByTeamDate(teamId, dStart, dEnd);
                    /**
                     * DB의 Date Type과 Entity의 Date Type 을 일치 시켜주고 시간은 빼야한다
                     * 현재 이렇게 시간 값까지 도출된다 -> 2021-02-05 00:00:00.0
                     * 데이터는 잘 나옴
                     */
                    model.addAttribute("tsDateList", teamScheduleList);
                    return "home";
                } else {
                    model.addAttribute("error", "start 값이 end 값보다 큽니다");
                    return "error";
                }
            } else {
                model.addAttribute("error", "teamId가 존재하지 않습니다");
                return "error";
            }
        }
        model.addAttribute("error", "teamId, start, end 중 null 값이 있습니다");
        return "error";
    }

    /**
     * TeamSchedule 에서 Role 에 맞는 일정 조회
     */
    @GetMapping("/team/search/role")
    public String searchTeamScheduleRole(@RequestParam(value = "teamId", required = false) String teamId,
                                                    @RequestParam(value = "role", required = false)Integer role, Model model){
        if((teamId != null) && (role != null)){
            if(teamService.findById(teamId) != null){
                if(role == 0 || role == 1){
                    List<TsResponseDto> tsList= searchService.searchByTeamScheduleRole(teamId, role);
                    model.addAttribute("tsRoleList", tsList);
                    return "home";
                } else {
                    model.addAttribute("error", "role 값이 옳지 않습니다");
                    return "error";
                }
            } else {
                model.addAttribute("error", "해당 팀이 존재하지 않습니다");
                return "error";
            }
        }
        model.addAttribute("error", "role 또는 teamId 가 null");
        return "error";
    }

    // user schedule -> teamSchedule
    @PostMapping("/team/{userSeq}/{teamId}")
    public String userScheduleInsert(@PathVariable Long userSeq, @PathVariable String teamId){
        Optional<UsResponseDto> userSchedule = userScheduleService.findById(userSeq);

        TsRequestDto tsRequestDto = new TsRequestDto();
        tsRequestDto.setUsername(userSchedule.get().getUsername());
        tsRequestDto.setTitle(userSchedule.get().getTitle());
        tsRequestDto.setDetail(userSchedule.get().getDetail());
        tsRequestDto.setStart(userSchedule.get().getStart());
        tsRequestDto.setEnd(userSchedule.get().getEnd());
        tsRequestDto.setTeamId(teamId);

        teamScheduleService.insert(tsRequestDto);
        return "redirect:/schedule/team";
    }

    // 팀방 스케줄 등록
    @PostMapping("/team")
    public String insert(@RequestBody TsRequestDto teamSchedule){

        UsRequestDto usRequestDto = new UsRequestDto();

        int i = 0;
        teamScheduleService.insert(teamSchedule);
        log.debug(teamSchedule.getTeamId());
        List<Room> roomList = roomService.findByTeamId(teamSchedule.getTeamId());
        for(i = 0; i < roomList.size(); i++){
            usRequestDto.setUsername(roomList.get(i).getUsername());
            usRequestDto.setTitle(teamSchedule.getTitle());
            usRequestDto.setDetail(teamSchedule.getDetail());
            usRequestDto.setStart(teamSchedule.getStart());
            usRequestDto.setEnd(teamSchedule.getEnd());
            usRequestDto.setRole(teamSchedule.getRole());
            userScheduleService.insert(usRequestDto);
            log.debug(i + " 번째" + roomList.get(i).getUsername() + "insert 완료");
        }

        return "redirect:/schedule/team";

    }


    // 팀방 스케줄 수정
    @PutMapping("/team/{seq}")
    public String update(@RequestBody TsRequestDto updateTeamSchedule, @PathVariable Long seq){
        teamScheduleService.update(updateTeamSchedule,seq);
        return "redirect:/schedule/team";
    }

    @DeleteMapping("/team/{username}/{seq}")
    public String delete(@PathVariable String username, @PathVariable Long seq, Model model){
        if(((userService.findById(username).getUsername()) != null) && seq != null){
            if(!teamScheduleService.findById(seq).isEmpty()){
                if(teamScheduleService.findById(seq).get().getUsername().equals(username)){
                    teamScheduleService.delete(seq);
                    return "redirect:/schedule/team";
                } else {
                    model.addAttribute("error", "해당 유저는 팀장이 아닙니다. 팀 일정은 팀장만 삭제 할 수 있습니다");
                    return "error";
                }
            } else {
                model.addAttribute("해당 seq 값은 존재하지 않습니다");
                return "error";
            }
        } else{
            model.addAttribute("error", "username 또는 seq 값이 null");
            return "error";
        }
    }




}
