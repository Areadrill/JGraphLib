package Graph;
import java.util.ArrayList;

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
	
	public static void main(String args[]){
		Graph testG = new Graph(true);
		
		testG.addEdge("A", "B", 1.5, true);
		testG.addEdge("A", "C", 2.0, true);
		testG.addEdge("B", "C", 1.0);
		testG.addEdge("B", "C", 1.0);
		System.out.println(testG);
	}
}
