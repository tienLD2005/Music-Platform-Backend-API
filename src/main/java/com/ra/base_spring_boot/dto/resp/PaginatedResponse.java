package com.ra.base_spring_boot.dto.resp;

import com.ra.base_spring_boot.model.base.Pagination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    private List<T> items;
    private Pagination pagination;
}
