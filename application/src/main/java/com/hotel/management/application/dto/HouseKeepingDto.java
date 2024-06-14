package com.hotel.management.application.dto;

import com.hotel.management.application.dto.validation.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class HouseKeepingDto extends RepresentationModel<HouseKeepingDto> {
    private String id;

    @NotBlank(groups = OnCreate.class)
    private String task, status;

    @NotNull(groups = OnCreate.class)
    private Date date;

    private EmployeeDto employee;

    private RoomDto room;
}