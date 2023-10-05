package com.imss.sivimss.procesos.service.impl;

import java.sql.Statement;
import java.util.logging.Level;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imss.sivimss.procesos.utils.ConnectionUtil;
import com.imss.sivimss.procesos.utils.LogUtil;
import com.imss.sivimss.procesos.beans.CajaBeans;
import com.imss.sivimss.procesos.beans.OrdenServicio;
import com.imss.sivimss.procesos.beans.ComisionesBeans;
import com.imss.sivimss.procesos.service.Tareas;

@Service
public class TareasImplements implements Tareas {

    private static Logger log = LoggerFactory.getLogger(TareasImplements.class);

    @Autowired
    ConnectionUtil jdbcConnection;

    private Connection connection;
    private Statement statement;
    private ResultSet resultadoBusqueda;
    private LogUtil logUtil;

    public String tareaODS(String idODS) throws SQLException {
        connection = jdbcConnection.getConnection();
        String salida = "eror";
        try {  

            OrdenServicio ods = new OrdenServicio();
            String buscarODSPREORDEN = ods.buscarODSPREORDEN(idODS);

            statement = connection.createStatement();
            resultadoBusqueda = statement.executeQuery(buscarODSPREORDEN);
            String contador = "0";
            if (resultadoBusqueda.next()) {
                contador = resultadoBusqueda.getString("total");
            }

            if (Integer.parseInt(contador) == 0) {
                resultadoBusqueda.close();
                statement.close();
                connection.close();

                return "cancelar";
            }

            String actualizarCaracteristicasPaqueteTem = ods.actualizarCaracteristicasPaqueteTemporal(idODS);
            String actualizarCaracteristicasPaqueteDetalleTemp = ods.actualizarCaracteristicasPaqueteDetalleTemp(idODS);
            String actualizarCaracteristicasPresupuestoTemporal = ods
                    .actualizarCaracteristicasPresupuestoTemporal(idODS);
            String actualizarCaracteristicasPresuestoDetalleTemp = ods
                    .actualizarCaracteristicasPresuestoDetalleTemp(idODS);
            String actualizarDonacionTemporal = ods.actualizarDonacionTemporal(idODS);
            String actualizarSalidaDonacionTemporal = ods.actualizarSalidaDonacionTemporal(idODS);

            statement.executeUpdate(actualizarCaracteristicasPaqueteTem);
            statement.executeUpdate(actualizarCaracteristicasPaqueteDetalleTemp);
            statement.executeUpdate(actualizarCaracteristicasPresupuestoTemporal);
            statement.executeUpdate(actualizarCaracteristicasPresuestoDetalleTemp);
            statement.executeUpdate(actualizarDonacionTemporal);
            statement.executeUpdate(actualizarSalidaDonacionTemporal);
            resultadoBusqueda.close();
            statement.close();
            connection.close();
            salida = "ok";

        } catch (Exception e) {
            salida = "error";
            resultadoBusqueda.close();
            statement.close();
            connection.close();
            log.error("Exception {}", e.getMessage());

            try {
                logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),
                        this.getClass().getPackage().toString(), "SQLException" + e.getMessage(), null);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                log.error("Exception {}", e1.getMessage());
            }
        }

        return salida;

    }

    public void cierrCaja(String proviene) throws SQLException {

        try {

            CajaBeans cajaBeans = new CajaBeans();
            connection = jdbcConnection.getConnection();
            statement = connection.createStatement();
            String sql = cajaBeans.cerrarCaja(proviene);
            statement.executeUpdate(sql);
            statement.close();
            connection.close();
        } catch (SQLException e) {

            statement.close();
            connection.close();
            log.error("Exception {}", e.getMessage());

            try {
                logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),
                        this.getClass().getPackage().toString(), "SQLException" + e.getMessage(), null);
            } catch (IOException e1) {
                log.error("Exception {}", e1.getMessage());
            }
        }
    }

    public void montoComision(String proviene) throws SQLException {

        try {
        	log.info("Ejecutando Cierre de comisiones...");
        	ComisionesBeans cajaBeans = new ComisionesBeans();
            connection = jdbcConnection.getConnection();
            statement = connection.createStatement();
            String sql = cajaBeans.cerrarComisiones(proviene);
            statement.executeUpdate(sql);
            statement.close();
            connection.close();
        } catch (SQLException e) {

            statement.close();
            connection.close();
            log.error("Exception {}", e.getMessage());

            try {
                logUtil.crearArchivoLog(Level.WARNING.toString(), this.getClass().getSimpleName(),
                        this.getClass().getPackage().toString(), "SQLException" + e.getMessage(), null);
            } catch (IOException e1) {
                log.error("Exception {}", e1.getMessage());
            }
        }
    }
}
