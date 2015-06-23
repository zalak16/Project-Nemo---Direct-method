import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Random;

import Typedef.Edge;
import Typedef.EdgeType;
import Typedef.Variables;
import Typedef.Vertex;


public class MFind
{
	
	static int digits(BigInteger data)
	{
		int ret = 0;
		while (!data.equals(BigInteger.ZERO))
		{
			//data /= 10;++ ret;
			data = data.divide(BigInteger.valueOf(10));
			++ret;
		}
		return ret;
	}
	
	
	
	public static void main(String[] args)
	{
		System.out.println("\n-------------------------------------\n");
		System.out.println("\nBenderCalc (2005, Sebastian Wernicke)\n");
		System.out.println("\n-------------------------------------\n");
		
		// Process flags and check correct usage
		System.out.println("1. Process Input Parameters and Initialize\n\n");
		
		//boolean directed = true;
		Variables var = new Variables();
		//long SMPLS =  1000000; //uint64

		long currentTime = System.currentTimeMillis();
		Random rand = new Random(currentTime);
		Init init = new Init();
		int ret = init.process_flags(args.length, args, var);
		if(ret != -1)
		{
			return;
		}
		
		
		//Initialize Graph structure from file
		System.out.println("\n2. Build Graph\n");
//		 long n,m, num_nodes, num_lonely_nodes, num_single_edges, num_mutual_edges;
//		    hash_map < edge, edgetype > edges;
//		    vector <long> num_neighbours;
//		boolean directed = true;
		
		//var.directed = directed;
		Graph64 g= new Graph64();
		try
		{
			g.read_graph(args[0], var);
			
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage() + "\n");
			//return 1;
		}
		
		
		//Build the degree sequence
		System.out.println(" - Building degree sequence for graph with \n");
		System.out.print(var.n + " vertices and " + var.m + " connections \n");
		
		long[] degree_r = new long[(int)var.n];
		long[] degree_c = new long[(int)var.n];
		
		
		for(Edge e : var.edges.keySet())
		{
			Vertex u = g.edge_get_u(e);
			Vertex v = g.edge_get_v(e);
			EdgeType etype = var.edges.get(e); 
			
			switch(etype.data)
			{
				case 1: //DIR_U_T_V
					degree_r[(int) u.data]++; 
					degree_c[(int)v.data]++;
					break;

				case 2: //DIR_V_T_U
					degree_r[(int)v.data]++;
					degree_c[(int)u.data]++;
					break;
					
				case 3: //UNDIR_U_V
					degree_r[(int)u.data]++; degree_c[(int)v.data]++;
					degree_r[(int)v.data]++; degree_c[(int)u.data]++;
					break;
			}
		}
		
		//##################################################################################################
		
		
		System.out.println("\n 3. Sampling \n");
		
		int total_num_subgraphs_undir[] = {1,1,1,2,6,21,112,853,11117,261080};
		
		long num_samples = var.SMPLS;
		Graph64 subgraph_library3_undir[] = {new Graph64(new BigInteger("78")), new Graph64(new BigInteger("238"))};
	    
		Graph64 subgraph_library4_undir[] = {new Graph64(new BigInteger("4382")),new Graph64(new BigInteger("4932")),
											new Graph64(new BigInteger("4958")),new Graph64(new BigInteger("13260")),
											new Graph64(new BigInteger("13278")),new Graph64(new BigInteger("31710"))}; //graph64

		//Undirected Graph
		Bender bender = new Bender(var.n,degree_r,rand);
		double elapsed = 0;
		long start_time = System.currentTimeMillis();//clock_t start_time(clock());
		
		if (var.G_N == 3)
		{
			double[] res = new double[2];
			for (int i = 0; i!= 2; ++i)
			{
				System.out.println("\n   Motif " + (i+1)  + " of 2");
//				cout.flush();
				res[i] = bender.motif_sample_log(subgraph_library3_undir[i],(short) 3,num_samples);
				/*for (int h = 0; h != (14 + digits(BigInteger.valueOf((i+1)))); ++h)
					System.out.print("\b"); //cout << '\b';*/
			}
			
			elapsed = (double) (System.currentTimeMillis() - start_time) / 1000; //Clocks_Per_sec = 1000
			//cout.precision(3);
			System.out.println(" - Overall time: " + elapsed + " seconds  \n");
			DecimalFormat df = new DecimalFormat("#.###");
			System.out.println(" - time per sample: " + df.format((elapsed/(double)(total_num_subgraphs_undir[var.G_N]))/(double)(num_samples)*1000000.0)+ " microseconds \n");
			
			double max = -50000000.0;
			for (int i = 0; i!= 2; ++i) 
			{
				if (res[i] > max && !(res[i] != res[i]))  //test for NaN!
				{
					max = res[i];	
				}
			}
			
			double sum = 0.0;
			for (int i = 0; i!= 2; ++i) 
			{
				if (!(res[i] != res[i]))  //test for NaN!
				{
					//cout << res[i];
					/*
					 * In bender.cpp they are playing with log 
					 *so suppose log 10 to base e = 1.4
					 *then e raise to 1.4 = 10
					 * log 8 to base 2 = 3 then 2 raise to 3 = 8
					 * res[i] = Math.exp(res[i] - max) they are doing the similar thing
					 */
					res[i] = Math.exp(res[i] - max); 
					System.out.println("res[" + i + "] = " + res[i]);
					sum += res[i];
				}
			}
			
			System.out.println("4. Results: \n");
			
			for(int i=0; i!= 2; ++i)
			{
				System.out.print(" Motif " + df.format((3 - (digits(subgraph_library3_undir[i].data)))) + " " + subgraph_library3_undir[i].data + " : ");
				if(!(res[i] != res[i]))
				{
					System.out.println(df.format((res[i]/sum)) + " \n");
					//System.out.println((res[i]/sum) + " \n");
				}
				else
				{
					System.out.println("Not found\n");
				}
			}
			
		}	
		
		if(var.G_N == 4)
		{
			double[] res = new double[6];
			for(int i =0; i!=6 ; ++i)
			{
				System.out.println("\n Motif " + (i+1) + " of 6");
				
				res[i] = bender.motif_sample_log(subgraph_library4_undir[i], (short)4, num_samples);
				
				for(int h= 0; h!= (14 + digits(new BigInteger(Integer.toString((i+1))))); ++h)
				{
					System.out.print("\b");
				}
				
			}
				DecimalFormat df = new DecimalFormat("#.###");
				elapsed= (double)(System.currentTimeMillis() - start_time) / 1000; //CLOCKS_PER_SEC;
				
				System.out.println(" - Overall time: " + elapsed + " seconds  \n");
				System.out.println(" - time per sample: " + df.format((elapsed/(double)(total_num_subgraphs_undir[var.G_N]))/(double)(num_samples)*1000000.0) + " microseconds \n");
				
				double max = -50000000.0;
				
			//	for(int i= 0; i!=13; ++i)
				for(int i= 0; i!=6; ++i)
				{
					if(res[i] > max && !(res[i] != res[i]))
					{
						max = res[i];
					}
				}
				double sum = 0.0;
				
				for(int i=0; i!=6; ++i)
				{
					if(!(res[i] != res[i]))
					{
						res[i] = Math.exp(res[i] - max);
						sum += res[i];
					}
				}
				
				System.out.println("\n  4.Results: \n");
				
				for(int i=0; i!=6; ++i)
				{
					System.out.println("\n Motif " + /*(5-(digits(subgraph_library4_undir[i].data)))*/  " " + subgraph_library4_undir[i].data + " : ");
					
					if(!(res[i] != res[i]))
					{
						System.out.println(df.format(res[i]/ sum) + " \n");
					}
					else
					{
						System.out.println("Not found \n");
					}
					
				}
				
			}
		//return 0;
		long endTime = System.currentTimeMillis();
		System.out.println("Total time taken for samling " + var.G_N + "size motif : " + (endTime - start_time) );
	}	
}
