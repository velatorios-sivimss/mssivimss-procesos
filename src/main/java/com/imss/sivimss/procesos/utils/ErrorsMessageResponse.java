package com.imss.sivimss.procesos.utils;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@JsonPropertyOrder({ "error", "codigo", "mensaje", "datos" })
public class ErrorsMessageResponse {

	private Date fecha;

	private String mensaje;

	private String datos;

	private long codigo;

	private boolean error;

	public ErrorsMessageResponse(Date timestamp, long codigo, String mensaje, String detalles) {
		super();
		this.fecha = timestamp;
		this.error = true;
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.datos = detalles;

	}

}
