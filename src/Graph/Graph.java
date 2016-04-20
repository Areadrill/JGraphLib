package Graph;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Graph {
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
		
		Graph clone = new Graph();
		HashMap<String, Double> values = new HashMap<String, Double>();
		HashMap<String, String> previous = new HashMap<String, String>();
		VComparator comp = new VComparator(values);
		PriorityQueue<String> pq = new PriorityQueue<String>(this.getNumVertex(), comp);
		
		for(Vertex v: this.vertex){
			values.put(v.getIdentifier(), Double.MAX_VALUE);
			values.put(v.getIdentifier(), null);
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
					else{
						
					}
				}
			}
		}
		
		return null;
		
		
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
	
	
	public static void main(String args[]){
		Graph testG = new Graph(true);
		
		testG.addEdge("A", "B", 1.5, true);
		testG.addEdge("A", "C", 2.0, true);
		testG.addEdge("B", "C", 1.0);
		testG.addEdge("B", "C", 1.0);
		testG.removeEdge("A", "B");
		System.out.println(testG);
	}
}
