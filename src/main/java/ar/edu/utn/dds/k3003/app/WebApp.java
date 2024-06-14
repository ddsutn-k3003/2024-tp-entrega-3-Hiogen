package ar.edu.utn.dds.k3003.app;

import ar.edu.utn.dds.k3003.clients.HeladerasProxy;
import ar.edu.utn.dds.k3003.controller.*;
import ar.edu.utn.dds.k3003.facades.dtos.Constants;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class WebApp {
	public static void main(String[] args) {
		
		var fachada = new Fachada();
	    var objectMapper = createObjectMapper();
	    Integer port = Integer.parseInt(System.getProperty("port","8080"));
	    Javalin app = Javalin.create().start(port);
	    var viandaController = new ViandaController(fachada);
	    fachada.setHeladerasProxy(new HeladerasProxy(objectMapper));
		
		app.post("/viandas", viandaController::agregar);
		app.get("/viandas/search/findByColaboradorIdAndAnioAndMes", viandaController::obtenerXColIDAndAnioAndMes);
		app.get("/viandas/{qr}", viandaController::obtenerXQR);
		app.get("/viandas/{qr}/vencida", viandaController::evaluarVencimiento);
		app.patch("/viandas/{qrVianda}", viandaController::modificarHeladeraXQR);
	
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
	/*public static void startEntityManagerFactory() {
        Map<String, String> env = System.getenv();
        Map<String, Object> configOverrides = new HashMap<String, Object>();
        String[] keys = new String[] { "javax.persistence.jdbc.url", "javax.persistence.jdbc.user",
                "javax.persistence.jdbc.password", "javax.persistence.jdbc.driver", "hibernate.hbm2ddl.auto",
                "hibernate.connection.pool_size", "hibernate.show_sql" };
        for (String key : keys) {
            if (env.containsKey(key)) {
                String value = env.get(key);
                configOverrides.put(key, value);
            }
        }
        entityManagerFactory = Persistence.createEntityManagerFactory("viandas", configOverrides);
<<<<<<< HEAD
    }*/
}
