package com.imss.sivimss.procesos.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CajaBeans {
    private static Logger log = LoggerFactory.getLogger(CajaBeans.class);
    String query;

    public String cerrarCaja(String proviene) {

        query = "UPDATE  SVT_PAGO_DETALLE SET" +
                " IND_ESTATUS_CAJA = coalesce(0, IND_ESTATUS_CAJA)," +
                " ID_USUARIO_MODIFICA = coalesce(1, ID_USUARIO_MODIFICA)," +
                " FEC_CIERRE_CAJA = coalesce(CURRENT_TIMESTAMP(), FEC_CIERRE_CAJA)," +
                " FEC_ACTUALIZACION = coalesce(CURRENT_TIMESTAMP(), FEC_ACTUALIZACION)" +
                " WHERE DATE_FORMAT(FEC_PAGO,'%d-%m-%Y') < DATE_FORMAT(CURDATE(), '%d-%m-%Y')";

        if (proviene.equals("cron")) {
            query = " UPDATE SVT_PAGO_DETALLE SET" +
                    " IND_ESTATUS_CAJA = coalesce(0, IND_ESTATUS_CAJA)," +
                    " ID_USUARIO_MODIFICA = coalesce(1, ID_USUARIO_MODIFICA)," +
                    " FEC_CIERRE_CAJA = coalesce(CURRENT_TIMESTAMP(), FEC_CIERRE_CAJA)," +
                    " FEC_ACTUALIZACION = coalesce(CURRENT_TIMESTAMP(), FEC_ACTUALIZACION)" +
                    " WHERE DATE_FORMAT(FEC_PAGO,'%d-%m-%Y') = DATE_FORMAT(CURDATE(), '%d-%m-%Y')";
        }
        log.info(query);
        return query;

    }

}
