package com.imss.sivimss.procesos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imss.sivimss.procesos.model.request.TareasDTO;
import com.imss.sivimss.procesos.scheduler.ExternalScheduler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/lote")
public class LoteController {

  @Autowired
  ExternalScheduler externalScheduler;

  @PostMapping(path = "/orden-servicio", consumes = "application/json", produces = "application/json")
  public String scheduleATask(@RequestBody TareasDTO tareasDTO) {
    boolean result = externalScheduler.agregarTarea(tareasDTO);
    if (result) {
      return "Job successfully scheduled!";
    }
    return "There is already a job running for the specified name!";
  }
}