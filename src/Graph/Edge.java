package Graph;

public class Edge {
	private String v1;
	private String v2;
	private String to;
	private String from;
	private boolean directed;
	private double weight;
	
	
	public Edge(String v1, String v2, double weight){
		this(v1, v2, weight, false);
	}
	
	

	public Edge(String v1, String v2, double weight, boolean directed){
		this.directed = directed;
		if(this.directed){
			this.to = v2;
			this.from = v1;
		}
		else{
			this.v1 = v1;
			this.v2 = v2;
		}
		this.weight = weight;
		
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public String getV1() {
		return v1;
	}

	public String getV2() {
		return v2;
	}
	
	public String getTo() {
		return to;
	}

	public String getFrom() {
		return from;
	}
	
	public boolean isDirected() {
		return directed;
	}
	
	public boolean equals(Object obj){
		if(!(obj instanceof Edge)){
			return false;
		}
		
		if(((Edge)obj).isDirected() && this.isDirected()){
			if(((Edge)obj).getTo().equals(this.to) && ((Edge)obj).getFrom().equals(this.from)){
				return true;
			}
			return false;
		}
		else if(!((Edge)obj).isDirected() && !this.isDirected()){
			if( (((Edge)obj).getV1().equals(this.v1) && ((Edge)obj).getV2().equals(this.v2)) || (((Edge)obj).getV1().equals(this.v2) && ((Edge)obj).getV2().equals(this.v1)) ){
				return true;
			}
			return false;
		}
		return false;
	}	
}
