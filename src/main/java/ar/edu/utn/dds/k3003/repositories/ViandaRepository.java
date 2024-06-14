package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Vianda;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Setter
@Getter
public class ViandaRepository {
	static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("viandas");
	EntityManager entityManager = entityManagerFactory.createEntityManager();

    public ViandaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Vianda save(Vianda vianda) throws NoSuchElementException {

        if (Objects.isNull(vianda.getId())) {
            entityManager.getTransaction().begin();
            entityManager.persist(vianda);
            entityManager.getTransaction().commit();
        }

        return vianda;
    }

    public Vianda buscarPorQr(String qr) {
    	Collection<Vianda> viandas = entityManager.createQuery("from Vianda",Vianda.class).getResultList();
        Optional<Vianda> first = viandas.stream().filter(v -> v.getQr().equals(qr)).findFirst();
        return first.orElseThrow(() -> new NoSuchElementException(
            String.format("No hay una vianda de qr: %s", qr)
        ));
    }

    public List<Vianda> getViandas() {
    	List<Vianda> viandas = entityManager.createQuery("from Vianda", Vianda.class).getResultList();
        return viandas;
    }

    public Vianda findById(Long id) {
        Vianda vianda = entityManager.find(Vianda.class, id);
        if (vianda == null) {
            throw new NoSuchElementException(String.format("No hay una vianda de id: %s", id));
        }
        return vianda;
    }




    public void update(Vianda vianda) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Vianda existingVianda = entityManager.find(Vianda.class, vianda.getId());
            if (existingVianda != null) {
                existingVianda.setColaboradorId(vianda.getColaboradorId());
                existingVianda.setHeladeraId(vianda.getHeladeraId());
                existingVianda.setEstado(vianda.getEstado());
                existingVianda.setFechaElaboracion(vianda.getFechaElaboracion());
                entityManager.merge(existingVianda);
            } else {
                throw new NoSuchElementException("No se encontró la vianda a actualizar");
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

}