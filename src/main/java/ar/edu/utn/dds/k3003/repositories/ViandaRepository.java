package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Vianda;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.NoSuchElementException;

public class ViandaRepository {
    public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
}
    public EntityManager getEntityManager() {
        return entityManager;
    }
    private EntityManager entityManager;

    public ViandaRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    public ViandaRepository() {
	}
public Vianda save(Vianda vianda) {
    EntityTransaction transaction = entityManager.getTransaction();
    boolean isNewTransaction = false;

    try {
        if (!transaction.isActive()) {
            transaction.begin();
            isNewTransaction = true;
        }

        if (vianda.getId() == null) {
            entityManager.persist(vianda);
        } else {
            entityManager.merge(vianda);
        }

        if (isNewTransaction) {
            transaction.commit();
        }

        return vianda;
    } catch (Exception e) {
        if (isNewTransaction && transaction.isActive()) {
            transaction.rollback();
        }
        throw e;
    }
}
    public Vianda buscarPorQr(String qr) {
        TypedQuery<Vianda> query = entityManager.createQuery("SELECT v FROM Vianda v WHERE v.codigoQR = :qr", Vianda.class);
        query.setParameter("qr", qr);
        List<Vianda> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Vianda> getViandas() {
        TypedQuery<Vianda> query = entityManager.createQuery("SELECT v FROM Vianda v", Vianda.class);
        return query.getResultList();
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
                throw new NoSuchElementException("No se encontró la vianda");
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}