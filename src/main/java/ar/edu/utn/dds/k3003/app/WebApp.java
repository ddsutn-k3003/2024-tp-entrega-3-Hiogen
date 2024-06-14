package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.controller.*;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import javax.persistence.EntityManagerFactory;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.Persistence;

public class WebApp {
	
	public static EntityManagerFactory entityManagerFactory;
	
	public static void main(String[] args) {
		
		startEntityManagerFactory();
		
        var env = System.getenv();
		var port = Integer.parseInt(env.getOrDefault("PORT", "8080"));
		var app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                configureObjectMapper(mapper);
            }));
        }).start(port);
		ObjectMapper objectMapper = createObjectMapper();
		Fachada fachada = new Fachada();
		var viandaController = new ViandaController(fachada);
		fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));
		
		app.get("/", ctx -> ctx.result("Vianda"));
		app.post("/viandas", ctx -> viandaController.agregar(ctx));
		app.get("/viandas/search/findByColaboradorIdAndAnioAndMes", ctx -> viandaController.obtenerXColIDAndAnioAndMes(ctx));
		app.get("/viandas/{qr}", ctx -> viandaController.obtenerXQR(ctx));
		app.get("/viandas/{qr}/vencida", ctx -> viandaController.evaluarVencimiento(ctx));
		app.patch("/viandas/{qrVianda}", ctx -> viandaController.modificarHeladeraXQR(ctx));
	
	}

	private static ObjectMapper createObjectMapper() {
		var objectMapper = new ObjectMapper();
	    objectMapper.registerModule(new JavaTimeModule());
	    objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
	    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	    objectMapper.setDateFormat(sdf);
	    return objectMapper;
	}
	public static void configureObjectMapper(ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        var sdf = new SimpleDateFormat(Constants.DEFAULT_SERIALIZATION_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(sdf);
    }

    public static void startEntityManagerFactory() {
        Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        String[] keys = new String[] { "javax.persistence.jdbc.url", "javax.persistence.jdbc.user",
                "javax.persistence.jdbc.password", "javax.persistence.jdbc.driver"};
        for (String key : keys) {
            if (env.containsKey(key)) {
                String value = env.get(key);
                configOverrides.put(key, value);
            }
        }
        entityManagerFactory = Persistence.createEntityManagerFactory("viandas", configOverrides);
    }
}
