package it.polito.tdp.extflightdelays.model;

public class EdgeModel {
	private Airport partenza;
	private Airport destinazione;
	private Integer peso;
	
	public EdgeModel(Airport partenza, Airport destinazione, Integer peso) {
		super();
		this.partenza = partenza;
		this.destinazione = destinazione;
		this.peso = peso;
	}

	public Integer getPeso() {
		return peso;
	}

	public void setPeso(Integer peso) {
		this.peso = peso;
	}

	public Airport getPartenza() {
		return partenza;
	}

	public Airport getDestinazione() {
		return destinazione;
	}
	
	
}
