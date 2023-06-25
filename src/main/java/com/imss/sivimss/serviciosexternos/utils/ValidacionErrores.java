package com.imss.sivimss.serviciosexternos.utils;

import java.util.Date;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidacionErrores {

	private Map<String, String> errores;

	private String mensaje;

	private long codigo;

	private boolean error;

	private Date fecha;


	public ValidacionErrores(Map<String, String> errores, Date fecha) {
		super();
		this.errores = errores;
		this.fecha = fecha;
		this.mensaje = "Error en la petición";
		this.codigo = HttpStatus.BAD_REQUEST.value();
		this.error = true;
	}

}
