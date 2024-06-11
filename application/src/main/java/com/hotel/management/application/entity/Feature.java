package com.hotel.management.application.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Feature {
    @Id
    private String id;
    private String fnamee;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
