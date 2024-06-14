package ar.edi.itn.dds.k3003.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.FachadaViandas;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.tests.TestTP;
import java.time.LocalDateTime;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ViandasPersonalTest implements TestTP<FachadaViandas> {

	private static final Long COLABORADOR_ID = 56L;
	private static final Integer HELADERA_ID = 673;

	FachadaViandas instancia;
	ViandaDTO vianda;
	ViandaDTO viandaV;
	final LocalDateTime now = LocalDateTime.now();
	@Mock FachadaHeladeras fachadaHeladeras;

	@SneakyThrows
	@BeforeEach
	void setUp() {
		instancia = this.instance();
		instancia.setHeladerasProxy(fachadaHeladeras);
		vianda = new ViandaDTO("unQr", now, EstadoViandaEnum.PREPARADA, COLABORADOR_ID, HELADERA_ID);
		viandaV = new ViandaDTO("otroQr", now, EstadoViandaEnum.VENCIDA, 35L, 152);
  	}

	@Test
	@DisplayName("Agregar vianda a coleccion")
	void testAgregarViandaAColeccion() {
		var viandaAgregada = instancia.agregar(vianda);
		var viandaBuscada = instancia.buscarXQR(vianda.getCodigoQR());
	    assertEquals(
	        viandaAgregada.getId(),
	        viandaBuscada.getId(),
	        "No se agrego correctamente la vianda a la coleccion");
	}
	@Test
	@DisplayName("Cambiar estado de vianda")
	void testModificarEstadoVianda() {
		instancia.agregar(vianda);

	    instancia.modificarEstado(vianda.getCodigoQR(), EstadoViandaEnum.DEPOSITADA);

	    EstadoViandaEnum estado = instancia.buscarXQR(vianda.getCodigoQR()).getEstado();
	    assertEquals(
	        EstadoViandaEnum.DEPOSITADA,
	        estado,
	        "No se logro el cambio de estado correctamente");
	}
	@Test
	@DisplayName("Conseguir viandas del colaborador")
	void testViandasDeColaborador() {
		instancia.agregar(vianda);
	    assertEquals(
	        1,
	        instancia.viandasDeColaborador(COLABORADOR_ID, now.getMonthValue(), now.getYear()).size());

	    instancia.agregar(
	        new ViandaDTO("unQrMas", now, EstadoViandaEnum.RETIRADA, COLABORADOR_ID, HELADERA_ID));
	    assertEquals(
	        2,
	        instancia.viandasDeColaborador(COLABORADOR_ID, now.getMonthValue(), now.getYear()).size());
	}
	@Test
	@DisplayName("Buscar vianda de coleccion")
	void testBuscarXQR() {
		var viandaAgregada = instancia.agregar(vianda);
	    var viandaBuscada = instancia.buscarXQR(vianda.getCodigoQR());

	    assertEquals(
	        viandaAgregada.getColaboradorId(),
	        viandaBuscada.getColaboradorId(),
	        "Al buscarXQR no devuelve la vianda correcta.");
	}
	@Test
	@DisplayName("Evaluar si la vianda esta vencida")
	void testEvaluarVencimiento() {
		instancia.setHeladerasProxy(fachadaHeladeras);
	    ViandaDTO viandaNoVencida = instancia.agregar(vianda);
	    ViandaDTO viandaVencida = instancia.agregar(viandaV);

	    when(fachadaHeladeras.obtenerTemperaturas(HELADERA_ID))
	        .thenReturn(
	            List.of(
	                new TemperaturaDTO(5, HELADERA_ID, now),
	                new TemperaturaDTO(-10, HELADERA_ID, now)));

	    assertTrue(
	        instancia.evaluarVencimiento(viandaNoVencida.getCodigoQR()),
	        "La vianda no esta vencida pero retorna true");
	    
	    when(fachadaHeladeras.obtenerTemperaturas(152))
	        .thenReturn(
	            List.of(
	                new TemperaturaDTO(6, 152, now),
	                new TemperaturaDTO(-6, 152, now)));
	
	    assertFalse(
	        instancia.evaluarVencimiento(viandaVencida.getCodigoQR()),
	        "La vianda esta vencida pero retorna false");
	}

	@Override
	  public String paquete() {
	    return PAQUETE_BASE + "tests.viandas";
	  }

	@Override
	  public Class<FachadaViandas> clase() {
	    return FachadaViandas.class;
	  }
}
