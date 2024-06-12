package com.hotel.management.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@Data
@AllArgsConstructor
public class HouseKeepingDto {
    private String id;

    private String task, status;

    private EmployeeDto employee;

    private RoomDto room;

    private Date date;
}