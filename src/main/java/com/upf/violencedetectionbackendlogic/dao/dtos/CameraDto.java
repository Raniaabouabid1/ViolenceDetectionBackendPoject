package com.upf.violencedetectionbackendlogic.dao.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CameraDto{
    private UUID id;
    private String name;
    private String sectionName;
    private String sectionId;
    private String lastKnownIp;
    private String streamToken;
    private int flaskPort;

   /* public CameraDto(UUID id, String name, String sectionName) {
        this.id = id;
        this.name = name;
        this.sectionName = sectionName;
    }*/

}
