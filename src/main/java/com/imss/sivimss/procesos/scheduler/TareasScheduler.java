package com.imss.sivimss.procesos.scheduler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.imss.sivimss.procesos.beans.CajaBeans;
import com.imss.sivimss.procesos.service.Tareas;
import com.imss.sivimss.procesos.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TareasScheduler {

    private static Logger log = LoggerFactory.getLogger(CajaBeans.class);

    @Autowired
    Tareas tareas;

    // se ejecuta la tarea solo una vez si se reinicia el pod
    @Scheduled(initialDelay = 1000, fixedDelay = Long.MAX_VALUE)
    public void execute() {
        try {
            tareas.cierrCaja("soloUnaVez");
        } catch (SQLException e) {
            log.error("Exception {}", e.getMessage());
        }

    }

    // se ejecuta la tarea a las 23:58 todos los dias
    @Scheduled(cron = "0 58 23 ? * *")
    public void cierreCajaDiario() {
        try {
            tareas.cierrCaja("cron");
        } catch (SQLException e) {
            log.error("Exception {}", e.getMessage());
        }

    }

}