package ar.edu.utn.dds.k3003.repositories;

import ar.edu.utn.dds.k3003.model.Vianda;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Setter
@Getter
public class ViandaRepository {
    private final EntityManager entityManager;

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
                throw new NoSuchElementException("No se encontró la vianda a actualizar");
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    public void borrarTodo() {
        entityManager.getTransaction().begin();
        try {
            int deletedCount = entityManager.createQuery("DELETE FROM Vianda").executeUpdate();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
            throw e;
        }
    }
}