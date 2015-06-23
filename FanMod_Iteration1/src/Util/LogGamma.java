package Util;

import java.text.DecimalFormat;

public class LogGamma
{
	 public double logGamma(double x)
	 {
	      double tmp = (x - 0.5) * Math.log(x + 4.5) - (x + 4.5);
	      double ser = 1.0 + 76.18009173    / (x + 0)   - 86.50532033    / (x + 1)
	                       + 24.01409822    / (x + 2)   -  1.231739516   / (x + 3)
	                       +  0.00120858003 / (x + 4)   -  0.00000536382 / (x + 5);
	      double val= tmp + Math.log(ser * Math.sqrt(2 * Math.PI));
	      
	      DecimalFormat df = new DecimalFormat("#.######");
	      return Double.valueOf(df.format(val));
	      
	 }
	 
//	 public static void main(String args[])
//	 {
//		 System.out.println(logGamma(0.56));
//	 }
}
