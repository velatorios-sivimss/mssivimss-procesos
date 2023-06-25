package com.imss.sivimss.serviciosexternos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

@Service
public class ExternalScheduler implements SchedulingConfigurer {
    private static Logger log = LoggerFactory.getLogger(ExternalScheduler.class);
    ScheduledTaskRegistrar scheduledTaskRegistrar;

    Map<String, ScheduledFuture> futureMap = new HashMap<>();

    @Bean
    public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }

    // Initially scheduler has no job
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        if (scheduledTaskRegistrar == null) {
            scheduledTaskRegistrar = taskRegistrar;
        }
        if (taskRegistrar.getScheduler() == null) {
            taskRegistrar.setScheduler(poolScheduler());
        }
    }

    public boolean addJob(String jobName) {
        if (futureMap.containsKey(jobName)) {
            return false;
        }

        ScheduledFuture future = scheduledTaskRegistrar.getScheduler().schedule(() -> methodToBeExecuted(jobName),
                t -> {
                    Calendar nextExecutionTime = new GregorianCalendar();
                    Date lastActualExecutionTime = t.lastActualExecutionTime();
                    log.info("fecha" + lastActualExecutionTime);
                    Calendar salida = new GregorianCalendar();
                    salida.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                    salida.add(Calendar.HOUR, 4);

                    log.info("fecha" + salida.toString());
                    nextExecutionTime.setTime(lastActualExecutionTime != null ? lastActualExecutionTime : new Date());
                    nextExecutionTime.add(Calendar.HOUR, 4);
                    return nextExecutionTime.getTime();
                });

        configureTasks(scheduledTaskRegistrar);
        futureMap.put(jobName, future);
        log.info("jobName" + futureMap.toString());
        log.info("incia el contador");

        return true;
    }


    

    public boolean removeJob(String name) {
        if (!futureMap.containsKey(name)) {
            return false;
        }
        ScheduledFuture future = futureMap.get(name);
        future.cancel(true);
        futureMap.remove(name);
        return true;
    }

    public void methodToBeExecuted(String jobName) {
        log.info("methodToBeExecuted: Next execution time of this will always be 5 seconds " + jobName);

        Boolean cancelado = removeJob(jobName);

        if (cancelado) {
            log.info("ya fue cancelado" + cancelado);
        } else {
            log.info("ya no se cancelo" + cancelado);
        }

    }
}