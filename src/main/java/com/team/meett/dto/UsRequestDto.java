package com.team.meett.dto;

import com.team.meett.model.UserSchedule;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

//put, post
@Getter
@Setter
public class UsRequestDto {
    private String username;
    private String title;
    private String detail;
    private Date start;
    private Date end;
    private int role;

    public UserSchedule toEntity(){
        return UserSchedule.builder()
                .username(username)
                .title(title)
                .detail(detail)
                .start(start)
                .end(end)
                .build();
    }


}
