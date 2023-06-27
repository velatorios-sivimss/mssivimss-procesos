package com.imss.sivimss.procesos.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.procesos.utils.QueryHelper;
import com.imss.sivimss.procesos.utils.SelectQueryUtil;

public class OrdenServicio {
    private static Logger log = LoggerFactory.getLogger(OrdenServicio.class);
    String query;

    public String buscarODSPREORDEN(String idODS){

        SelectQueryUtil selectQueryUtil= new SelectQueryUtil();
        selectQueryUtil.select(" COUNT(ID_ORDEN_SERVICIO)  as total")
		.from("SVC_ORDEN_SERVICIO ")
        .where("ID_ESTATUS_ORDEN_SERVICIO  = :idEstatus")
        .setParameter("idEstatus", 1)
        .and(" ID_ORDEN_SERVICIO = "+ idODS);
        
        query= selectQueryUtil.build();
         log.info(query);
        return query;

    }

    public String actualizarCaracteristicasPaqueteTemporal(String idOrden) {
        final QueryHelper queryHelper = new QueryHelper("UPDATE SVC_CARACTERISTICAS_PAQUETE_TEMP ");
        queryHelper.agregarParametroValues("IND_ACTIVO", "0");
        queryHelper.addWhere(" ID_ORDEN_SERVICIO = " + idOrden);

        query = queryHelper.obtenerQueryActualizar();
        log.info(query);
        return query;
    }


      public String actualizarCaracteristicasPaqueteDetalleTemp(String idOrden) {

          query = " UPDATE SVC_DETALLE_CARACTERISTICAS_PAQUETE_TEMP SET IND_ACTIVO = 0"
                + " WHERE ID_CARACTERISTICAS_PAQUETE IN "
                + " (SELECT DISTINCT ID_CARACTERISTICAS_PAQUETE "
                + " FROM SVC_CARACTERISTICAS_PAQUETE_TEMP"
                + " WHERE ID_ORDEN_SERVICIO ="+idOrden+")";
        log.info(query);
        return query;

    }


     public String actualizarCaracteristicasPresupuestoTemporal(String idOrden) {
        final QueryHelper queryHelper = new QueryHelper("UPDATE CARACTERISTICAS_PRESUPUESTO_TEMP ");
        queryHelper.agregarParametroValues("IND_ACTIVO", "0");
        queryHelper.addWhere(" ID_ORDEN_SERVICIO = " + idOrden);

        query = queryHelper.obtenerQueryActualizar();
        log.info(query);
        return query;
    }


         public String actualizarCaracteristicasPresuestoDetalleTemp(String idOrden) {

          query = " UPDATE SVC_DETALLE_CARACTERISTICAS_PRESUPUESTO_TEMP SET IND_ACTIVO = 0"
                + " WHERE ID_CARACTERISTICAS_PRESUPUESTO IN "
                + " (SELECT DISTINCT ID_CARACTERISTICAS_PRESUPUESTO "
                + " FROM SVC_CARACTERISTICAS_PRESUPUESTO_TEMP"
                + " WHERE ID_ORDEN_SERVICIO ="+idOrden+")";
        log.info(query);
        return query;

    }





}
