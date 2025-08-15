package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistReq {

    @NotBlank(message = " playlist Cannot be blank")
    @Size(max = 255, message = "name playlist <= 255")
    private String name;
    private String description;
    private Boolean isPublic = true;
}
