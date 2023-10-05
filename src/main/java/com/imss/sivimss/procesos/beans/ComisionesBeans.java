package com.imss.sivimss.procesos.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComisionesBeans {
    private static Logger log = LoggerFactory.getLogger(ComisionesBeans.class);
    String query;

    public String cerrarComisiones() {
            query = "UPDATE  SVT_COMISION_MENSUAL SET"
                    + " IND_ACTIVO = coalesce(0, IND_ACTIVO), FEC_BAJA = CURDATE(), ID_USUARIO_BAJA = 0"
            		+ " WHERE NUM_MES_COMISION = DATE_FORMAT(CURDATE(),'%m') - 1 AND NUM_ANIO_COMISION = DATE_FORMAT(CURDATE(),'%Y')";
        log.info(query);
        return query;

    }

}
