package com.teste.pratico.agenda.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import com.teste.pratico.agenda.enums.StatusVagaEnum;

@Entity
@Table(name = "ESTACIONAMENTO")
public class Estacionamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "NUMERO")
    private Double numero;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "VALOR_POR_HORA")
    private Double valorPorHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private StatusVagaEnum status;

    public Estacionamento() {
    }

    public Estacionamento(Double numero, String tipo, Double valorPorHora) {
        this.numero = numero;
        this.tipo = tipo;
        this.valorPorHora = valorPorHora;
        this.status = StatusVagaEnum.DISPONIVEL;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getNumero() {
        return numero;
    }

    public void setNumero(Double numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValorPorHora() {
        return valorPorHora;
    }

    public void setValorPorHora(Double valorPorHora) {
        this.valorPorHora = valorPorHora;
    }

    public StatusVagaEnum getStatus() {
        return status;
    }
    
    public void setStatus(StatusVagaEnum status) {
        this.status = status;
    }
}
