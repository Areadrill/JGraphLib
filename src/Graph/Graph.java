package Graph;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Graph {
	private static final boolean DIRECTED = true;
	private static final boolean UNDIRECTED = false;
	private ArrayList<Vertex> vertex;
	private boolean directed;
	
	public Graph(){
		this(false);
	}
	
	public Graph(boolean directed){
		this.vertex = new ArrayList<Vertex>();
		this.directed  = directed;
	}
	
	public Graph(Graph toClone){
		this.vertex = toClone.vertex;
		this.directed = toClone.directed;
	}
	
	public boolean addVertex(String id){
		if(id == null)
			return false;
		if(!vertex.contains(new Vertex(id))){
			return vertex.add(new Vertex(id));
		}
		return false;
	}
	
	public void addEdge(String v1, String v2, double w){
		addEdge(v1, v2, w, false);
	}
	
	public boolean addEdge(String v1, String v2, double w, boolean force){
		if(!this.vertex.contains(new Vertex(v1))){
			if(force){
				this.addVertex(v1);
			}
			else{
				return false;
			}
		}
		if(!this.vertex.contains(new Vertex(v2))){
			if(force){
				this.addVertex(v2);
			}
			else{
				return false;
			}
		}
		
		if(this.vertex.get(this.vertex.indexOf(new Vertex(v1))).getEdges().contains(new Edge(v1, v2, w, this.directed))){
			return false;
		}
		
		this.vertex.get(this.vertex.indexOf(new Vertex(v1))).getEdges().add(new Edge(v1, v2, w, this.directed));
		this.vertex.get(this.vertex.indexOf(new Vertex(v2))).getEdges().add(new Edge(v1, v2, w, this.directed));
		return true;
		
	}
	
	public boolean removeEdge(String v1, String v2){
		if(v1 == null || v2 == null){
			return false;
		}
		
		else if(!this.vertex.contains(new Vertex(v1)) || !this.vertex.contains(new Vertex(v2))){
			return false;
		}
		
		this.vertex.get(this.vertex.indexOf(new Vertex(v1))).getEdges().remove(new Edge(v1, v2, 0.0, this.directed));
		this.vertex.get(this.vertex.indexOf(new Vertex(v2))).getEdges().remove(new Edge(v1, v2, 0.0, this.directed));
		return true;
	}
	
	public boolean removeEdge(Edge e){
		if(e == null){
			return false;
		}
		if(this.directed){
			return removeEdge(e.getTo(), e.getFrom());
		}
		else{
			return removeEdge(e.getV1(), e.getV2());
		}
	}
	
	public boolean removeVertex(String id){
		if(id == null){
			return false;
		}
		
		for(Edge e: this.vertex.get(this.vertex.indexOf(id)).getEdges()){
			this.removeEdge(e);
		}
		
		return this.vertex.remove(id);
		
	}
	
	public Graph rangedDfs(String id, double range){
		Graph g = new Graph(this.directed);
		g.addVertex(id);
		return (this.directed)?rangedDfsDirected(id, range, 0.0, g):rangedDfsUndirected(id, range, 0.0, g, null);
	}
	
	public Graph rangedDfsDirected(String id, double range, double accumulator, Graph resultGraph){
		if(accumulator >= range)
			return resultGraph;
		
		for(Edge e: this.vertex.get(this.vertex.indexOf(new Vertex(id))).getEdges()){
			if(e.getTo().equals(id))
				continue;
			else if(e.getFrom().equals(id) && e.getWeight()+accumulator < range){
				resultGraph.addVertex(e.getTo());
				resultGraph = rangedDfsDirected(e.getTo(), range, accumulator+e.getWeight(), resultGraph);
			}		
		}
		
		return resultGraph;
	}
	
	public Graph rangedDfsUndirected(String id, double range, double accumulator, Graph resultGraph, Vertex previous){
		if(accumulator >= range)
			return resultGraph;
		
		for(Edge e: this.vertex.get(this.vertex.indexOf(new Vertex(id))).getEdges()){
			if( (e.getV1().equals(id) && (previous != null)?(previous.equals(new Vertex(e.getV2()))):false) || (e.getV2().equals(id) && (previous != null)?(previous.equals(new Vertex(e.getV1()))):false) ){
				continue;
			}
			else if(e.getV1().equals(id) && e.getWeight()+accumulator < range){
				resultGraph.addVertex(e.getV2());
				resultGraph = rangedDfsUndirected(e.getV2(), range, e.getWeight()+accumulator, resultGraph, this.vertex.get(this.vertex.indexOf(new Vertex(id))));
			}
			else if(e.getV2().equals(id) && e.getWeight()+accumulator < range){
				resultGraph.addVertex(e.getV1());
				resultGraph = rangedDfsUndirected(e.getV1(), range, e.getWeight()+accumulator, resultGraph, this.vertex.get(this.vertex.indexOf(new Vertex(id))));
			}
		}	
		return resultGraph;
	}	
	
	public String toString(){
		StringBuilder builder = new StringBuilder();
		for(Vertex v: vertex){
			builder.append(v.getIdentifier() + ": ");
			for(Edge e: v.getEdges()){
				if(this.directed && e.getFrom().equals(v.getIdentifier())){
					builder.append(e.getTo() + ", ");
				}
				else if(!this.directed){
					if(e.getV1().equals(v.getIdentifier())){
						builder.append(e.getV2() + ", ");
					}
					else{
						builder.append(e.getV1() + ", ");
					}
				}	
			}
			builder.append("\b\b\n");
		}
		return builder.toString();
	}
	
	public int getNumVertex(){
		return this.vertex.size();
	}
	
	public boolean checkExistsVertex(String id){
		return this.vertex.contains(new Vertex(id));
	}
	
	public Graph dijkstra(String id){
		if(!this.vertex.contains(new Vertex(id))){
			return null;
		}
		
		Graph dijkstraGraph = new Graph(this.directed);
		HashMap<String, Double> values = new HashMap<String, Double>();
		HashMap<String, String> previous = new HashMap<String, String>();
		VComparator comp = new VComparator(values);
		PriorityQueue<String> pq = new PriorityQueue<String>(this.getNumVertex(), comp);
		
		for(Vertex v: this.vertex){
			values.put(v.getIdentifier(), Double.MAX_VALUE);
			previous.put(v.getIdentifier(), null);
		}
		
		values.remove(id);
		values.put(id, 0.0);
		
		comp.update(values);
		
		pq.add(id);
		while(!pq.isEmpty()){
			for(Edge e: this.vertex.get(this.vertex.indexOf(new Vertex(pq.peek()))).getEdges()){
				if(this.directed && e.getFrom().equals(pq.peek())){
					if(values.get(e.getTo()) >= e.getWeight()+values.get(e.getFrom())){
						
						values.remove(e.getTo());
						values.put(e.getTo(), e.getWeight()+values.get(e.getFrom()));
						previous.remove(e.getTo());
						previous.put(e.getTo(), e.getFrom());
						if(pq.contains(e.getTo())){
							pq.remove(e.getTo());
						}
						comp.update(values);
						pq.add(e.getTo());
					}
				}
				else if(!this.directed){
					if(e.getV1().equals(pq.peek()) && (previous.get(pq.peek()) == null)?true:!previous.get(pq.peek()).equals(e.getV2())){
						if(values.get(e.getV2()) >= e.getWeight()+values.get(e.getV1())){
							values.remove(e.getV2());
							values.put(e.getV2(), e.getWeight()+values.get(e.getV1()));
							previous.remove(e.getV2());
							previous.put(e.getV2(), e.getV1());
							if(pq.contains(e.getV2())){
								pq.remove(e.getV2());
							}
							comp.update(values);
							pq.add(e.getV2());
						}
					}
					else if(e.getV2().equals(pq.peek()) && !previous.get(pq.peek()).equals(e.getV1())){
						if(values.get(e.getV1()) >= e.getWeight()+values.get(e.getV2())){
							values.remove(e.getV1());
							values.put(e.getV1(), e.getWeight()+values.get(e.getV2()));
							previous.remove(e.getV1());
							previous.put(e.getV1(), e.getV2());
							if(pq.contains(e.getV1())){
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
		
		for(Vertex v: this.vertex){
			dijkstraGraph.addVertex(v.getIdentifier());
			if(previous.get(v.getIdentifier()) != null){
				dijkstraGraph.addEdge(previous.get(v.getIdentifier()), v.getIdentifier(), 0.0, true);
			}
		}
		
		return dijkstraGraph;
		
		
	}
	
	private class VComparator implements Comparator<String>{
		HashMap<String, Double> values;
		public VComparator(HashMap<String, Double> vals){
			this.values = vals;
		}
		
		public void update(HashMap<String, Double> vals){
			this.values = vals;
		}
		
		@Override
		public int compare(String v1, String v2) {
			return ((int) (this.values.get(v1) - this.values.get(v2)));
		}
		
	}
	
	public void printEdges(){
		for(Vertex v: this.vertex){
			for(Edge e: v.getEdges()){
				System.out.println(e.getV1() + ((this.directed)?"-->":"---") + e.getV2());
			}
		}
		System.out.println("\n\n\n");
	}
	
	
	public ArrayList<Vertex> getVertex() {
		return vertex;
	}

	public static void main(String args[]){
		Graph testG = new Graph(UNDIRECTED);
		
		/*testG.addEdge("A", "C", 10.0, true);
		testG.addEdge("A", "D", 3.0, true);
		testG.addEdge("D", "C", 4.0, true);
		testG.addEdge("C", "B", 20.0, true);
		testG.addEdge("B", "E", 4.0, true);
		testG.addEdge("B", "F", 4.0, true);
		testG.addEdge("B", "E", 4.0, true);
		testG.addEdge("E", "F", 4.0, true);*/
		
		testG.addEdge("A", "B", 10.0, true);
		testG.addEdge("B", "C", 20.0, true);
		testG.addEdge("B", "D", 5.0, true);
		testG.addEdge("D", "C", 5.0, true);
		
		System.out.println(testG);
		
		//System.out.println(testG.getVertex().indexOf("A"));
		
		System.out.println("\n\n\n" + testG.rangedDfs("A", 30.0));
		
		//testG.printEdges();
		
		Graph otherG = testG.dijkstra("A");
		//System.out.println(otherG);
		
	}
}
