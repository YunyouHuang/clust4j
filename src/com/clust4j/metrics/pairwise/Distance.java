package com.clust4j.metrics.pairwise;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.math3.util.FastMath;

import com.clust4j.utils.VecUtils;

public enum Distance implements DistanceMetric, java.io.Serializable {
	HAMMING {
		
		@Override
		public double getPartialDistance(final double[] a, final double[] b) {
			VecUtils.checkDims(a, b);
			
			final int n = a.length;
			double ct = 0;
			for(int i = 0; i < n; i++)
				if(a[i] != b[i])
					ct++;
			
			return ct / n;
		}
		
		@Override
		final public double getP() {
			return DEFAULT_P;
		}
		
		@Override
		public String getName() {
			return "Hamming";
		}

		@Override public boolean isBinaryDistance() { return true; }
	},
	
	MANHATTAN {
		
		@Override 
		public double getPartialDistance(final double[] a, final double[] b) {
			VecUtils.checkDims(a,b);
			
			double sum = 0;
			for(int i = 0; i < a.length; i++) {
				double diff = a[i] - b[i];
				sum += FastMath.abs(diff);
			}
			
			return sum;
		}
		
		@Override
		final public double getP() {
			return 1.0;
		}
		
		@Override
		public String getName() {
			return "Manhattan";
		}

		@Override public boolean isBinaryDistance() { return false; }
	},
	
	
	
	EUCLIDEAN {
		
		@Override
		public double distanceToPartialDistance(final double d) {
			return d * d;
		}
		
		@Override
		final public double getP() {
			return 2.0;
		}
		
		@Override
		public double getPartialDistance(final double[] a, final double[] b) {
			VecUtils.checkDims(a,b);
			
			double sum = 0;
			for(int i = 0; i < a.length; i++) {
				// Don't use math.pow -- too expensive
				double diff = a[i]-b[i];
				sum += diff * diff;
			}
			
			return sum;
		}
		
		@Override
		public double partialDistanceToDistance(double d) {
			return FastMath.sqrt(d);
		}
		
		@Override
		public String getName() {
			return "Euclidean";
		}

		@Override public boolean isBinaryDistance() { return false; }
	},
	
	
	BRAY_CURTIS {
		
		@Override
		public double getPartialDistance(final double[] a, final double[] b) {
			VecUtils.checkDims(a,b);
			
			final int n = a.length;
			double sum_1 = 0, sum_2 = 0;
			for(int i = 0; i < n; i++) {
				sum_1 += FastMath.abs(a[i] - b[i]);
				sum_2 += FastMath.abs(a[i] + b[i]);
			}
			
			return 0 == sum_1 ? 0 : nanInf(sum_1 / (sum_2));
		}
		
		@Override
		final public double getP() {
			return DEFAULT_P;
		}
		
		@Override
		public String getName() {
			return "BrayCurtis";
		}

		@Override public boolean isBinaryDistance() { return false; }
	},
	
	
	CANBERRA {
		
		@Override
		public double getPartialDistance(final double[] a, final double[] b) {
			VecUtils.checkDims(a,b);
			
			final int n = a.length;
			double sum=0, numer;
			for(int i = 0; i < n; i++) {
				numer = FastMath.abs(a[i] - b[i]);
				sum += 0 == numer ? 0 : nanInf( numer / (FastMath.abs(a[i]) + FastMath.abs(b[i])) );
			}
			
			return sum;
		}
		
		@Override
		final public double getP() {
			return DEFAULT_P;
		}
		
		@Override
		public String getName() {
			return "Canberra";
		}

		@Override public boolean isBinaryDistance() { return false; }
	},
	
	
	
	CHEBYSHEV {
		
		@Override
		public double getPartialDistance(final double[] a, final double[] b) {
			VecUtils.checkDims(a, b);
			
			final int n = a.length;
			double max = 0;
			for(int i = 0; i < n; i++) {
				double abs = FastMath.abs(a[i] - b[i]);
				if(abs > max)
					max = abs;
			}
			
			return max;
		}
		
		@Override
		final public double getP() {
			return Double.POSITIVE_INFINITY;
		}
		
		@Override
		public String getName() {
			return "Chebyshev";
		}

		@Override public boolean isBinaryDistance() { return false; }
	},
	
	
	DICE {
		
		@Override
		public double getPartialDistance(final double[] a, final double[] b) {
			BooleanSimilarity bool = BooleanSimilarity.build(a, b);
			double ctt = bool.getFirst(), ctf = bool.getSecond(), cft = bool.getThird();
			
			double numer = (ctf + cft);
			
			// This hack covers the case where all true (1) or all false (0)
			return 0 == numer ? 0 : numer / (2 * ctt + cft + ctf);
		}
		
		@Override
		final public double getP() {
			return DEFAULT_P;
		}
		
		@Override
		public String getName() {
			return "Dice";
		}

		@Override public boolean isBinaryDistance() { return true; }
	},
	
	
	KULSINSKI {
		
		@Override
		public double getPartialDistance(final double[] a, final double[] b) {
			BooleanSimilarity bool = BooleanSimilarity.build(a, b);
			final double ctt = bool.getFirst(), ctf = bool.getSecond(), cft = bool.getThird();
			
			return (ctf + cft - ctt + a.length) / (cft + ctf + a.length);
		}
		
		@Override
		final public double getP() {
			return DEFAULT_P;
		}
		
		@Override
		public String getName() {
			return "Kulsinski";
		}

		@Override public boolean isBinaryDistance() { return true; }
	},
	
	
	ROGERS_TANIMOTO {
		@Override
		public double getPartialDistance(final double[]a, final double[] b) {
			BooleanSimilarity bool = BooleanSimilarity.build(a, b);
			final double ctt = bool.getFirst(), ctf = bool.getSecond(), cft = bool.getThird(), cff = bool.getFourth();
			final double R = 2 * (cft + ctf);
			return 0 == R ? 0 : 
				// Should be impossible to be NaN:
				nanInf(R / (ctt + cff + R));
		}
		
		@Override
		final public double getP() {
			return DEFAULT_P;
		}
		
		@Override
		public String getName() {
			return "RogersTanimoto";
		}

		@Override public boolean isBinaryDistance() { return true; }
	},
	
	
	RUSSELL_RAO {
		@Override
		public double getPartialDistance(final double[] a, final double[] b) {
			// This actually takes 3N and can get expensive...
			/*final double ip = VecUtils.innerProduct(
				BooleanSimilarity.asBool(a), 
				BooleanSimilarity.asBool(b));*/
			
			BooleanSimilarity bool = BooleanSimilarity.build(a, b);			
			final double n = (double)a.length;
			return (n - bool.getFirst()) / n;
		}
		
		@Override
		final public double getP() {
			return DEFAULT_P;
		}
		
		@Override
		public String getName() {
			return "RussellRao";
		}
		
		@Override public boolean isBinaryDistance() { return true; }
	},
	
	
	SOKAL_SNEATH {
		@Override
		final public double getP() {
			return DEFAULT_P;
		}
		
		@Override
		public double getPartialDistance(final double[] a, final double[] b) {
			BooleanSimilarity bool = BooleanSimilarity.build(a, b);
			final double ctt = bool.getFirst(), ctf = bool.getSecond(), cft = bool.getThird();
			final double R = 2 * (cft + ctf);

			// If all values in a and b are 0s, the distance will be NaN.
			// Do we want to call that a distance of positive infinity? Or zero?
			return 0 == R ? 0 : 
				nanInf(R / (ctt + R));
		}
		
		@Override
		public String getName() {
			return "SokalSneath";
		}
		
		@Override public boolean isBinaryDistance() { return true; }
	},
	
	YULE {
		@Override
		final public double getP() {
			return DEFAULT_P;
		}
		
		@Override
		public double getPartialDistance(final double[] a, final double[] b) {
			BooleanSimilarity bool = BooleanSimilarity.build(a, b);
			final double ctt = bool.getFirst(), ctf = bool.getSecond(), cft = bool.getThird(), cff = bool.getFourth();
			final double R = 2 * cft * ctf; // per scipy 0.17. 0.14 had 2 * (cft + ctf)
			
			// If all values in a and b are 0s, the distance will be NaN.
			// Do we want to call that a distance of positive infinity? Or zero?
			if(0 == R)
				return 0;
			
			// Shouldn't ever have a NaN?...
			return nanInf(R / (ctt * cff + cft * ctf));
		}
		
		@Override
		public String getName() {
			return "Yule";
		}
		
		@Override public boolean isBinaryDistance() { return true; }
	}
	
	;
	
	@Override
	public double getDistance(double[] a, double[] b) {
		return partialDistanceToDistance(getPartialDistance(a, b));
	}

	@Override
	public double partialDistanceToDistance(double d) {
		return d;
	}

	@Override
	public double distanceToPartialDistance(double d) {
		return d;
	}
	
	public static Collection<Distance> binaryDistances() {
		final ArrayList<Distance> binary= new ArrayList<>();
		for(Distance d: values()) {
			if(d.isBinaryDistance())
				binary.add(d);
		}
		
		return binary;
	}
	
	private static double nanInf(double d) {
		return Double.isNaN(d) ? Double.POSITIVE_INFINITY : d;
	}
	
	abstract public boolean isBinaryDistance();
}
