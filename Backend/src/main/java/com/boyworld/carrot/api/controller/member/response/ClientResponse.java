package com.boyworld.carrot.api.controller.member.response;

import com.boyworld.carrot.domain.member.Member;
import com.boyworld.carrot.domain.member.Role;
import lombok.Builder;
import lombok.Data;

@Data
public class ClientResponse {

    private String name;

    private String nickname;

    private String email;

    private String phoneNumber;

    private String role;

    @Builder
    public ClientResponse(String name, String nickname, String email, String phoneNumber, Role role) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role.toString();
    }

    public static ClientResponse of(Member member) {
        return ClientResponse.builder()
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .role(member.getRole())
                .build();
    }
}
