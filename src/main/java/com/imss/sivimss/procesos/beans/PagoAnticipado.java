package com.imss.sivimss.procesos.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imss.sivimss.procesos.utils.QueryHelper;
import com.imss.sivimss.procesos.utils.SelectQueryUtil;

public class PagoAnticipado {

	private String query;
	
	private static final Logger log = LoggerFactory.getLogger(PagoAnticipado.class);

	private String consultaPlanesPa() {
		SelectQueryUtil selectQueryUtil= new SelectQueryUtil();
		
		selectQueryUtil.select(
				" DISTINCT "
				+"SPA.ID_PLAN_SFPA ")
		.from("SVT_PLAN_SFPA SPA")
		.innerJoin("SVT_PAGO_SFPA SFPA", "SPA.ID_PLAN_SFPA = SFPA.ID_PLAN_SFPA")
		.where("SPA.ID_ESTATUS_PLAN_SFPA in (1,2,3)");
		return selectQueryUtil.build();
	}
	public String cambiarEstatusConAdeudo() {
		QueryHelper queryHelper= new QueryHelper("UPDATE SVT_PAGO_SFPA");
		
		
		queryHelper.addColumn("ID_ESTATUS_PAGO", "2");
		queryHelper.addWhere("FEC_PARCIALIDAD < CURRENT_DATE() AND ID_ESTATUS_PAGO = 8 AND ID_PLAN_SFPA in (".concat(this.consultaPlanesPa().concat(")")));
		query=queryHelper.obtenerQueryActualizar();
		log.info(query);
		return query;
	}
	
	public String cambiarEstatusPorPagar() {
		QueryHelper queryHelper= new QueryHelper("UPDATE SVT_PAGO_SFPA");
		queryHelper.addColumn("ID_ESTATUS_PAGO", "8");
		queryHelper.addWhere("FEC_PARCIALIDAD = CURRENT_DATE() AND ID_ESTATUS_PAGO = 7 AND ID_PLAN_SFPA in (".concat(this.consultaPlanesPa().concat(")")));
		query=queryHelper.obtenerQueryActualizar();
		log.info(query);
		return query;
	}
	
	public String cambiarEstatusPlanPa() {
		QueryHelper queryHelper= new QueryHelper("UPDATE SVT_PLAN_SFPA");
		SelectQueryUtil selectQueryUtil= new SelectQueryUtil();

		selectQueryUtil.select("case"
				+ "when count(SFPA.ID_ESTATUS_PAGO)>= 2 "
				+"then "
				+ "SPA.ID_PLAN_SFPA "
				+ "end ")
		.from("SVT_PLAN_SFPA SPA")
		.innerJoin("SVT_PAGO_SFPA SFPA ", "SPA.ID_PLAN_SFPA = SFPA.ID_PLAN_SFPA")
		.where("SPA.ID_ESTATUS_PLAN_SFPA in (1, 2) "
				+ "AND SFPA.ID_ESTATUS_PAGO = 2 "
				+ "GROUP BY "
				+ "SPA.ID_PLAN_SFPA ");
		
		queryHelper.addColumn("ID_ESTATUS_PLAN_SFPA ", "3");
		queryHelper.addWhere("ID_PLAN_SFPA in ("+selectQueryUtil.build()+")");
		log.info(query);
		return query;
	}
	
	
	
}
