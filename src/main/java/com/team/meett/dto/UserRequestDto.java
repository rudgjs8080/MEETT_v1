package com.team.meett.dto;

import com.team.meett.model.Users;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    private String username;
    private String password;
    private String email;
    private String nickname;

    public Users toEntity(){
        return Users.builder()
                .username(username)
                .password(password)
                .email(email)
                .nickname(nickname)
                .build();
    }
}
