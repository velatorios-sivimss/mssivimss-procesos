package com.imss.sivimss.procesos.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComisionesBeans {
    private static Logger log = LoggerFactory.getLogger(ComisionesBeans.class);
    String query;

    public String cerrarComisiones(String proviene) {

        query = "UPDATE  SVT_COMISION_MENSUAL SET" +
                " IND_ACTIVO = coalesce(0, IND_ACTIVO), FEC_BAJA = CURDATE(), ID_USUARIO_BAJA = 0"   ;

        if (proviene.equals("cron")) {
            query = "UPDATE  SVT_COMISION_MENSUAL SET" +
                    " IND_ACTIVO = coalesce(0, IND_ACTIVO), FEC_BAJA = CURDATE(), ID_USUARIO_BAJA = 0"   ;
        }
        log.info(query);
        return query;

    }

}
