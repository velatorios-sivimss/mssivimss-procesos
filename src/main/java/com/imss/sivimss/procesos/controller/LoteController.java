package com.imss.sivimss.procesos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imss.sivimss.procesos.model.request.TareasDTO;
import com.imss.sivimss.procesos.scheduler.ExternalScheduler;
import com.imss.sivimss.procesos.utils.Response;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/lote")
public class LoteController {

  @Autowired
  private ExternalScheduler externalScheduler;
  
  private Response<Object>response;
  
  @PostMapping(path = "/generico")
  public Response<Object> scheduleATask(@RequestBody TareasDTO tareasDTO) {
    boolean result = externalScheduler.agregarTarea(tareasDTO);
    
    if (result) {
    	response= new Response<>(false, 200, "Tarea generada");
    }
    response= new Response<>(true, 200, "La tarea ya tiene una ejecucion");
    return response;
  }
}