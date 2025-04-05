package com.teste.pratico.agenda.entities;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ESTACIONAMENTO_ID")
    private Estacionamento estacionamento;

    @ManyToOne
    @JoinColumn(name = "SOLICITANTE_ID")
    private Solicitante solicitante;

    @Column(name = "DATA_INICIO")
    private Timestamp dataInicio;

    @Column(name = "DATA_FIM")
    private Timestamp dataFim;

    @Column(name = "VALOR_TOTAL")
    private Double valorTotal;

    public Reserva() {
    }

    public Reserva(Estacionamento estacionamento, Solicitante solicitante, Timestamp dataInicio) {
        this.estacionamento = estacionamento;
        this.solicitante = solicitante;
        this.dataInicio = dataInicio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Estacionamento getEstacionamento() {
        return estacionamento;
    }

    public void setEstacionamento(Estacionamento estacionamento) {
        this.estacionamento = estacionamento;
    }

    public Solicitante getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Solicitante solicitante) {
        this.solicitante = solicitante;
    }

    public Timestamp getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Timestamp dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Timestamp getDataFim() {
        return dataFim;
    }

    public void setDataFim(Timestamp dataFim) {
        this.dataFim = dataFim;
    }

    public Double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(Double valorTotal) {
        this.valorTotal = valorTotal;
    }

    
}
