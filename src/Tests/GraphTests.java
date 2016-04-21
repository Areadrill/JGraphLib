package Tests;

import static org.junit.Assert.*;

import org.junit.Test;
import Graph.Graph;

public class GraphTests {
	
	@Test
	public void testAddVertex(){
		Graph g = new Graph();
		int priorSize = g.getNumVertex();
		assertTrue(g.addVertex("test"));
		assertEquals((g.getNumVertex() - priorSize), 1);
		assertTrue(g.checkExistsVertex("test"));
		
		assertFalse(g.addVertex("test"));
	}
	
	@Test
	public void testAddEdgeNoforce(){
		Graph g = new Graph();
		assertEquals(g.getNumVertex(), 0);
		assertFalse(g.addEdge("A", "B", 1.0, false));
		
		g.addVertex("A");
		g.addVertex("B");
		assertTrue(g.addEdge("A", "B", 1.0, false));
		assertFalse(g.addEdge("A", "B", 1.0, false));
	}
	
	@Test
	public void testaddEdgeForce(){
		Graph g = new Graph();
		int priorSize = g.getNumVertex();
		assertTrue(g.addEdge("A", "B", 1.0, true));
		assertEquals((g.getNumVertex() - priorSize), 2);
		assertFalse(g.addEdge("A", "B", 1.0, true));
	}
	
	@Test
	public void testEdgeDirectedValues(){
		Graph g = new Graph(true);
		
		g.addEdge("A", "B", 3.0, true);
				
	}
}
