package Graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.PriorityQueue;

public class Graph {
	private static final boolean DIRECTED = true;
	private static final boolean UNDIRECTED = false;
	private static final int DISTANCE = 0;
	private static final int TIME = 1;
	private ArrayList<Vertex> vertex;
	private boolean directed;

	public Graph() {
		this(false);
	}

	public Graph(boolean directed) {
		this.vertex = new ArrayList<Vertex>();
		this.directed = directed;
	}

	public Graph(Graph toClone) {
		this.vertex = new ArrayList<Vertex>();
		for(Vertex v: toClone.vertex){
			Vertex toAdd = new Vertex(v.getIdentifier());
			
			for(Edge e: v.getEdges()){
				toAdd.getEdges().add(e);
			}
			this.vertex.add(toAdd);
		}
		
		this.directed = toClone.directed;
	}

	public boolean addVertex(String id) {
		if (id == null)
			return false;
		if (!vertex.contains(new Vertex(id))) {
			return vertex.add(new Vertex(id));
		}
		return false;
	}

	public void addEdge(String v1, String v2, double[] w) {
		addEdge(v1, v2, w, false);
	}

	public boolean addEdge(String v1, String v2, double[] w, boolean force) {
		if (!this.vertex.contains(new Vertex(v1))) {
			if (force) {
				this.addVertex(v1);
			} else {
				return false;
			}
		}
		if (!this.vertex.contains(new Vertex(v2))) {
			if (force) {
				this.addVertex(v2);
			} else {
				return false;
			}
		}

		if (this.vertex.get(this.vertex.indexOf(new Vertex(v1))).getEdges()
				.contains(new Edge(v1, v2, w, this.directed))) {
			return false;
		}

		this.vertex.get(this.vertex.indexOf(new Vertex(v1))).getEdges().add(new Edge(v1, v2, w, this.directed));
		this.vertex.get(this.vertex.indexOf(new Vertex(v2))).getEdges().add(new Edge(v1, v2, w, this.directed));
		return true;

	}

	public boolean removeEdge(String v1, String v2) {
		if (v1 == null || v2 == null) {
			return false;
		}

		else if (!this.vertex.contains(new Vertex(v1)) || !this.vertex.contains(new Vertex(v2))) {
			return false;
		}
		double[] temp = {0.0, 0.0};
		this.vertex.get(this.vertex.indexOf(new Vertex(v1))).getEdges().remove(new Edge(v1, v2, temp, this.directed));
		this.vertex.get(this.vertex.indexOf(new Vertex(v2))).getEdges().remove(new Edge(v1, v2, temp, this.directed));
		return true;
	}

	public boolean removeEdge(Edge e) {
		if (e == null) {
			return false;
		}
		if (this.directed) {
			return removeEdge(e.getTo(), e.getFrom());
		} else {
			return removeEdge(e.getV1(), e.getV2());
		}
	}

	public boolean removeVertex(String id) {
		if (id == null) {
			return false;
		}

		ListIterator<Edge> it = this.vertex.get(this.vertex.indexOf(new Vertex(id))).getEdges().listIterator(0);
		while(it.hasNext()){
			Edge e = it.next();
			it.remove();
			if(this.directed){
				if(e.getFrom().equals(id)){
					this.vertex.get(this.vertex.indexOf(new Vertex(e.getTo()))).getEdges().remove(e);
				}
				else{
					this.vertex.get(this.vertex.indexOf(new Vertex(e.getFrom()))).getEdges().remove(e);
				}
			}
			else{
				if(e.getV1().equals(id)){
					this.vertex.get(this.vertex.indexOf(new Vertex(e.getV2()))).getEdges().remove(e);
				}
				else{
					this.vertex.get(this.vertex.indexOf(new Vertex(e.getV1()))).getEdges().remove(e);
				}
			}
		}

		return this.vertex.remove(new Vertex(id));

	}

	public ArrayList<Vertex> rangedDfs(String id, double range, int criteria) {
		Graph g = new Graph(this.directed);
		g.addVertex(id);
		return (this.directed) ? rangedDfsDirected(id, range, 0.0, g, criteria).vertex : rangedDfsUndirected(id, range, 0.0, g, null, criteria).vertex;
	}

	public Graph rangedDfsDirected(String id, double range, double accumulator, Graph resultGraph, int criteria) {
		if (accumulator >= range)
			return resultGraph;

		for (Edge e : this.vertex.get(this.vertex.indexOf(new Vertex(id))).getEdges()) {
			if (e.getTo().equals(id))
				continue;
			else if (e.getFrom().equals(id) && e.getWeight(criteria) + accumulator < range) {
				resultGraph.addVertex(e.getTo());
				resultGraph = rangedDfsDirected(e.getTo(), range, accumulator + e.getWeight(criteria), resultGraph, criteria);
			}
		}

		return resultGraph;
	}

	public Graph rangedDfsUndirected(String id, double range, double accumulator, Graph resultGraph, Vertex previous, int criteria) {
		if (accumulator >= range)
			return resultGraph;

		for (Edge e : this.vertex.get(this.vertex.indexOf(new Vertex(id))).getEdges()) {
			if ((e.getV1().equals(id) && (previous != null) ? (previous.equals(new Vertex(e.getV2()))) : false)
					|| (e.getV2().equals(id) && (previous != null) ? (previous.equals(new Vertex(e.getV1())))
							: false)) {
				continue;
			} else if (e.getV1().equals(id) && e.getWeight(criteria) + accumulator < range) {
				resultGraph.addVertex(e.getV2());
				resultGraph = rangedDfsUndirected(e.getV2(), range, e.getWeight(criteria) + accumulator, resultGraph,
						this.vertex.get(this.vertex.indexOf(new Vertex(id))), criteria);
			} else if (e.getV2().equals(id) && e.getWeight(criteria) + accumulator < range) {
				resultGraph.addVertex(e.getV1());
				resultGraph = rangedDfsUndirected(e.getV1(), range, e.getWeight(criteria) + accumulator, resultGraph,
						this.vertex.get(this.vertex.indexOf(new Vertex(id))), criteria);
			}
		}
		return resultGraph;
	}

	public ArrayList<Vertex> invertedRangedDfs(String id, double range, int criteria){
		Graph cloneGraph = new Graph(this);
		ArrayList<Vertex> rdfsVertex = rangedDfs(id, range, criteria);
		for(Vertex v: rdfsVertex){
			cloneGraph.removeVertex(v.getIdentifier());
		}
		return cloneGraph.vertex;
	}
	
	public Graph getCommonVertex(Graph g1, Graph g2){
		ListIterator<Vertex> it = g1.getVertex().listIterator();
		while(it.hasNext()){
			Vertex v = it.next();
			if(!g2.getVertex().contains(v)){
				it.remove();
			}
		}
		return g1 ;
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Vertex v : vertex) {
			builder.append(v.getIdentifier() + ": ");
			for (Edge e : v.getEdges()) {
				if (this.directed && e.getFrom().equals(v.getIdentifier())) {
					builder.append(e.getTo() + ", ");
				} else if (!this.directed) {
					if (e.getV1().equals(v.getIdentifier())) {
						builder.append(e.getV2() + ", ");
					} else {
						builder.append(e.getV1() + ", ");
					}
				}
			}
			builder.append("\b\b\n");
		}
		return builder.toString();
	}

	public int getNumVertex() {
		return this.vertex.size();
	}

	public boolean checkExistsVertex(String id) {
		return this.vertex.contains(new Vertex(id));
	}

	public Graph dijkstra(String id, int criteria) {
		if (!this.vertex.contains(new Vertex(id))) {
			return null;
		}

		Graph dijkstraGraph = new Graph(this.directed);
		HashMap<String, Double> values = new HashMap<String, Double>();
		HashMap<String, String> previous = new HashMap<String, String>();
		VComparator comp = new VComparator(values);
		PriorityQueue<String> pq = new PriorityQueue<String>(this.getNumVertex(), comp);

		for (Vertex v : this.vertex) {
			values.put(v.getIdentifier(), Double.MAX_VALUE);
			previous.put(v.getIdentifier(), null);
		}

		values.remove(id);
		values.put(id, 0.0);

		comp.update(values);

		pq.add(id);
		while (!pq.isEmpty()) {
			for (Edge e : this.vertex.get(this.vertex.indexOf(new Vertex(pq.peek()))).getEdges()) {
				if (this.directed && e.getFrom().equals(pq.peek())) {
					if (values.get(e.getTo()) >= e.getWeight(criteria) + values.get(e.getFrom())) {

						values.remove(e.getTo());
						values.put(e.getTo(), e.getWeight(criteria) + values.get(e.getFrom()));
						previous.remove(e.getTo());
						previous.put(e.getTo(), e.getFrom());
						if (pq.contains(e.getTo())) {
							pq.remove(e.getTo());
						}
						comp.update(values);
						pq.add(e.getTo());
					}
				} else if (!this.directed) {
					if (e.getV1().equals(pq.peek()) && (previous.get(pq.peek()) == null) ? true
							: !previous.get(pq.peek()).equals(e.getV2())) {
						if (values.get(e.getV2()) >= e.getWeight(criteria) + values.get(e.getV1())) {
							values.remove(e.getV2());
							values.put(e.getV2(), e.getWeight(criteria) + values.get(e.getV1()));
							previous.remove(e.getV2());
							previous.put(e.getV2(), e.getV1());
							if (pq.contains(e.getV2())) {
								pq.remove(e.getV2());
							}
							comp.update(values);
							pq.add(e.getV2());
						}
					} else if (e.getV2().equals(pq.peek()) && !previous.get(pq.peek()).equals(e.getV1())) {
						if (values.get(e.getV1()) >= e.getWeight(criteria) + values.get(e.getV2())) {
							values.remove(e.getV1());
							values.put(e.getV1(), e.getWeight(criteria) + values.get(e.getV2()));
							previous.remove(e.getV1());
							previous.put(e.getV1(), e.getV2());
							if (pq.contains(e.getV1())) {
								pq.remove(e.getV1());
							}
							comp.update(values);
							pq.add(e.getV1());
						}
					}
				}
			}
			pq.remove();
		}

		for (Vertex v : this.vertex) {
			dijkstraGraph.addVertex(v.getIdentifier());
			if (previous.get(v.getIdentifier()) != null) {
				double[] temp = {0.0, 0.0};
				dijkstraGraph.addEdge(previous.get(v.getIdentifier()), v.getIdentifier(), temp, true);
			}
		}

		return dijkstraGraph;

	}

	public ArrayList<Graph> yen(String startId, String endId, int nrPaths, int criteria) {

		if (!this.vertex.contains(new Vertex(startId)) || !this.vertex.contains(new Vertex(endId))) {
			return null;
		}

		ArrayList<Graph> yenArray = new ArrayList<Graph>();
		// get the shortest path -> the first one on the array
		yenArray.add(dijkstra(startId, criteria)); // SUBSTITUIR PELO DO TRINDADE

		ArrayList<Vertex> rootPath = new ArrayList<Vertex>();
		Vertex spurVertex;
		String v1;
		String v2;

		for (int k = 1; k < nrPaths; k++) {

			for (int i = 0; i < (yenArray.get(k - 1).vertex.size() - 1); i++) {

				spurVertex = yenArray.get(k - 1).vertex.get(i);
				rootPath = rootPath(yenArray.get(k - 1), i);

				if (rootPath.size() != 0) {
					v1 = rootPath.get(rootPath.size() - 2).getIdentifier();
					v2 = rootPath.get(rootPath.size() - 1).getIdentifier();

					double originalWeight = 0;

					for (int index = 0; index < this.vertex.size(); index++) {
						for (int j = 0; j < this.vertex.get(index).getEdges().size(); j++) {

							// set the weight to max
							if (v1.equals(this.vertex.get(index).getEdges().get(j).getV1())) {
								if (v2.equals(this.vertex.get(index).getEdges().get(j).getV2())) {
									originalWeight = this.vertex.get(index).getEdges().get(j).getWeight(criteria);
									this.vertex.get(index).getEdges().get(j).setWeight(Double.MAX_VALUE, criteria);
								}
							}
							if (v2.equals(this.vertex.get(index).getEdges().get(j).getV1())) {
								if (v1.equals(this.vertex.get(index).getEdges().get(j).getV2())) {
									originalWeight = this.vertex.get(index).getEdges().get(j).getWeight(criteria);
									this.vertex.get(index).getEdges().get(j).setWeight(Double.MAX_VALUE, criteria);
								}
							}
						}
					}

					yenArray.add(dijkstra(spurVertex.getIdentifier(), criteria)); // SUBSTITUIR
																				  // PELO
																				  // DO
																				  // TRINDADE

					for (int index = 0; index < this.vertex.size(); index++) {
						for (int j = 0; j < this.vertex.get(index).getEdges().size(); j++) {

							// set the weight back to its original value
							if (v1.equals(this.vertex.get(index).getEdges().get(j).getV1()))
								if (v2.equals(this.vertex.get(index).getEdges().get(j).getV2()))
									this.vertex.get(index).getEdges().get(j).setWeight(originalWeight, criteria);
							if (v2.equals(this.vertex.get(index).getEdges().get(j).getV1()))
								if (v1.equals(this.vertex.get(index).getEdges().get(j).getV2()))
									this.vertex.get(index).getEdges().get(j).setWeight(originalWeight, criteria);

						}
					}

				}
			}
		}

		return yenArray;

	}

	private ArrayList<Vertex> rootPath(Graph spurGraph, int i) {

		ArrayList<Vertex> rootPath = new ArrayList<Vertex>();

		for (int j = 0; j < i; j++)
			rootPath.add(spurGraph.vertex.get(j));

		return rootPath;

	}

	private class VComparator implements Comparator<String> {
		HashMap<String, Double> values;

		public VComparator(HashMap<String, Double> vals) {
			this.values = vals;
		}

		public void update(HashMap<String, Double> vals) {
			this.values = vals;
		}

		@Override
		public int compare(String v1, String v2) {
			return ((int) (this.values.get(v1) - this.values.get(v2)));
		}

	}

	public void printEdges() {
		for (Vertex v : this.vertex) {
			for (Edge e : v.getEdges()) {
				System.out.println((this.directed)?(e.getFrom() + "-->" + e.getTo()):(e.getV1() + "---" + e.getV2()));
			}
		}
		System.out.println("\n\n\n");
	}

	public ArrayList<Vertex> getVertex() {
		return vertex;
	}

	public static void main(String args[]) {
		Graph testG = new Graph(DIRECTED);

		/*
		 * testG.addEdge("A", "C", 10.0, true); testG.addEdge("A", "D", 3.0,
		 * true); testG.addEdge("D", "C", 4.0, true); testG.addEdge("C", "B",
		 * 20.0, true); testG.addEdge("B", "E", 4.0, true); testG.addEdge("B",
		 * "F", 4.0, true); testG.addEdge("B", "E", 4.0, true);
		 * testG.addEdge("E", "F", 4.0, true);
		 */

		testG.addEdge("A", "B", new double[]{10.0, 10.0}, true);
		testG.addEdge("B", "C", new double[]{20.0, 20.0}, true);
		testG.addEdge("B", "D", new double[]{5.0, 5.0}, true);
		testG.addEdge("D", "C", new double[]{55.0, 55.0}, true);

		Graph testG2 = new Graph(testG);
		// System.out.println(testG.getVertex().indexOf("A"));
		
		//System.out.println("\n\n\n" + testG.invertedRangedDfs("A", 30.0));
		//System.out.println("\n\n\n" + testG.rangedDfs("A", 30.0));
		
		/*for(Vertex v: testG.invertedRangedDfs("A", 30.0, DISTANCE)){
			System.out.println(v.getIdentifier());
		}
		
		for(Vertex v: testG.rangedDfs("A", 30.0, DISTANCE)){
			System.out.println(v.getIdentifier());
		}
		
		System.out.println(testG);*/
		
		
		
		
		Graph otherG = testG.dijkstra("A", DISTANCE);
		 System.out.println(otherG);
		 System.out.println("\n\n\n" + testG);

	}
}