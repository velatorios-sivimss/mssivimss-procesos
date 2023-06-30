package com.imss.sivimss.procesos.scheduler;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.imss.sivimss.procesos.model.request.ODSDTO;
import com.imss.sivimss.procesos.model.request.TareasDTO;
import com.imss.sivimss.procesos.service.Tareas;
import com.imss.sivimss.procesos.utils.ConnectionUtil;
import com.imss.sivimss.procesos.utils.LogUtil;
import java.util.logging.Level;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
public class ExternalScheduler implements SchedulingConfigurer {
    private static Logger log = LoggerFactory.getLogger(ExternalScheduler.class);
    @Value("${numero-por-grupo}")
    private Integer numeroPorGrupo;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String usuario;

    @Value("${spring.datasource.password}")
    private String contrasenia;

    ScheduledTaskRegistrar tareasProgramadas;

    @Autowired
    ConnectionUtil connection;

    @Autowired
    Tareas tareas;

    Map<String, ScheduledFuture> mapaProgramado = new HashMap<>();

    private LogUtil logUtil;

    @Bean
    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler programarPool = new ThreadPoolTaskScheduler();
        programarPool.setThreadNamePrefix("ThreadPoolTaskScheduler");
        programarPool.setPoolSize(numeroPorGrupo);
        programarPool.initialize();
        return programarPool;
    }

    // validar si existe o no una tarea con el identificador
    @Override
    public void configureTasks(ScheduledTaskRegistrar registrarTarea) {
        if (tareasProgramadas == null) {
            tareasProgramadas = registrarTarea;
        }
        if (registrarTarea.getScheduler() == null) {
            registrarTarea.setScheduler(poolScheduler());
        }
    }

    public boolean agregarTarea(TareasDTO tareasDTO) {
        String cveTarea = tareasDTO.getCveTarea();

        if (mapaProgramado.containsKey(cveTarea)) {
            if (tareasDTO.getValidacion().equals("INSERT")) {
                return false;
            } else if (tareasDTO.getValidacion().equals("CANCELAR")) {
                eliminarTarea(cveTarea);
                return true;
            } else {
                // si es una actualizacion se elimina la tarea y se regenera
                Boolean tareaEliminada = eliminarTarea(cveTarea);

                if (Boolean.TRUE.equals(tareaEliminada)) {
                    log.info("Tarea eliminada correctamente");
                } else {
                    log.info("No se pudo cancela la tarea anterior");
                    return false;
                }
            }

        }
        try {
            ScheduledFuture future = tareasProgramadas.getScheduler().schedule(() -> ejecutarTarea(tareasDTO),
                    t -> {
                        Calendar nextExecutionTime = new GregorianCalendar();
                        Integer totalHoraMinuto = tareasDTO.getTotalHoraMinuto();
                        Date lastActualExecutionTime = t.lastActualExecutionTime();
                        nextExecutionTime
                                .setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                        if (tareasDTO.getTipoHoraMinuto().equals("HORA")) {
                            nextExecutionTime.add(Calendar.HOUR, totalHoraMinuto);
                        } else if (tareasDTO.getTipoHoraMinuto().equals("MINUTOS")) {
                            nextExecutionTime.add(Calendar.MINUTE, totalHoraMinuto);
                        } else {
                            nextExecutionTime.add(Calendar.SECOND, totalHoraMinuto);
                        }

                        return nextExecutionTime.getTime();
                    });

            configureTasks(tareasProgramadas);
            mapaProgramado.put(cveTarea, future);

            return true;
        } catch (Exception e) {
            // TODO: handle exception
            log.info(e.getMessage());
            try {
                logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),
                        this.getClass().getPackage().toString(), "SQLException" + e.getMessage(), null);
            } catch (IOException e1) {
                log.error("IOException {}", e1.getMessage());

            }
            return false;
        }

    }

    public boolean eliminarTarea(String cveTarea) {
        if (!mapaProgramado.containsKey(cveTarea)) {
            return false;
        }

        try {
            ScheduledFuture future = mapaProgramado.get(cveTarea);
            future.cancel(true);
            mapaProgramado.remove(cveTarea);
            log.info("Tarea cancelada correctamente ");

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }

    }

    private void ejecutarTarea(TareasDTO tareasDTO) {

        try {
            log.info("Incicando Tarea", tareasDTO.getCveTarea());

            String datos = tareasDTO.getDatos().toString();

            String validarEjecucion = validarEjecucion(tareasDTO.getTipoEjecucion(), datos);
            if (validarEjecucion.equals("ok")) {
                Boolean cancelado = eliminarTarea(tareasDTO.getCveTarea());

                if (Boolean.TRUE.equals(cancelado)) {
                    log.info("Tarea finalizada correctamente");
                } else {
                    log.info("No se pudo cancelar la tarea");
                }
            } else {
                Boolean cancelado = eliminarTarea(tareasDTO.getCveTarea());

                if (Boolean.TRUE.equals(cancelado)) {
                    log.info("Tarea finalizada correctamente");
                } else {
                    log.info("No se pudo cancelar la tarea");
                }
            }
        } catch (Exception e) {
            log.error("Exception {}", e.getMessage());
            try {
                logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),
                        this.getClass().getPackage().toString(), "SQLException" + e.getMessage(), null);
            } catch (IOException e1) {
                log.error("IOException {}", e1.getMessage());
            }

        }

    }

    private String validarEjecucion(String tipoEjecucion, String datos) throws IOException {
        String salida = "";
        switch (tipoEjecucion) {
            case "ODS":
                try {
                    Gson gson = new Gson();
                    ODSDTO valor = gson.fromJson(datos, ODSDTO.class);
                    salida = tareas.tareaODS(Integer.toString(valor.getIdODS()));
                } catch (SQLException e) {
                    salida = "error";
                    log.error("SQLException {}", e.getMessage());
                    logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),
                            this.getClass().getPackage().toString(), "SQLException" + e.getMessage(), null);

                } catch (Exception ex) {
                    salida = "error";
                    log.error("Exception {}", ex.getMessage());
                    logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),
                            this.getClass().getPackage().toString(), "Exception" + ex.getMessage(), null);

                }

                break;
            case "otro":
                salida = "otro";
                break;
            default:
                salida = "noexiste";
                break;
        }
        return salida;
    }
}