package com.imss.sivimss.procesos.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class RestTemplateUtil {

	
	private final RestTemplate restTemplate;

	private static final String ERROR_ENVIAR = "Ha ocurrido un error al enviar";
	public RestTemplateUtil(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Env&iacute;a una petici&oacute;n con Body.
	 *
	 * @param url
	 * @param clazz
	 * @return
	 */
	public Response<Object> sendPostRequestByteArray(String url, EnviarDatosRequest body, Class<?> clazz)
			throws IOException {
		Response<Object> responseBody = new Response<>();
		HttpHeaders headers = RestTemplateUtil.createHttpHeaders();

		HttpEntity<Object> request = new HttpEntity<>(body, headers);
		ResponseEntity<?> responseEntity = null;
		try {
			responseEntity = restTemplate.postForEntity(url, request, clazz);
			if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
				// noinspection unchecked
				responseBody = (Response<Object>) responseEntity.getBody();
			} else {
				throw new IOException(ERROR_ENVIAR);
			}
		} catch (IOException ioException) {
			throw ioException;
		} catch (Exception e) {
			throw e;
		}

		return responseBody;
	}

	/**
	 * Env&iacute;a una petici&oacute;n con Body y token.
	 *
	 * @param url
	 * @param clazz
	 * @return
	 */
	public Response<Object> sendPostRequestByteArrayToken(String url, EnviarDatosRequest body, String subject,
			Class<?> clazz) {
		Response<Object> responseBody = new Response<>();
		HttpHeaders headers = RestTemplateUtil.createHttpHeadersToken(subject);

		HttpEntity<Object> request = new HttpEntity<>(body, headers);
		ResponseEntity<?> responseEntity = null;
		try {
			responseEntity = restTemplate.postForEntity(url, request, clazz);
			responseBody = (Response<Object>) responseEntity.getBody();

		} catch (Exception e) {
			responseBody.setError(true);
			responseBody.setMensaje(e.getMessage());
			throw e;
		}
		return  responseBody;
	}

	public Response<Object> sendPostRequestByteArray(String url, RecuperarStreamRequest body, Class<?> clazz)
			throws IOException {
		Response<?> responseBody = new Response<>();
		HttpHeaders headers = RestTemplateUtil.createHttpHeaders();

		HttpEntity<Object> request = new HttpEntity<>(body, headers);
		ResponseEntity<?> responseEntity = null;
		try {
			responseEntity = restTemplate.postForEntity(url, request, clazz);
			if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
				// noinspection unchecked
				responseBody = (Response<List<String>>) responseEntity.getBody();
			} else {
				throw new IOException("Ha ocurrido un error al guardar el archivo");
			}
		} catch (IOException ioException) {
			throw ioException;
		} catch (Exception e) {
			throw e;
		}

		return (Response<Object>) responseBody;
	}

	/**
	 * Env&iacute;a una petici&oacute;n de tipo POST a la url que se seleccione
	 *
	 * @param url
	 * @param clazz
	 * @return
	 */
	public Response<Object> sendGetRequest(String url) {
		Response<Object> response = new Response<>();
		ResponseEntity<?> responseEntity = null;
		try {
			responseEntity = restTemplate.getForEntity(url, String.class);
			if (responseEntity.getStatusCode() == HttpStatus.OK) {

				response = mapearRespuesta(responseEntity);

			} else {
				response.setCodigo(responseEntity.getStatusCodeValue());
				response.setError(true);
			}
		} catch (HttpClientErrorException e) {
			response.setError(true);
			response.setCodigo(e.getRawStatusCode());
		}
		return response;
	}

	private Response<Object> mapearRespuesta(ResponseEntity<?> responseEntity) {
		Response<Object> response = new Response<>();
		JsonNode json;
		ObjectMapper mapper = new ObjectMapper();
		Object object = responseEntity.getBody();
		if (object != null) {
			try {
				json = mapper.readTree(String.valueOf(object));
				response.setError(false);
				response.setCodigo(responseEntity.getStatusCodeValue());
				response.setMensaje(AppConstantes.EXITO);
				response.setDatos(json);
			} catch (Exception e) {
				response.setError(true);
				response.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
				response.setMensaje("186");
			}

		}
		return response;
	}

	/**
	 * Crea los headers para la petici&oacute;n  - falta agregar el tema de
	 * seguridad para las peticiones
	 *
	 * @return
	 */
	private static HttpHeaders createHttpHeaders() {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return header;
	}

	/**
	 * Crea los headers para la petici&oacute;n con token  - falta agregar el
	 * tema de seguridad para las peticiones
	 *
	 * @return
	 */
	private static HttpHeaders createHttpHeadersToken(String subject) {
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		header.set("Authorization", "Bearer " + subject);

		header.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return header;
	}

	///////////////////////////////////////////////////// peticion con archivos
	/**
	 * Crea los headers para la petici&oacute;n con token  - falta agregar el
	 * tema de seguridad para las peticiones
	 *
	 * @return
	 */
	/**
	 * Env&iacute;a una petici&oacute;n con Body, archivos y token.
	 *
	 * @param url
	 * @param clazz
	 * @return
	 */
	public Response<Object> sendPostRequestByteArrayArchviosToken(String url, EnviarDatosArchivosRequest body,
			String subject, Class<?> clazz) throws IOException {
		Response<?> responseBody = new Response<>();
		HttpHeaders headers = RestTemplateUtil.createHttpHeadersArchivosToken(subject);

		ResponseEntity<?> responseEntity = null;
		try {

			LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();

			for (MultipartFile file : body.getArchivos()) {
				if (!file.isEmpty()) {
					parts.add("files",
							new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
				}
			}

			parts.add("datos", body.getDatos());
			HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(parts, headers);

			responseEntity = restTemplate.postForEntity(url, request, clazz);
			if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
				// noinspection unchecked
				responseBody = (Response<List<String>>) responseEntity.getBody();
			} else {
				throw new IOException(ERROR_ENVIAR);
			}
		} catch (IOException ioException) {
			throw ioException;
		} catch (Exception e) {
			throw e;
		}

		return (Response<Object>) responseBody;
	}

	private static HttpHeaders createHttpHeadersArchivosToken(String subject) {

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.MULTIPART_FORM_DATA);
		header.setAccept(Arrays.asList(MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_JSON));
		header.set("Authorization", "Bearer " + subject);
		return header;
	}

	/**
	 * Envia correo.
	 *
	 * @param url
	 * @param CorreoRequest
	 * @param clazz
	 * @return
	 */
	public Response<Object> sendPostRequestCorreo(String url, String correo)
			throws IOException {
		Response<Object> responseBody = new Response<>();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
       
		ResponseEntity<?> responseEntity = null;
		
		try {

			HttpEntity<String>entity= new HttpEntity<>(correo ,headers);
			responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class );
			
			if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT ) {
				responseBody = new Response<>(false, HttpStatus.OK.value(), "Correo enviado correctamente", null);
			} else {
				throw new IOException(ERROR_ENVIAR);
			}
		} catch (IOException ioException) {
			throw ioException;
		} catch (Exception e) {
			throw e;
		}

		return responseBody;
	}
}
