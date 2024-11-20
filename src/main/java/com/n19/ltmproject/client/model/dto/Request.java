package com.n19.ltmproject.client.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class Request {

    private String action;
    private Map<String, Object> params;
}
