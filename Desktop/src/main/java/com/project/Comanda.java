package com.project;

public class Comanda {
    private int idComanda;
    private int idTaula;
    private int idCambrer;
    private String comanda;
    private String dataComanda;
    private String estatComanda;
    private double preuComanda;
    private boolean pagada;

    public Comanda(int idComanda, int idTaula, int idCambrer, String comanda, String dataComanda, String estatComanda, double preuComanda, boolean pagada) {
        this.idComanda = idComanda;
        this.idTaula = idTaula;
        this.idCambrer = idCambrer;
        this.comanda = comanda;
        this.dataComanda = dataComanda;
        this.estatComanda = estatComanda;
        this.preuComanda = preuComanda;
        this.pagada = pagada;
    }

    public int getIdComanda() {
        return idComanda;
    }

    public void setIdComanda(int idComanda) {
        this.idComanda = idComanda;
    }

    public int getIdTaula() {
        return idTaula;
    }

    public void setIdTaula(int idTaula) {
        this.idTaula = idTaula;
    }

    public int getIdCambrer() {
        return idCambrer;
    }

    public void setIdCambrer(int idCambrer) {
        this.idCambrer = idCambrer;
    }

    public String getComanda() {
        return comanda;
    }

    public void setComanda(String comanda) {
        this.comanda = comanda;
    }

    public String getDataComanda() {
        return dataComanda;
    }

    public void setDataComanda(String dataComanda) {
        this.dataComanda = dataComanda;
    }

    public String getEstatComanda() {
        return estatComanda;
    }

    public void setEstatComanda(String estatComanda) {
        this.estatComanda = estatComanda;
    }

    public double getPreuComanda() {
        return preuComanda;
    }

    public void setPreuComanda(double preuComanda) {
        this.preuComanda = preuComanda;
    }

    public boolean isPagada() {
        return pagada;
    }

    public void setPagada(boolean pagada) {
        this.pagada = pagada;
    }
}
