package com.upf.violencedetectionbackendlogic.dao.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SectionDTO {
    public String name;
    public String coordinates;
    public List<UUID> userIds;
    public List<UUID> cameraIds;
}

