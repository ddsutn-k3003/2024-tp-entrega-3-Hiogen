package ar.edu.utn.dds.k3003.controller;

import java.util.NoSuchElementException;

import org.json.JSONException;
import org.json.JSONObject;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class ViandaController{
	private final Fachada fachada;
	public ViandaController(Fachada fachada) {
		this.fachada = fachada;
	}
	
	public void agregar(Context context) {
		var viandaDTO = context.bodyAsClass(ViandaDTO.class);
		var viandaDTORta = fachada.agregar(viandaDTO);
		
		context.json(viandaDTORta);
		context.status(HttpStatus.CREATED);
	}
	
	public void obtenerXColIDAndAnioAndMes(Context context) {
		var colId = context.queryParamAsClass("colaboradorId", Long.class).get();
		var mes = context.queryParamAsClass("mes", Integer.class).get();
		var anio = context.queryParamAsClass("anio", Integer.class).get();
		var listaViandaDTO = fachada.viandasDeColaborador(colId, mes, anio);
		
	    context.json(listaViandaDTO);
	    context.status(HttpStatus.OK);
	}
	
	public void obtenerXQR(Context context) {
		var qr = context.pathParam("qr");
		var viandaDTO = fachada.buscarXQR(qr);
		
	    context.json(viandaDTO);
	    context.status(HttpStatus.OK);
	}
	
	public void evaluarVencimiento(Context context) {	
		var qr = context.pathParam("qr");
		try {
		      var valorVencimiento = fachada.evaluarVencimiento(qr);
		      context.json(valorVencimiento);
		      context.status(HttpStatus.OK);
		} catch (NoSuchElementException ex) {
		      context.result(ex.getLocalizedMessage());
		      context.status(HttpStatus.NOT_FOUND);
		}
	}
	
	public void modificarHeladeraXQR(Context context) {
		var qrVianda = context.pathParam("qrVianda");
		try {
			String body = context.body();
	        JSONObject jsonBody = new JSONObject(body);
	        int heladeraDestino = jsonBody.getInt("heladeraId");
			var viandaDTORta = fachada.modificarHeladera(qrVianda, heladeraDestino);
		    context.json(viandaDTORta);
		    context.status(HttpStatus.OK);
        } catch (NumberFormatException e) {
        	context.status(400).result("Formato incorrecto para heladeraId");
		} catch (NoSuchElementException e) {
		    context.result(e.getLocalizedMessage());
		    context.status(HttpStatus.NOT_FOUND);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
