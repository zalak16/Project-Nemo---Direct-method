import Typedef.Variables;


public class Init
{

	public int process_flags(int length, String[] args, Variables v)
	{
		if(length < 2)
		{
			System.out.println("ERROR: Usage is \'" + args[0] + " <infile> [options]\' \n");
			
			System.out.println(" Call \'" + args[0] + " -- help \' for more information. \n");
			
			return 1;
		}
		v.directed =true;
		
		for(int i = 1; i!=length; ++i)
		{
			String s = args[i];
			
			if(s.equals("-s"))
			{
				++i;
				if(length == i) 
				{
					System.out.println("Error: Flag \'-s\' expects number to follow. \n" );
					return 1;
				}
				//String t = args[i];
			    //stringstream ss(t);
				//ss >> G_N;
				v.G_N = Short.parseShort(args[i]);
				//if (ss.fail() || G_N < 3 || G_N > 4) {
				if(v.G_N < 3 || v.G_N> 4)
				{
					System.out.println(    "ERROR: Expected number between 3 and 4 to follow flag \'-s\' \n");
						return 1;
				}
					
			}
			else if(s.equals("-t"))
			{
				++i;
				if(length == i)
				{
					System.out.println("ERROR: Flag \'-t\' expects number to follow.\n");
					return 1;
				}
				
				v.SMPLS = Long.parseLong(args[i]);
				if(v.SMPLS < 0)
				{
					System.out.println("ERROR: Expected number > 0 to follow flag \'-s\' \n");
					return 1;
				}
			}
			else if(s.equals("-h") || s.equals("--help") || s.equals(" -help"))
			{
				System.out.println("Usage of the program is \'" + args[0] +  " <infile> [options]\' \n");
				System.out.println("Recognized option flags are: \n");
				System.out.println("  -s <int>       size of subgraphs to sample (3-4)    \n");
				System.out.println("  -directed      input graph is directed              \n");
				System.out.println("  -d             equivalent to -directed              \n");
				System.out.println("  -undirected    input graph is undirected            \n");
				System.out.println("  -nondirected   equivalent to -undirected            \n");
				System.out.println("  -nd            equivalent to -undirected            \n");
				System.out.println("  -ud            equivalent to -undirected            \n");
				System.out.println("  -t <int>       number of samples                    \n");
				System.out.println("  -h             display this help                    \n");
				System.out.println("  -help          equivalent to -h                     \n");
				System.out.println("  --help         equivalent to -h             \n");
				System.out.println("Unspecified options imply default values.     \n");
				return 0;
			}
			else if (s.equals("-directed") || s.equals("-d")) 
			{
			    v.directed = true;
			} 
			else if (s.equals("-undirected") || s.equals("-nondirected")
				   || s.equals("-nd") || s.equals("-ud")) 
			{
			    v.directed = false;
			}
			else if (i != 1) 
			{
			    System.out.println("ERROR: Unrecognized flag \'" + s + "\'. Call \'" + args[0] + " --help\' for more " + "information.\n");
			    return 1;
			}
		    
		}
		
		System.out.println(" - Subgraph size = " + v.G_N + "\n");
		System.out.println(" - Number of samples per subgraph = " + v.SMPLS + "\n");
	    System.out.println(" - Input graph is " + (v.directed == true ? "" : "un") +  "directed " + "\n");
		return  -1;
	}

}
