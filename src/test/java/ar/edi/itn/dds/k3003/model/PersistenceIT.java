package ar.edi.itn.dds.k3003.model;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;
import ar.edu.utn.dds.k3003.model.Vianda;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PersistenceIT {
	
    static EntityManagerFactory entityManagerFactory ;
    EntityManager entityManager ;

    @BeforeAll
    public static void setUpClass() throws Exception {
        entityManagerFactory = Persistence.createEntityManagerFactory("pruebadb");
    }
    @BeforeEach
    public void setup() throws Exception {
        entityManager = entityManagerFactory.createEntityManager();
        
    }
    @Test
    public void testConectar() {
    	// vacío, para ver que levante el ORM
    }

    @Test   
    public void testGuardarYRecuperarVianda() throws Exception {
    	Vianda vianda1 = new Vianda();
        vianda1.setCodigoQR("abc"); //NO PUEDE SER EL MISMO
        vianda1.setFechaElaboracion(LocalDateTime.now());
        vianda1.setEstado(EstadoViandaEnum.PREPARADA);
        vianda1.setColaboradorId(15L);
        vianda1.setHeladeraId(11);

        entityManager.getTransaction().begin();
        entityManager.persist(vianda1);
        entityManager.getTransaction().commit();
        entityManager.close();

        Long viandaId = vianda1.getId();

        entityManager = entityManagerFactory.createEntityManager();
        Vianda vianda2 = entityManager.find(Vianda.class, viandaId);

        assertEquals(vianda1.getQr(), vianda2.getQr());
    }
}