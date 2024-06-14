package ar.edu.utn.dds.k3003.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
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
        viandaRepository.getEntityManager().getTransaction().begin();
    	Vianda vianda = new Vianda(viandaDTO.getCodigoQR(), viandaDTO.getFechaElaboracion(), EstadoViandaEnum.PREPARADA, viandaDTO.getColaboradorId(), viandaDTO.getHeladeraId());
    	vianda = this.viandaRepository.save(vianda);
    	viandaRepository.getEntityManager().getTransaction().commit();
        viandaRepository.getEntityManager().close();
    	return viandaMapper.map(vianda);
    }
    
    @Override
    public ViandaDTO modificarEstado(String qr, EstadoViandaEnum estado) {
        viandaRepository.getEntityManager().getTransaction().begin();
        Vianda vianda = viandaRepository.buscarPorQr(qr);
        if (vianda != null) {
            vianda.setEstado(estado);
            viandaRepository.save(vianda);
            entityManager.getTransaction().commit();
            entityManager.close();
            return viandaMapper.map(vianda);
        } else {
            entityManager.getTransaction().rollback();
            entityManager.close();
            throw new IllegalArgumentException("No se encontró la vianda");
        }
    }
    
	@Override
    public List<ViandaDTO> viandasDeColaborador(Long colaboradorId, Integer mes, Integer anio) {
        TypedQuery<Vianda> query = entityManager.createQuery(
                "SELECT v FROM Vianda v WHERE v.colaboradorId = :colaboradorId AND FUNCTION('MONTH', v.fechaElaboracion) = :mes AND FUNCTION('YEAR', v.fechaElaboracion) = :anio",
                Vianda.class
        );
        query.setParameter("colaboradorId", colaboradorId);
        query.setParameter("mes", mes);
        query.setParameter("anio", anio);
        List<Vianda> viandas = query.getResultList();
        entityManager.close();
        if (viandas.isEmpty()) {
            throw new NoSuchElementException("No se encontraron viandas");
        }
        List<ViandaDTO> viandasDeColaborador = new ArrayList<>();
        for (Vianda vianda : viandas) {
            viandasDeColaborador.add(viandaMapper.map(vianda));
        }
        return viandasDeColaborador;
    }
    
    @Override
    public ViandaDTO buscarXQR(String qr) {
    	Vianda vianda = viandaRepository.buscarPorQr(qr);
    	entityManager.close();
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
        entityManager.getTransaction().begin();
		Vianda vianda = viandaRepository.buscarPorQr(qrVianda);
		if (vianda == null) {
            entityManager.getTransaction().rollback();
            entityManager.close();
            throw new NoSuchElementException("No se encontró la vianda");
        }
        vianda.setHeladeraId(heladeraDestino);
        viandaRepository.save(vianda);
        entityManager.getTransaction().commit();
        entityManager.close();
        return viandaMapper.map(vianda);
	}
}