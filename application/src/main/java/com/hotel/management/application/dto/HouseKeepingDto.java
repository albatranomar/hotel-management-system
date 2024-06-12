package com.hotel.management.application.dto;

import java.sql.Date;

public class HouseKeepingDto {
    private String id;

    private String task, status;

    private EmployeeDto employee;

    private RoomDto room;

    private Date date;
}