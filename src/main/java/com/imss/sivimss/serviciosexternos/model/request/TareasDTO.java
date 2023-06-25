package com.imss.sivimss.serviciosexternos.model.request;

import lombok.Data;

@Data
public class TareasDTO {

    private String cronExpression;
    private String actionType;
    private String data;
}