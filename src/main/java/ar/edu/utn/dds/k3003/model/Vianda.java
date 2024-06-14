package ar.edu.utn.dds.k3003.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ar.edu.utn.dds.k3003.facades.dtos.EstadoViandaEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "viandas")
public class Vianda {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "codigo_qr", nullable = false, unique = true)
	private String qr;
	
	@Column(name = "colaborador_id", nullable = false)
	private Long colaboradorId;
	
	@Column(name = "heladera_id", nullable = false)
	private Integer heladeraId;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
	private EstadoViandaEnum estado;
	
	@Column(name = "fecha_elaboracion", nullable = false)
	private LocalDateTime fechaElaboracion;
    public Vianda(String qr, LocalDateTime fechaElaboracion, EstadoViandaEnum estado, Long colaboradorId, Integer heladeraId) {
    	this.qr = qr;
    	this.colaboradorId = colaboradorId;
    	this.heladeraId = heladeraId;
    	this.estado = estado;
    	this.fechaElaboracion = LocalDateTime.now();
    }
    
    public void setEstado(EstadoViandaEnum nuevoEstado) {
		this.estado = nuevoEstado;
	}
    public void setId(Long nuevoId) {
		this.id = nuevoId;
	}
    public void setHeladeraId(Integer heladeraDestino) {
		this.heladeraId = heladeraDestino;
	}
    public void setFechaElaboracion(LocalDateTime nuevaFecha) {
		this.fechaElaboracion = nuevaFecha;
	}
	public void setColaboradorId(Long nuevoColaboradorId) {
		this.colaboradorId = nuevoColaboradorId;	
	}
	public void setCodigoQR(String string) {
		this.qr = string;
	}


	public Long getId() {
		return id;
	}
	public String getQr() {
		return qr;
	}
	public Long getColaboradorId() {
		return colaboradorId;
	}
	public Integer getHeladeraId() {
		return heladeraId;
	}
	public EstadoViandaEnum getEstado() {
		return estado;
	}
	public LocalDateTime getFechaElaboracion() {
		return fechaElaboracion;
	}

	

	
	

}