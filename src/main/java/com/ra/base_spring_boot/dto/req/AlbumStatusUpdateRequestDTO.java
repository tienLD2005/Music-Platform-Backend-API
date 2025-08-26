package com.ra.base_spring_boot.dto.req;

import com.ra.base_spring_boot.model.constants.AlbumStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlbumStatusUpdateRequestDTO {

    @NotNull(message = "Status is required")
    private AlbumStatus status;
}
