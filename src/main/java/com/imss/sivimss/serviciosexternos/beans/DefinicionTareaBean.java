package com.imss.sivimss.serviciosexternos.beans;

import org.springframework.stereotype.Service;

import com.imss.sivimss.serviciosexternos.model.request.TareasDTO;

@Service
public class DefinicionTareaBean    implements Runnable  {
    private TareasDTO taskDefinition;
    @Override
    public void run() {
        System.out.println("Running action: " + taskDefinition.getActionType());
        System.out.println("With Data: " + taskDefinition.getData());
    }

    public TareasDTO getTaskDefinition() {
        return taskDefinition;
    }

    public static void setTaskDefinition(TareasDTO taskDefinition) {
        taskDefinition = taskDefinition;
    }
}
