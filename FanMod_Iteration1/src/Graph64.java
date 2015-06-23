import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;



import Typedef.Edge;
import Typedef.EdgeType;
import Typedef.Variables;
import Typedef.Vertex;


public class Graph64
{	
	/**
	 * @param data
	 */
	public Graph64(BigInteger data)
	{
		super();
		this.data = BigInteger.ONE;
		this.data = data;
	}

	/**
	 * 
	 */
	public Graph64()
	{
		super();
	}
//	public long data;
	public BigInteger data;

	public static final EdgeType NOEDGE_UV = new EdgeType((short)0);
	public static final EdgeType  DIR_U_T_V = new EdgeType((short) 1);
	public static final EdgeType  DIR_V_T_U = new EdgeType((short) 2);
	public static final EdgeType  UNDIR_U_V = new EdgeType((short)3);
	
	public static final short INDEG = 0;
	public static final short OUTDEG = 1;
	public static final Vertex NILLVERTEX = new Vertex(4294967295L);


	public Edge new_edge(Vertex u, Vertex v)
	{
		Edge edge = new Edge();
		edge.data = BigInteger.valueOf((u.data << 32) | v.data);
		
	    return edge;
	}

	Edge edge_code(Vertex u, Vertex v)
	{
		Edge edge = new Edge();
	    if (u.data < v.data) {
	    	//edge.data = (u.data << 32) | v.data;
	    	edge.data = BigInteger.valueOf(((u.data << 32) | v.data));
	    } else {
	    	edge.data = BigInteger.valueOf(((v.data << 32) | u.data));
	    }	    
	    return edge;
	}

	Vertex edge_get_u(Edge e)
	{
	    //return new Vertex(e.data >> 32);
		return new Vertex(Long.parseUnsignedLong((e.data.shiftRight(32)).toString()));
	}

	Vertex edge_get_v(Edge e)
	{
	    //return new Vertex(e.data & 4294967295L);
		BigInteger mask = new BigInteger("4294967295");
		return new Vertex(Long.parseUnsignedLong(e.data.and(mask).toString()));
	}

	EdgeType reverse(EdgeType et)
	{
		EdgeType edgeType = new EdgeType();
		edgeType.data = (short) ((et.data >> 1) | ((et.data << 1) & 2));
	    return edgeType;
	}

	public void adj(Graph64 g) {
		short shift;
		for (int i= 0; i!=8 ; ++i) {
			for (int j= 0; j!=8 ; ++j) {
				shift = (short)(63 - i*8 -j);
				//System.out.println ((g.data>>shift)&1);
				BigInteger mask = BigInteger.ONE;
				System.out.println((g.data.shiftRight(shift)).and(mask));
			}
			System.out.println("\n");
		}
		System.out.println("\n");
		return;
	}

	public void DEL(Graph64 g, long row, long col) 
	{
		//g.data &= ~(1L << (63-(row*8+col)));
		long mask = Long.parseUnsignedLong((~(1L << (63-(row*8+col))))+"");
		
		BigInteger mask1 = BigInteger.valueOf(mask);
		g.data = g.data.and(mask1);
	}

	public void SET(Graph64 g, long row, long col) 
	{
		//g.data |=  (1L << (63-(row*8+col)));
		
		long mask = Long.parseUnsignedLong((1L << (63-(row*8+col)))+"");
		BigInteger mask1 = BigInteger.valueOf(mask);
		g.data = g.data.or(mask1);
	}
	
	
	public void read_graph(String filename, Variables variable)
	{
	    //FILE in = fopen(filename, "r");
		File file = new File(filename);
		System.out.println(" - Reading File \'" + filename + "\'." + "\n");
		final String numbers = "0123456789";
		variable.n = 0;
		variable.num_nodes =0;
		variable.num_lonely_nodes = 0;
		variable.num_single_edges = 0;
		variable.num_mutual_edges = 0;
					
		boolean error_flag = false;
		Vertex u = null;
		Vertex v = null;
		
		//long weight;
		long linenumber = 0;
		Edge e;
		int flag;
		
		if(file.exists())
		{
			//Zalak : Different way of reading the file . 
			Scanner scanner;
			try
			{
				scanner = new Scanner(file);
				while(scanner.hasNext())
				{
					//Zalak : reading as long in original it is read as int. 
					//Zalak: u v can be reused.
					//Zalak : Assuming reading the file is successful.
					//Zalak : removing weight as weight will not be specified in our code.
					u = new Vertex(scanner.nextLong());
					
					v= new Vertex(scanner.nextLong());	
					//weight = scanner.nextLong();
					
					if (u.data > variable.n) {
						variable.n = u.data;
					}
					if(v.data > variable.n)
					{
						variable.n = v.data;
					}
					
					//resizing the arraylist
					if(variable.num_neighbours.length <= variable.n)
					{
						variable.num_neighbours = Arrays.copyOf(variable.num_neighbours, (int) variable.n+1);
					}
					
					if(u.data != v.data)
					{
						if(u.data < v.data)
						{
							e = new_edge(u, v);
						
							if(!variable.edges.containsKey(e))
							{
								int index = (int)u.data;
								variable.num_neighbours[index]++;
								
								index = (int)v.data;
								variable.num_neighbours[index]++;
								
								variable.edges.put(e, DIR_U_T_V);
							}
							else
							{
								EdgeType et = variable.edges.get(e);
								variable.edges.put(e, new EdgeType((short) (et.data|DIR_U_T_V.data)));
							}
							
							if(!variable.directed)
							{
								variable.edges.put(e, UNDIR_U_V);
							}
						}
						//if v > u
						else
						{
							e = new_edge(v, u);
							if(!variable.edges.containsKey(e))
							{
								int index = (int)u.data;
								variable.num_neighbours[index]++;
								
								index = (int)v.data;
								variable.num_neighbours[index]++;
								
								variable.edges.put(e, DIR_V_T_U);
							}
							else
							{
								EdgeType et = variable.edges.get(e);
								variable.edges.put(e, new EdgeType((short) (et.data|DIR_V_T_U.data)));
							}
							
							if(!variable.directed)
							{
								variable.edges.put(e, UNDIR_U_V);
							}
						}		
					}
				}
				scanner.close();
			}
			catch (FileNotFoundException e1)
			{
				System.out.println("ERROR: File does not exist " + filename);
			}
		}
			else
			{
				System.out.println("ERROR: File does not exist " + filename);
				return;
			}
		    ++variable.n;
		    variable.m = variable.edges.size();
	}
	
//	public static void main(String args[])
//	{
//		new Graph64().read_graph("Scere2014Oct_index.txt", new Variables());
//	}

	
}
