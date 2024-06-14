package ar.edu.utn.dds.k3003.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import java.time.LocalDateTime;
import java.util.*;

import ar.edu.utn.dds.k3003.facades.FachadaHeladeras;
import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.facades.dtos.TemperaturaDTO;
import ar.edu.utn.dds.k3003.facades.dtos.ViandaDTO;
import ar.edu.utn.dds.k3003.model.Vianda;
import ar.edu.utn.dds.k3003.repositories.ViandaMapper;
import ar.edu.utn.dds.k3003.repositories.ViandaRepository;
import javax.persistence.Persistence;

public class Fachada implements ar.edu.utn.dds.k3003.facades.FachadaViandas {
	
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private final ViandaRepository viandaRepository;
    private final ViandaMapper viandaMapper;
    private FachadaHeladeras fachadaHeladeras;
    //private FachadaColaboradores fachadaColaborador;
   
    public Fachada() {
    	this.entityManagerFactory = Persistence.createEntityManagerFactory("postgres");
        this.entityManager = entityManagerFactory.createEntityManager();
        this.viandaRepository = new ViandaRepository(entityManager);
        this.viandaMapper = new ViandaMapper();
    }
    
    @Override
    public ViandaDTO agregar(ViandaDTO viandaDTO) {
    	Vianda vianda = new Vianda(viandaDTO.getCodigoQR(), viandaDTO.getFechaElaboracion(), EstadoViandaEnum.PREPARADA, viandaDTO.getColaboradorId(), viandaDTO.getHeladeraId());
    	vianda = this.viandaRepository.save(vianda);
    	return viandaMapper.map(vianda);
    }
    
    @Override
    public ViandaDTO modificarEstado(String qr, EstadoViandaEnum estado) {
        Vianda vianda = viandaRepository.buscarPorQr(qr);
        if (vianda != null) {
            vianda.setEstado(estado);
            viandaRepository.save(vianda);
            return viandaMapper.map(vianda);
        } else {
            throw new IllegalArgumentException("No se encontró la vianda");
        }
    }
    
	@Override
    public List<ViandaDTO> viandasDeColaborador(Long colaboradorId, Integer mes, Integer anio) {
		List<ViandaDTO> viandasDeColaborador = new ArrayList<>();

        for (Vianda vianda : this.viandaRepository.getViandas()) {
            LocalDateTime fechaVianda = vianda.getFechaElaboracion();
            if (vianda.getColaboradorId().equals(colaboradorId) &&
                    fechaVianda.getMonthValue() == mes &&
                    fechaVianda.getYear() == anio) {
                viandasDeColaborador.add(viandaMapper.map(vianda));
            }
        }

        if (viandasDeColaborador.isEmpty()) {
            throw new NoSuchElementException("No se encontraron viandas");
        }

        return viandasDeColaborador;
    }
    
    @Override
    public ViandaDTO buscarXQR(String qr) {
    	Vianda vianda = viandaRepository.buscarPorQr(qr);
    	if (vianda != null) {
            return viandaMapper.map(vianda);
        } else {
            return null;
        }
    }
    
    @Override
    public boolean evaluarVencimiento(String qr) {
    	ViandaDTO vianda = this.buscarXQR(qr);
    	Optional<TemperaturaDTO> temperaturasDTO = fachadaHeladeras.obtenerTemperaturas(vianda.getHeladeraId()).stream().filter(x -> x.getTemperatura() > 5).findAny();	
    	if(temperaturasDTO.isEmpty()) {
    		return true;
    	} else {
    		return false;
    	}
    }

	@Override
	public void setHeladerasProxy(FachadaHeladeras fachadaHeladerasInstancia) {
		this.fachadaHeladeras = fachadaHeladerasInstancia;	
	}

	@Override
	public ViandaDTO modificarHeladera(String qrVianda, int heladeraDestino) {
		Vianda vianda = viandaRepository.buscarPorQr(qrVianda);
		if (vianda == null) {
            throw new NoSuchElementException("No se encontró la vianda");
        }
        vianda.setHeladeraId(heladeraDestino);
        return viandaMapper.map(vianda);
	}
}