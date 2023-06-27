package com.imss.sivimss.procesos.utils;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

@Service
public class ConnectionUtil {
   @Value("${spring.datasource.driverClassName}")
   private String driver;
   @Value("${spring.datasource.url}")
   private String url;

   @Value("${spring.datasource.username}")
   private String usuario;

   @Value("${spring.datasource.password}")
   private String contrasenia;

   private static final Logger log = LoggerFactory.getLogger(Connection.class);

   public Connection getConnection() {
      try {
         DriverManagerDataSource dataSource = new DriverManagerDataSource();
         dataSource.setDriverClassName(driver);
         dataSource.setUrl(url);
         dataSource.setUsername(usuario);
         dataSource.setPassword(contrasenia);
         return dataSource.getConnection();
      } catch (SQLException e) {
         log.info(e.getMessage());
      }
      return null;
   }

}
