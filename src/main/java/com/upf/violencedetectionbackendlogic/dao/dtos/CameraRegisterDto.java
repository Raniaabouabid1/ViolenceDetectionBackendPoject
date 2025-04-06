package com.upf.violencedetectionbackendlogic.dao.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CameraRegisterDto {
    private String name;
    private String streamToken;
    private String ipAddress;
    private String streamUrl;
}
