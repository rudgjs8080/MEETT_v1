package com.team.meett.controller;

import com.team.meett.dto.TeamRequestDto;
import com.team.meett.dto.TeamResponseDto;
import com.team.meett.repository.RoomRepository;
import com.team.meett.service.SearchService;
import com.team.meett.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
public class TeamController {

    protected final TeamService teamService;
    protected final SearchService searchService;

    @PostMapping("/team/create")
    public String teamCreate(@RequestBody TeamRequestDto team) {
        teamService.insert(team);
        return "redirect:/";
    }

    //update
    @PutMapping(value = "/team/{TeamId}")
    public String update(@RequestBody TeamRequestDto updateTeam, @PathVariable String TeamId) {
        teamService.update(updateTeam, TeamId);
        return "redirect:/";
    }

    @DeleteMapping("/team/{teamId}")
    public String delete(@PathVariable String teamId) {
        teamService.delete(teamId);
        return "redirect:/";
    }

}
