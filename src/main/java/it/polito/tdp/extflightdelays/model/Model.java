package it.polito.tdp.extflightdelays.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.traverse.BreadthFirstIterator;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	private Graph<Airport, DefaultWeightedEdge> grafo;
	private Map<Integer, Airport> idMap;
	private ExtFlightDelaysDAO dao;

	public Model() {
		this.grafo = new SimpleWeightedGraph<Airport, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.idMap = new HashMap<>();
		this.dao = new ExtFlightDelaysDAO();
		this.dao.loadAllAirports(idMap);
	}

	public void buildGraph(int nAirLines) {
		// Caricare i vertici nel grafo
		if (this.grafo.vertexSet().size() == 0) {
			Graphs.addAllVertices(this.grafo, this.dao.getAllNodes(nAirLines, idMap));

			// Caricare gli archi nel grafo

			List<EdgeModel> edges = this.dao.getAllEdges(idMap);

			for (EdgeModel e : edges) {
				Airport origin = e.getPartenza();
				Airport destination = e.getDestinazione();
				int N = e.getPeso();

				if (grafo.vertexSet().contains(origin) && grafo.vertexSet().contains(destination)) {
					DefaultWeightedEdge edge = this.grafo.getEdge(origin, destination);
					if (edge != null) {
						double weight = this.grafo.getEdgeWeight(edge);
						weight += N;
						this.grafo.setEdgeWeight(origin, destination, weight);
					} else {
						this.grafo.addEdge(origin, destination);
						this.grafo.setEdgeWeight(origin, destination, N);
					}
				}
			}
		}
		System.out.println("Grafo creato");
		System.out.println("Ci sono " + this.grafo.vertexSet().size() + " vertici");
		System.out.println("Ci sono " + this.grafo.edgeSet().size() + " edges");

	}

	public List<Airport> getVertici() {
		List<Airport> vertici = new ArrayList<>(this.grafo.vertexSet());
		Collections.sort(vertici);
		return vertici;
	}

	public List<Airport> trovaPercorso(Airport origin, Airport destination) {
		List<Airport> percorso = new ArrayList<>();
		BreadthFirstIterator<Airport, DefaultWeightedEdge> it = new BreadthFirstIterator<>(this.grafo, origin);
		Boolean trovato = false;

		// visito il grafo fino alla fine o fino a che non trovo la destinazione
		while (it.hasNext() & !trovato) {
			Airport visitato = it.next();
			if (visitato.equals(destination))
				trovato = true;
		}

		/*
		 * se ho trovato la destinazione, costruisco il percorso risalendo l'albero di
		 * visita in senso opposto, ovvero partendo dalla destinazione fino ad arrivare
		 * all'origine, ed aggiiungo gli aeroporti ad ogni step IN TESTA alla lista se
		 * non ho trovato la destinazione, restituisco null.
		 */
		if (trovato) {
			percorso.add(destination);
			Airport step = it.getParent(destination);
			while (!step.equals(origin)) {
				percorso.add(0, step);
				step = it.getParent(step);
			}

			percorso.add(0, origin);
			return percorso;
		} else {
			return null;
		}

	}

	public boolean esistePercorso(Airport origin, Airport destination) {
		ConnectivityInspector<Airport, DefaultWeightedEdge> inspect = new ConnectivityInspector<Airport, DefaultWeightedEdge>(
				this.grafo);
		Set<Airport> componenteConnessaOrigine = inspect.connectedSetOf(origin);
		return componenteConnessaOrigine.contains(destination);
	}
}
