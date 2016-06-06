package Graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

public class Graph {
	private static final boolean DIRECTED = true;
	private static final boolean UNDIRECTED = false;
	public static final int DISTANCE = 0;
	public static final int TIME = 1;
	private ArrayList<Vertex> vertex;
	private boolean directed;

	private Stack<String> path = new Stack<String>();
	private Set<String> onPath = new HashSet<String>();
	private ArrayList<Stack<String>> paths = new ArrayList<Stack<String>>();

	/**
	 * @Constructor: Creates an empty undirected graph
	 */
	public Graph() {
		this(false);
	}

	/**
	 * @Constructor: Creates an empty graph
	 * @param directed Determines whether the graph is directed or not. true = directed, false = undirected
	 */
	public Graph(boolean directed) {
		this.vertex = new ArrayList<Vertex>();
		this.directed = directed;
	}

	/**
	 * @Constructor: Creates a graph that's a clone of toClone
	 * @param toClone The graph to copy
	 */
	public Graph(Graph toClone) {
		this.vertex = new ArrayList<Vertex>();
		for (Vertex v : toClone.vertex) {
			Vertex toAdd = new Vertex(v.getIdentifier());

			for (Edge e : v.getEdges()) {
				toAdd.getEdges().add(e);
			}
			this.vertex.add(toAdd);
		}

		this.directed = toClone.directed;
	}

	/**
	 * Adds a vertex to the graph
	 * @param id The identifier for the graph
	 * @return true if created, false if a vertex with the same ID already existed
	 */
	public boolean addVertex(String id) {
		if (id == null)
			return false;
		if (!vertex.contains(new Vertex(id))) {
			return vertex.add(new Vertex(id));
		}
		return false;
	}

	/**
	 * Adds an edge to the mentioned vertex
	 * @param v1 first vertex
	 * @param v2 second vertex
	 * @param w weights for the edge
	 */
	public void addEdge(String v1, String v2, double[] w) {
		addEdge(v1, v2, w, false);
	}

	/**
	 * Adds an edge to the mentioned vertex
	 * @param v1 first vertex
	 * @param v2 second vertex
	 * @param w weights for the edge
	 * @param force if true and one of the passed IDs doesn't correspond to a vertex, the vertex will be created
	 * @return true if the edge is created, false if not
	 */
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

	/**
	 * Removes the edge that connects the mentioned vertex
	 * @param v1 first vertex
	 * @param v2 second vertex
	 * @return true if the edge is removed, false otherwise
	 */
	public boolean removeEdge(String v1, String v2) {
		if (v1 == null || v2 == null) {
			return false;
		}

		else if (!this.vertex.contains(new Vertex(v1)) || !this.vertex.contains(new Vertex(v2))) {
			return false;
		}
		double[] temp = { 0.0, 0.0 };
		this.vertex.get(this.vertex.indexOf(new Vertex(v1))).getEdges().remove(new Edge(v1, v2, temp, this.directed));
		this.vertex.get(this.vertex.indexOf(new Vertex(v2))).getEdges().remove(new Edge(v1, v2, temp, this.directed));
		return true;
	}

	/**
	 * Removes the edge edge that's passed as a parameter
	 * @param e
	 * @return true if removed, false otherwise
	 */
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

	/**
	 * Removes the mentioned vertex from the graph, removing also all of the associated edges
	 * @param id
	 * @return
	 */
	public boolean removeVertex(String id) {
		if (id == null) {
			return false;
		}

		ListIterator<Edge> it = this.vertex.get(this.vertex.indexOf(new Vertex(id))).getEdges().listIterator(0);
		while (it.hasNext()) {
			Edge e = it.next();
			it.remove();
			if (this.directed) {
				if (e.getFrom().equals(id)) {
					this.vertex.get(this.vertex.indexOf(new Vertex(e.getTo()))).getEdges().remove(e);
				} else {
					this.vertex.get(this.vertex.indexOf(new Vertex(e.getFrom()))).getEdges().remove(e);
				}
			} else {
				if (e.getV1().equals(id)) {
					this.vertex.get(this.vertex.indexOf(new Vertex(e.getV2()))).getEdges().remove(e);
				} else {
					this.vertex.get(this.vertex.indexOf(new Vertex(e.getV1()))).getEdges().remove(e);
				}
			}
		}

		return this.vertex.remove(new Vertex(id));

	}

	/**
	 * Goes through the graph depth-first until it reaches the provided range
	 * @param id
	 * @param range The weight at which to stop 
	 * @param criteria The weight to use (distance or time)
	 * @return An ArrayList with all the visited vertex
	 */
	public ArrayList<Vertex> rangedDfs(String id, double range, int criteria) {
		Graph g = new Graph(this.directed);
		g.addVertex(id);
		return (this.directed) ? rangedDfsDirected(id, range, 0.0, g, criteria).vertex
				: rangedDfsUndirected(id, range, 0.0, g, null, criteria).vertex;
	}

	/**
	 * Goes through a directed graph depth-first until it reaches the provided range
	 * @param id The vertex from which to start
	 * @param range The weight at which to stop 
	 * @param accumulator The accumulated weight of all nodes visited from root to the last node visited
	 * @param resultGraph The graph with all of the vertex visited so far
	 * @param criteria The weight to use (distance or time)
	 * @return The graph with all of the vertex visited
	 */
	public Graph rangedDfsDirected(String id, double range, double accumulator, Graph resultGraph, int criteria) {
		if (accumulator >= range)
			return resultGraph;

		for (Edge e : this.vertex.get(this.vertex.indexOf(new Vertex(id))).getEdges()) {
			if (e.getTo().equals(id))
				continue;
			else if (e.getFrom().equals(id) && e.getWeight(criteria) + accumulator < range) {
				resultGraph.addVertex(e.getTo());
				resultGraph = rangedDfsDirected(e.getTo(), range, accumulator + e.getWeight(criteria), resultGraph,
						criteria);
			}
		}

		return resultGraph;
	}

	/**
	 * Goes through a directed graph depth-first until it reaches the provided range
	 * @param id The vertex from which to start
	 * @param range The weight at which to stop 
	 * @param accumulator The accumulated weight of all nodes visited from root to the last node visited
	 * @param resultGraph The graph with all of the vertex visited so far
	 * @param previous The last visited node
	 * @param criteria The weight to use (distance or time)
	 * @return The graph with all of the vertex visited
	 */
	public Graph rangedDfsUndirected(String id, double range, double accumulator, Graph resultGraph, Vertex previous,
			int criteria) {
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

	/**
	 * Uses rangedDfs to return all of the vertex that weren't visited
	 * @param id The vertex from which to start
	 * @param range The weight at which to stop
	 * @param criteria The weight to use (distance or time)
	 * @return An ArrayList with all the visited vertex
	 */
	public ArrayList<Vertex> invertedRangedDfs(String id, double range, int criteria) {
		Graph cloneGraph = new Graph(this);
		ArrayList<Vertex> rdfsVertex = rangedDfs(id, range, criteria);
		for (Vertex v : rdfsVertex) {
			cloneGraph.removeVertex(v.getIdentifier());
		}
		return cloneGraph.vertex;
	}
	
	/**
	 * Gets the vertex that exist in both of the passed ArrayLists
	 * @param v1 first ArrayList 
	 * @param v2 second ArrayList
	 * @return An ArrayList with all of the common vertex
	 */
	public ArrayList<Vertex> getCommonVertex(ArrayList<Vertex> v1, ArrayList<Vertex> v2){
		ArrayList<Vertex> vTemp = new ArrayList<Vertex>();
		for(Vertex v: v1){
			vTemp.add(v);
		}
		
		ListIterator<Vertex> it = vTemp.listIterator();
		while(it.hasNext()){
			Vertex v = it.next();
			if(!v2.contains(v)){
				it.remove();
			}
		}
		return vTemp ;
	}

	/**
	 * Returns a string containing the edges and all of their connections
	 */
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

	/**
	 * Gets the number of vertex in this graph
	 * @return The number of vertex in this graph
	 */
	public int getNumVertex() {
		return this.vertex.size();
	}

	/**
	 * Checks if a vertex with the passed ID exists in this graph
	 * @param id The ID to check
	 * @return true if vertex exists, false otherwise
	 */
	public boolean checkExistsVertex(String id) {
		return this.vertex.contains(new Vertex(id));
	}

	
	  public Graph passing(String origin, String destination, String passing,
	  int criteria) { Graph result = new Graph(this.directed);
	  
	  Graph firstLeg = yen(origin, passing, 1, criteria).get(0); Graph
	  secondLeg = yen(passing, destination, 1, criteria).get(0);
	  
	  for (Vertex v : firstLeg.getVertex()) { for (Edge e : v.getEdges()) {
	  if (this.directed) { result.addEdge(e.getFrom(), e.getTo(),
	  e.getWeight(), true); } else { result.addEdge(e.getV1(), e.getV2(),
	  e.getWeight(), true); } } }
	  
	  for (Vertex v : secondLeg.getVertex()) { for (Edge e : v.getEdges()) {
	  if (this.directed) { result.addEdge(e.getFrom(), e.getTo(),
	  e.getWeight(), true); } else { result.addEdge(e.getV1(), e.getV2(),
	  e.getWeight(), true); } } }
	  
	  return result; }
	 

	/**
	 * Gets the shortest path from the mentioned vertex to all other vertex
	 * @param id The vertex from which to start
	 * @param criteria The weight to use (distance or time)
	 * @return A graph with only the shortest paths from the mentioned node to all other nodes
	 */
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
				double[] temp = { 0.0, 0.0 };
				dijkstraGraph.addEdge(previous.get(v.getIdentifier()), v.getIdentifier(), temp, true);
			}
		}

		return dijkstraGraph;

	}

	/**
	 * Gets the shortest path between the mentioned vertex
	 * @param start Vertex at which to start
	 * @param end Destination vertex
	 * @param criteria The weight to use (distance or time)
	 * @return A graph containing the shortest path between the two vertex
	 */
	public Graph dijkstraYen(String start, String end, int criteria) {

		Graph graph = new Graph();
		Graph copy = new Graph(this);

		copy.allPaths(start, end);

		HashMap<Stack<String>, Double> totalPathWeight = new HashMap<Stack<String>, Double>();
		HashMap<ArrayList<String>, Double> totalPathWeightConverted = new HashMap<ArrayList<String>, Double>();

		double totalWeight = 0;

		for (int j = 0; j < copy.paths.size(); j++) {

			Stack<String> path = copy.paths.get(j);
			Stack<String> copiedStack = (Stack<String>) path.clone();
			int totalSize = path.size() - 1;
			String startVertex = path.pop();
			String endVertex = path.pop();

			for (int k = 0; k < totalSize; k++) {
				double edgeWeight = getEdgeWeight(startVertex, endVertex, criteria);
				totalWeight += edgeWeight;
				startVertex = endVertex;
				if (k == totalSize - 1)
					break;
				endVertex = path.pop();
			}

			totalPathWeight.put(copiedStack, totalWeight);
			totalWeight = 0;

		}

		for (HashMap.Entry<Stack<String>, Double> entry : totalPathWeight.entrySet()) {
			ArrayList<String> path = new ArrayList<String>(entry.getKey());
			totalPathWeightConverted.put(path, entry.getValue());
		}

		ArrayList<String> bestPath = new ArrayList<String>();
		Double previousBetter = Double.MAX_VALUE;

		for (HashMap.Entry<ArrayList<String>, Double> entry : totalPathWeightConverted.entrySet()) {
			if (entry.getValue() < previousBetter) {
				previousBetter = entry.getValue();
				bestPath = entry.getKey();
			}
		}

		for (int l = 0; l < (bestPath.size() - 1); l++) {
			double[] total = new double[2];
			double weight1;
			double weight2;
			if (criteria == DISTANCE) {
				weight1 = getEdgeWeight(bestPath.get(l), bestPath.get(l + 1), criteria);
				weight2 = getEdgeWeight(bestPath.get(l), bestPath.get(l + 1), TIME);
			} else {
				weight2 = getEdgeWeight(bestPath.get(l), bestPath.get(l + 1), criteria);
				weight1 = getEdgeWeight(bestPath.get(l), bestPath.get(l + 1), DISTANCE);
			}
			total[0] = weight1;
			total[1] = weight2;
			graph.addEdge(bestPath.get(l), bestPath.get(l + 1), total, true);
		}

		return graph;

	}

	/**
	 * Return the weight of the edge that exists between the mentioned nodes
	 * @param startVertex The vertex at which to start
	 * @param endVertex The destination vertex
	 * @param criteria The weight to obtain (distance or time)
	 * @return The weight of the aforementioned edge
	 */
	private double getEdgeWeight(String startVertex, String endVertex, int criteria) {

		Vertex start = getGraphVertex(this, startVertex);
		double weight = 0;

		for (int i = 0; i < start.getEdges().size(); i++) {
			if (start.getEdges().get(i).getV1().equals(startVertex)
					&& start.getEdges().get(i).getV2().equals(endVertex))
				weight = start.getEdges().get(i).getWeight(criteria);
			else if (start.getEdges().get(i).getV2().equals(startVertex)
					&& start.getEdges().get(i).getV1().equals(endVertex))
				weight = start.getEdges().get(i).getWeight(criteria);
		}

		return weight;

	}

	/**
	 * Gets all paths between the mentioned vertex
	 * @param start Fisrt vertex
	 * @param end Second vertex
	 */
	private void allPaths(String start, String end) {

		path.push(start);
		onPath.add(start);

		if (start.equals(end)) {
			Stack<String> copiedStack = (Stack<String>) path.clone();
			paths.add(copiedStack);
		}

		else {
			for (String w : this.adjacentTo(start)) {
				if (!onPath.contains(w))
					allPaths(w, end);
			}
		}

		path.pop();
		onPath.remove(start);
	}

	/**
	 * Gets all of the vertex that are adjacent to the mentioned vertex
	 * @param v The vertex to start from
	 * @return An ArrayList with all of the adjacent vertex
	 */
	private ArrayList<String> adjacentTo(String v) {

		ArrayList<String> adjacents = new ArrayList<String>();
		Vertex vertex = getGraphVertex(this, v);

		for (int i = 0; i < vertex.getEdges().size(); i++) {
			if (!vertex.getEdges().get(i).getV1().equals(v))
				adjacents.add(vertex.getEdges().get(i).getV1());
			else
				adjacents.add(vertex.getEdges().get(i).getV2());
		}

		return adjacents;
	}

	/**
	 * Get the vertex with the passed ID from the mentioned graph
	 * @param graph graph in which to search
	 * @param id ID for which to search
	 * @return The vertex
	 */
	public Vertex getGraphVertex(Graph graph, String id) {

		for (int i = 0; i < graph.getNumVertex(); i++) {

			if (graph.vertex.get(i).getIdentifier().equals(id))
				return graph.vertex.get(i);

		}

		return null;
	}

	/**
	 * Returns the requested number of paths between the two mentioned vertex
	 * @param startId First vertex
	 * @param endId Second vertex
	 * @param nrPaths Number of paths to get
	 * @param criteria The weight to use (distance or time)
	 * @return An ArrayList with Graphs containing the requested paths
	 */
	public ArrayList<Graph> yen(String startId, String endId, int nrPaths, int criteria) {

		if (!this.vertex.contains(new Vertex(startId)) || !this.vertex.contains(new Vertex(endId))) {
			return null;
		}

		ArrayList<Graph> yenArray = new ArrayList<Graph>();

		// get the shortest path -> the first one on the array
		yenArray.add(dijkstraYen(startId, endId, criteria));

		ArrayList<Vertex> rootPath = new ArrayList<Vertex>();
		Vertex spurVertex;
		String v1;
		String v2;

		for (int k = 1; k < nrPaths; k++) {

			Graph current = yenArray.get(k - 1);

			int max = current.getNumVertex();

			for (int i = 0; i < (max-1); i++) {

				spurVertex = current.vertex.get(i);
				rootPath = current.rootPath(startId, spurVertex.getIdentifier());

				if (rootPath.size() != 0) {
					v1 = rootPath.get(rootPath.size() - 1).getIdentifier();
					v2 = current.vertex.get(i+1).getIdentifier();

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

					yenArray.add(dijkstraYen(startId, endId, criteria));

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

	/**
	 * Gets the number of vertex between the two mentioned vertex
	 * @param source First vertex
	 * @param spur Second vertex
	 * @return The number of vertex between the two mentioned vertex
	 */
	private int getDistance(String source, String spur) {
		Vertex sourceNode = getGraphVertex(this, source);
		Vertex spurNode = getGraphVertex(this, spur);

		int distance = 0;

		for (int i = 0; i < this.getNumVertex(); i++) {
			if (this.vertex.get(i) == spurNode)
				break;
			distance++;
		}

		return distance;

	}

	/**
	 * Gets the path between the two mentioned vertes
	 * @param source First vertex 
	 * @param spur Second vertex
	 * @return An ArrayList with the vertex between the two mentioned nodes
	 */
	private ArrayList<Vertex> rootPath(String source, String spur) {

		Vertex sourceNode = getGraphVertex(this, source);
		Vertex spurNode = getGraphVertex(this, spur);

		ArrayList<Vertex> rootPath = new ArrayList<Vertex>();

		int distance = getDistance(source, spur);

		for (int i = 0; i < (distance + 1); i++) {
			if (this.vertex.get(i) != spurNode) {
				rootPath.add(this.vertex.get(i));
			} else {
				rootPath.add(spurNode);
				break;
			}
		}

		return rootPath;

	}

	/**
	 * 
	 * Class needed for dijkstra to get the correct priorities in a PriorityQueue
	 *
	 */
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

	/**
	 * Print all the edges in the graph
	 */
	public void printEdges() {
		for (Vertex v : this.vertex) {
			for (Edge e : v.getEdges()) {
				System.out
						.println((this.directed) ? (e.getFrom() + "-->" + e.getTo()) : (e.getV1() + "---" + e.getV2()));
			}
		}
	}

	/**
	 * Gets this graph's vertex
	 * @return An Arraylist with this graph's vertex
	 */
	public ArrayList<Vertex> getVertex() {
		return vertex;
	}

	public static void main(String args[]) {
		// Graph testG = new Graph(DIRECTED);

		/*
		 * testG.addEdge("A", "C", 10.0, true); testG.addEdge("A", "D", 3.0,
		 * true); testG.addEdge("D", "C", 4.0, true); testG.addEdge("C", "B",
		 * 20.0, true); testG.addEdge("B", "E", 4.0, true); testG.addEdge("B",
		 * "F", 4.0, true); testG.addEdge("B", "E", 4.0, true);
		 * testG.addEdge("E", "F", 4.0, true);
		 */
		/*
		 * testG.addEdge("A", "B", new double[] { 10.0, 10.0 }, true);
		 * testG.addEdge("B", "C", new double[] { 20.0, 20.0 }, true);
		 * testG.addEdge("B", "D", new double[] { 5.0, 5.0 }, true);
		 * testG.addEdge("D", "C", new double[] { 55.0, 55.0 }, true);
		 * 
		 * Graph testG2 = new Graph(testG);
		 */
		// System.out.println(testG.getVertex().indexOf("A"));

		// System.out.println("\n\n\n" + testG.invertedRangedDfs("A", 30.0));
		// System.out.println("\n\n\n" + testG.rangedDfs("A", 30.0));

		/*
		 * for(Vertex v: testG.invertedRangedDfs("A", 30.0, DISTANCE)){
		 * System.out.println(v.getIdentifier()); }
		 * 
		 * for(Vertex v: testG.rangedDfs("A", 30.0, DISTANCE)){
		 * System.out.println(v.getIdentifier()); }
		 * 
		 * System.out.println(testG);
		 */
		/*
		 * Graph otherG = testG.dijkstra("A", DISTANCE);
		 * System.out.println(otherG); System.out.println("\n\n\n" + testG);
		 */

		Graph graph = new Graph();
		/*
		 * graph.addVertex("A"); graph.addVertex("B"); graph.addVertex("C");
		 * graph.addVertex("D"); graph.addVertex("E");
		 */

		graph.addEdge("A", "B", new double[] { 2.0, 0.0 }, true);
		graph.addEdge("A", "C", new double[] { 1.0, 0.0 }, true);
		graph.addEdge("B", "D", new double[] { 5.0, 0.0 }, true);
		graph.addEdge("B", "C", new double[] { 3.0, 0.0 }, true);
		graph.addEdge("D", "E", new double[] { 1.0, 0.0 }, true);
		graph.addEdge("C", "E", new double[] { 2.0, 0.0 }, true);

		ArrayList<Graph> paths = graph.yen("A", "E", 3, DISTANCE);
		System.out.println("------------");
		paths.get(0).printEdges();
		System.out.println("------------");
		paths.get(1).printEdges();
		System.out.println("------------");
		paths.get(2).printEdges();


	}
}