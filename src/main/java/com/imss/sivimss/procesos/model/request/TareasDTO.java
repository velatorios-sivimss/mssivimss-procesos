package com.imss.sivimss.procesos.model.request;

import lombok.Data;

@Data
public class TareasDTO {

    private String tipoHoraMinuto;
    private String cveTarea;
    private Integer totalHoraMinuto;
    private String tipoEjecucion; 
    private String validacion;
}