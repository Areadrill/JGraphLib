package Graph;
import java.util.ArrayList;

public class Vertex {
	private String identifier;
	private ArrayList<Edge> edges;
	private int key;
	
	public Vertex(String id){
		this.identifier = id;
		this.edges = new ArrayList<Edge>();
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof Vertex)){
			return false;
		}
		if(((Vertex)obj).getIdentifier().equals(this.identifier)){
			return true;
		}
		return false;
	}

	public String getIdentifier() {
		return identifier;
	}

	public ArrayList<Edge> getEdges() {
		return edges;
	}
}
