package com.imss.sivimss.procesos.service;

import java.sql.SQLException;

public interface Tareas {
   String tareaODS(String idODS) throws SQLException;

   void cierrCaja(String proviene) throws SQLException;

   void montoComision() throws SQLException;

}
