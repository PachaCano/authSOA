package com.pc.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifTokenResponse {

    private String tokenEncript;

    private String username;

    private String rol;

    private Date expiraEn;

    private String mensaje;
}
