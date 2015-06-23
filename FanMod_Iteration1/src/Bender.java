
import java.lang.Object;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import Util.LogGamma;
public class Bender
{
	public static final double EXP =  2.71828182845904523536028747135;
	
	public final long MSKSEG[] = {  0x0000000000000000L,
									0xFF00000000000000L,
									0x00FF000000000000L,
									0x0000FF0000000000L,
									0x000000FF00000000L,
									0x00000000FF000000L,
									0x0000000000FF0000L,
									0x000000000000FF00L,
									0x00000000000000FFL};
	
	public final long DELSEG[] = {  0x0000000000000000L,
									0x0000FFFFFFFFFFFFL,
									0xFF0000FFFFFFFFFFL,
									0xFFFF0000FFFFFFFFL,
									0xFFFFFF0000FFFFFFL,
									0xFFFFFFFF0000FFFFL,
									0xFFFFFFFFFF0000FFL,
									0xFFFFFFFFFFFF0000L};
	
	public final long MSKBIT[] = {  0x0000000000000000L,
									0x8080808080808080L,
									0x4040404040404040L,
									0x2020202020202020L,
									0x1010101010101010L,
									0x0808080808080808L,
									0x0404040404040404L,
									0x0202020202020202L,
									0x0101010101010101L};

	public final long DELBIT[] = {  0x0000000000000000L,
									0x3F3F3F3F3F3F3F3FL,
									0x9F9F9F9F9F9F9F9FL,
									0xCFCFCFCFCFCFCFCFL,
									0xE7E7E7E7E7E7E7E7L,
									0xF3F3F3F3F3F3F3F3L,
									0xF9F9F9F9F9F9F9F9L,
									0xFCFCFCFCFCFCFCFCL};
	
	private boolean directed;
	private long n;
	private long[]r;
	private long []c;
	private double b;
	//private long f;
	private BigInteger f;
	private double[] logfac_r;
	private double[] logfac_c;
    private double[] logfac_f;
	private double[] logfac_cache;
	//private long sqsum_r;
	private BigInteger sqsum_r;
	//private long sqsum_c;
	private BigInteger sqsum_c = BigInteger.ZERO;
	//private long maxdeg;
	private BigInteger maxdeg = BigInteger.ZERO;
	private LogGamma lgamma;
	//randlib::rand rand;
	Random rand;
	
	public Bender(long n, long[] r, Random rand) {
	      this.directed = false;
		  this.n = n;
		  this.r = r;
		  this.b = 0;
		  this.rand = rand;
		  this.lgamma = new LogGamma();
		  f  = BigInteger.ZERO;
		  sqsum_r = BigInteger.ZERO;
		  logfac_r = new double[(int) n];
		  for (int i = 0; i != n; ++i)
		  {
			  
			logfac_r[i] = lgamma.logGamma((double)(r[i]+1)); 
			//f+=r[i];
			f = f.add(BigInteger.valueOf(r[i]));
			//sqsum_r += r[i] * r[i] - r[i];
			sqsum_r = sqsum_r.add(new BigInteger(Long.toString(r[i] * r[i] - r[i])));
		  }
	    }
		
		int getRandomNumber(int n)
		{
			Random rand = new Random(System.currentTimeMillis());
			return rand.nextInt(n);
		}
		
		 double motif_sample_log(Graph64 motif, short k, long num_samples)
		 {
			 //Init
			 ArrayList<Double> results = new ArrayList<Double>();
			 Vector<Double> result_buffer = new Vector<Double>(); //vector<double> result_buffer;
			 long BUF_SIZE = 1000000; //maximum size of results vector, should be at least 1e6
			 Vector<Short> odg = new Vector<Short>(); //vector<short> 
			 Vector<Short> idg = new Vector<Short>(); //vector<short>
			 
			 noautoms (odg, idg, motif, k, this.directed, results);
			 
			 int num_graphs = odg.size() / k;
			 
			 short[] odeg = new short[k];
			 short[] ideg = new short[k];
			 long[] pos = new long[k];
			 
			 ArrayList<Long> candidate_indices = new ArrayList<Long>(); //vector<uint64>
			 
				for (int i = 0; i!=n; ++i) 
				{
					if (directed)
					{
						for (int j = 0; j!= k; ++j) 
						{ 
							if ((r[i] >= odg.get(j)) && (c[i] >= idg.get(j))) 
							{
								candidate_indices.add((long) i);//candidate_indices.push_back(i);
								break;
							}
						}
					} 
					else
					{
						for (int j = 0; j!= k; ++j) 
						{
							if ((r[i] >= odg.get(j))) 
							{
								candidate_indices.add((long)i); //candidate_indices.push_back(i);
								break;
							}
						}
					}
				}
				
				long new_n = candidate_indices.size();
				
				//cout << "For motif ID " << motif << " we have " << new_n 
				//	 << " candidates out of " << n << " vertices." << endl;
			    boolean[] taken = new boolean[(int)new_n]; // defaul vaue of boolean in java is false
				/*for (int i = 0; i!= new_n; ++i)
				{
					taken[i] = false;
				}*/
				
				//Main sample loop
				double maximum = 0.0;
				long cand; //uint64 cand;
				
				if (new_n >= k) 
				{
					for (int i = 0; i!=num_samples; ++i)
					{

						for (int j=0; j!= k; ++j) 
						{
							do
							{
								cand = rand.nextInt((int)new_n); // Need to change this
								//cand = getRandomNumber((int)new_n);
							} while (taken[(int) cand]);
							taken[(int) cand] = true;
							pos[j] = cand;
						}

						for (int j=0; j!= k; ++j) 
						{
							taken[(int) pos[j]] = false;
							pos[j] = candidate_indices.get((int) pos[j]);//pos[j] = candidate_indices[pos[j]];
						}

						for (int j=0; j!= num_graphs; ++j)
						{
							if (directed) 
							{
							
							}
							else
							{
								for (int l = 0; l!= k; ++l)
								{
									odeg[l] = odg.get(j*k+l); //odg[j*k + l];
								}
								
								calc_motif_undir(pos, odeg, k, results);
							}

							if (results.size() > BUF_SIZE) // (results.size() > BUF_SIZE)
							{
								double sum = 0.0;
								long l = 0;
								//Arrays.sort(results);//sort(results.begin(), results.end());
								Collections.sort(results);
								if (result_buffer.size() == 0) //(result_buffer.size() == 0)
									maximum = results.get(results.size() - 1); //[maximum = results[results.size()-1];
								while (l != BUF_SIZE)
								{
									sum += Math.exp(results.get((int) l)-maximum); //  exp(results[l]-maximum);
									++l;
								}
								
								removeInRange(results, 0, (int)BUF_SIZE); //results.erase(results.begin(),results.begin()+BUF_SIZE);
								result_buffer.add(sum);//result_buffer.push_back(sum);
								
							}
						}
					}
				}
			 
				//apply correctional factor due to vertex selection
				double p = 1.0;

				for (int i = 0; i != k; ++i)
				{
					p *= ((double)(new_n - i))/((double)(n-i)); //double(new_n - i) / double(n-i);
				}
				
			    //cout << "correctional factor " << p << endl;

				//Calculate results
				Collections.sort(results); //sort(results.begin(), results.end());
				double ret = 0.0;
				if (results.size() > 0)
				{
					//cout << "Number of actual samples: " 
					//	 << (result_buffer.size() * BUF_SIZE + results.size()) << endl;
					if(result_buffer.size() == 0)
						maximum = results.get(results.size() - 1); //maximum = results[results.size()-1];
					//double maximum = results[results.size()-1];
					double sum = 0;
					//for (vector<double>::size_type i = 0; i!= results.size(); ++i) {
					Iterator<Double> iter = results.iterator();
					while(iter.hasNext())
					{
						//sum += exp(results[i]-maximum);
						sum+= Math.exp(iter.next() - maximum);
					}

					
					Iterator<Double> iter1 = result_buffer.iterator();
					//for (vector<double>::size_type i = 0; i!= result_buffer.size(); ++i) {
					while(iter1.hasNext())
					{
						//sum += result_buffer[i];
						sum+=iter1.next();
					}

					ret = maximum+Math.log(sum)-Math.log(((double)num_samples)/(p));
				} 
				else 
				{
					ret = Math.sqrt(-1.0);
				}

				//Clean up
				results.clear();
				candidate_indices.clear();
				
				pos = null;
				taken = null;
				odg.clear();
				idg.clear();
				odeg = null;
				ideg = null;
				return ret;
		 }
		 
		 
		 

		 private void removeInRange(ArrayList<Double> results, int begin, int end) 
		 {
			 for(int i=0; i<=results.size(); i++)
			 {
				// if(results.elementAt(i)!= null)
					// results.removeElementAt(i);
				 if(results.get(i) != null)
				 {
					 results.remove(i);
				 }
			 }
		 }

		private void calc_motif_undir(long[] positions, short[] degrees, short k,
				ArrayList<Double> results)
		{
			//cout << "WARNING -- UNSTABLE!!! " << endl;
			//cout.flush();
			//long fn = this.f; // uint64
			BigInteger fn = this.f;
			//long sqn = this.sqsum_r; //uint64
			BigInteger sqn = this.sqsum_r;
			for (int i = 0; i != k ; ++i) {
				if (r[(int) positions[i]] < degrees[i]) {
					//cout << "GA";
					return;
				}
				
				if(positions[i] <= 2)
				{					
					//fn -= degrees[(int) positions[i]]; //Added
					fn = fn.subtract(BigInteger.valueOf(degrees[(int)positions[i]]));
				}
				
				//sqn = sqn - (r[(int) positions[i]]*r[(int) positions[i]] - r[(int) positions[i]]); //Added cast to int
				sqn = sqn.subtract(BigInteger.valueOf((r[(int) positions[i]]*r[(int) positions[i]] - r[(int) positions[i]])));
			}
			
			//double olda = (double)(this.sqsum_r)/(double)(2*this.f);
			double olda = this.sqsum_r.doubleValue()/(2 * this.f.doubleValue());
			//double newa = (double)(sqn)/(double)(2*fn);
			double newa = sqn.doubleValue()/ (2 * fn.doubleValue());
			double oldb = 0;
			long  be = 0; //uint64
			for (int i = 0; i != k ; ++i)
				for (int j = i+1; j < k ; ++j)
					be += r[(int)positions[i]]*r[(int)positions[i]];
		//	double newb = (double)(be) /(double)(fn);
			double newb = (double) (be) / fn.doubleValue();
			double changelog = 0;
			for (int i = 0; i != k ; ++i)
				changelog += logfac_r[(int)positions[i]]- lgamma.logGamma(r[(int)positions[i]] - degrees[i]+1);
			double x = 0;
				x= 	( ((fn.doubleValue())*Math.log((fn.doubleValue())/EXP) - (f.doubleValue())*Math.log((f.doubleValue())/EXP) ) / 2
					 +olda*olda+olda-newa*newa-newa-newb
					 +changelog );
			results.add(x);//result.push_back(x);
			//cout >> x;
			//System.out.println("olda: " + olda + "newa: " + newa + "newb: " + newb + "changelog: " + changelog + "x: " + x);
			return;
			 
		}

		void adj(Graph64 g, short k) {
			short shift;
			for (int i= 0; i!=k ; ++i)
			{
				{
				for (int j= 0; j!=k ; ++j) {
					
					shift = (short) (63 - i*8 -j);
					  //System.out.println((g.data>>shift)&1);//cout << ((g>>shift)&1);
					System.out.println((g.data.shiftRight(shift).and(BigInteger.ONE)));
				}
				System.out.println("\n");
				}
			}
			System.out.println("\n");
			return;
		}
		
		Graph64 getcanonical (final Graph64 g, final short k) {
			Graph64 gr = g;  //register
			//long tmp1; //register
			//long tmp2; //register
			BigInteger tmp1 = BigInteger.ZERO;
			BigInteger tmp2 = BigInteger.ZERO;
			int[] c = new int[k+1]; //register unsigned short
			int[] o = new int[k+1]; //register unsigned short
			short j = k; //register
			short s = 0; //register
			short q; //register
			short t1; //register
			short t2; //register
			Graph64 canon = g; //register

			for (int i = 0; i!=k+1 ; ++i) {	  
				c[i] = 0;	
				o[i] = 1;  
			}	

			while (j != 0) {
				q = (short) (c[j] + o[j]);
				if (q < 0) {
					o[j] = -o[j];	
					--j;
				} else if (q == j) {
					++s;
					o[j] = -o[j];	
					--j;
				} else if (q > -1) {
					t1 = (short) (j-q+s);
					t2 = (short) (j-c[j]+s);
					if (t1 > t2) { 
						t1 ^= t2; 
						t2 ^= t1;
						t1 ^= t2;
					}
					BigInteger mask = BigInteger.valueOf(MSKSEG[t1]);
					//tmp1  =  gr.data & MSKSEG[t1];
					tmp1 = gr.data.and(mask);
					mask = BigInteger.valueOf(MSKSEG[t2]);
					//tmp2  =  gr.data & MSKSEG[t2];
					tmp2 = gr.data.and(mask);
					//gr.data   &=  DELSEG[t1];
					gr.data = gr.data.and(BigInteger.valueOf(DELSEG[t1]));
					//gr.data   |=  (tmp1 >> 8);
					gr.data = gr.data.or(tmp1.shiftRight(8));
					//gr.data   |=  (tmp2 << 8);
					gr.data = gr.data.or(tmp2.shiftLeft(8));
					
					tmp1 = gr.data.and(BigInteger.valueOf(MSKBIT[t1]));
					//tmp1  =  gr.data & MSKBIT[t1];
					tmp2 = gr.data.and(BigInteger.valueOf(MSKBIT[t2]));
					//tmp2  =  gr.data & MSKBIT[t2];
					
					//gr.data   &=  DELBIT[t1];
					gr.data = gr.data.and(BigInteger.valueOf(DELBIT[t1]));
					
					gr.data = gr.data.or(tmp1.shiftRight(1));
					//gr.data   |=  (tmp1 >> 1);
					//gr.data   |=  (tmp2 << 1);
					gr.data = gr.data.or(tmp2.shiftLeft(1));
					// compare to max
					if (gr.data.compareTo(canon.data) == 1) {
						canon = gr;
					}
					//continue algorithm
					if(q >=0 && q <= 65535)  // since c is unsigned short so checking the where q is in the range of unsigned short or not
					{
						c[j] = q;	
					}
					j = k;
					s = 0;
				}
			}	
			c = null; //delete[]c
			o = null; //delete[]o
			return canon;
		}
		

		private void noautoms(Vector<Short> odg, Vector<Short> idg, Graph64 g, short k,
				boolean directed, ArrayList<Double> results)
		{
 			Graph64 gr = new Graph64();
			gr.data = BigInteger.ZERO; //register
			//HashSet<Long> already = new HashSet<Long>();//std::set<graph64> already; it stores data of graph64 not object
			HashSet<BigInteger> already = new HashSet<BigInteger>();
			short[] odeg = new short[k+1];
			short[] ideg = new short[k+1];
			for (int i = 0; i !=k+1 ; ++i)
			{
				odeg[i] = 0;	
				ideg[i] = 0;  
			}
			//Convert graph format
			for (int i = 0; i!= k; ++i) 
			{
				for (int j = 0; j!= k; ++j) 
				{
					//if ((g.data & (1L << i+j*k)) > 0)
					if((g.data.and(BigInteger.ONE.shiftLeft((i+j*k)))).compareTo(BigInteger.ZERO) == 1)
					{
						new Graph64().SET(gr,j,i);
						++ideg[i];
						++odeg[j];
					}
				}
			}
			
			//long tmp1; //register uint64
			BigInteger tmp1 = BigInteger.ZERO;
			//long tmp2; //register uint64
			BigInteger tmp2 = BigInteger.ZERO;
			int[]  c = new int[k+1]; //register unsigned short
			int[] o = new int[k+1]; //register  unsigned short*
			short j = k; //register
			short s = 0; //register
			short q; // register
			short t1; // register
			short t2; //register
		
			for (int i = 0; i!=k+1 ; ++i)
			{	 
				c[i] = 0;	
				o[i] = 1;  
			}	
			already.add(gr.data); //already.insert(gr);
			for (int i = 0; i!= k; ++i)
			{
				idg.add(ideg[i]);
				odg.add(odeg[i]);
			}
			
			while (j != 0)
			{
				q = (short) (c[j] + o[j]);
				if (q < 0) 
				{
					o[j] = -o[j];	
					--j;
				}
				else if (q == j) 
				{
					++s;
					o[j] = -o[j];	
					--j;
				}
				else if (q > -1) 
				{
					t1 = (short) (j-q+s);
					t2 = (short) (j-c[j]+s);
					if (t1 > t2)
					{ 
						t1 ^= t2; 
						t2 ^= t1;
						t1 ^= t2;
					}
					tmp1 = BigInteger.valueOf(ideg[t1-1]);
					ideg[t1-1] = ideg[t2-1];
					ideg[t2-1] =  tmp1.shortValue();
					tmp1 = BigInteger.valueOf(odeg[t1-1]);
					odeg[t1-1] = odeg[t2-1];
					odeg[t2-1] =  tmp1.shortValue();
					
					tmp1 = gr.data.and(BigInteger.valueOf(MSKSEG[t1]));
					//tmp1  =  gr.data & MSKSEG[t1];
					//tmp2  =  gr.data & MSKSEG[t2];
					tmp2 = gr.data.and(BigInteger.valueOf(MSKSEG[t2]));
					//gr.data   &=  DELSEG[t1];
					gr.data = gr.data.and(BigInteger.valueOf(DELSEG[t1]));
					
					gr.data = gr.data.or(tmp1.shiftRight(8));
					//gr.data   |=  (tmp1 >> 8);
					gr.data = gr.data.or(tmp2.shiftLeft(8));
					//gr.data   |=  (tmp2 << 8);
					tmp1 = gr.data.and(BigInteger.valueOf(MSKBIT[t1]));
					tmp2 = gr.data.and(BigInteger.valueOf(MSKBIT[t2]));
					//tmp1  =  gr.data & MSKBIT[t1];
					//tmp2  =  gr.data & MSKBIT[t2];
					gr.data = gr.data.and(BigInteger.valueOf(DELBIT[t1]));
				//	gr.data   &=  DELBIT[t1];
					gr.data = gr.data.or(tmp1.shiftRight(1));
					gr.data = gr.data.or(tmp2.shiftLeft(1));
					//gr.data   |=  (tmp1 >> 1);
					//gr.data   |=  (tmp2 << 1);
					if(!already.contains(gr.data))//if (already.count(gr) == 0)
					{
						already.add(gr.data);
						for (int i = 0; i!= k; ++i)
						{
							idg.add(ideg[i]);
							odg.add(odeg[i]);
						}
					}	
					c[j] = q;
					j = k;
					s = 0;
				}
			}	
			
			c = null; //delete[]
			 o = null; //delete[]
			odeg = null; //delete[]
			ideg = null; //delete[]
			already.clear();
			return;
		}
		
	
	
}
