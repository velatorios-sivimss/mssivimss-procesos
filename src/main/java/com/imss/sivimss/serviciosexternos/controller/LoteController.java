package com.imss.sivimss.serviciosexternos.controller;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
import com.imss.sivimss.serviciosexternos.model.request.TareasDTO;
import com.imss.sivimss.serviciosexternos.service.ExternalScheduler; 

 
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping("/lote")
public class LoteController {

	  @Autowired
    ExternalScheduler externalScheduler;
	
	 @PostMapping(path="/orden-servicio", consumes = "application/json", produces="application/json")
    public String scheduleATask(@RequestBody TareasDTO tareasDTO) {
      boolean result = externalScheduler.addJob(tareasDTO.getData());
        if (result) {
            return "Job successfully scheduled!";
        }
        return "There is already a job running for the specified name!";
    }
}