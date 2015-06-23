package Typedef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

public class Variables
{
	//Varibles defined in mFind.cpp and used in readFile
	public HashMap<Edge, EdgeType> edges;
	//public ArrayList<Long> num_neighbours;
	public long[] num_neighbours;
	public long n;
	public long m;
	public long num_nodes;
	public long num_lonely_nodes;
	public long num_single_edges;
	public long num_mutual_edges;
	public boolean directed;
	public long SMPLS;
	public short G_N;
	
	public Variables()
	{
		edges = new HashMap<Edge, EdgeType>();
		num_neighbours = new long[5];
		n =0;
		m =0; 
		num_nodes =0;
		num_lonely_nodes = 0;
		num_single_edges = 0;
		num_mutual_edges = 0;
		directed = true;
		SMPLS = 1000000;
		G_N = 3;
	}
	

	
	
}
 