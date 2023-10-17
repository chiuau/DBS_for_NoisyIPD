package ipdlx.tools;

import java.util.Random;

public abstract class RandomGenerator {
    
    private static java.util.Random random;
    private static long randomSeed;
    private static float deviation;
    
    static {
        randomSeed = System.currentTimeMillis();
        random = new Random(randomSeed);
    }

    public static double gaussianDouble() {
	return gaussianDouble(deviation);
    }

    public static void setDeviation(float dev) {
	RandomGenerator.deviation = dev;
    }
    
    /**
     * Generates a random double from a Gaussian distribution with the specified
     * deviation.
     * <p>
     * @param dev the desired deviation.
     * @return a random double from a Gaussian distribution with deviation
     * <code>dev</code>.
     */
    public static double gaussianDouble(float dev)
    {
	return random.nextGaussian()*dev;
    }
    
    public static double randomDouble(double lo,double hi)
    {
        return (hi-lo)*random.nextDouble()+lo;
    }
    
    public static double gaussianRandom(double mean, float deviation) {
	return mean + gaussianDouble(deviation);
    }
    
    public static final void main(String[] args) {
	javax.swing.JFrame f = new javax.swing.JFrame("Test");
	f.setSize(300, 300);
	f.setVisible(true);	
	javax.swing.JPanel p = new javax.swing.JPanel() {
		public void paint(java.awt.Graphics g) {
		    int size = 200;
		    int[] bin = new int[size];
		    RandomGenerator.setDeviation(0.05f);
		    for (int i = 0; i < 10000; i++) {
			int index = (int) ((size/2) + gaussianRandom(size, 0.05f));
			if (index >= 0 && index < size) {
			    bin[index]++;
			    g.drawLine(index, 0, index, bin[index]);
			}
		    }
		}
	    };
	f.getContentPane().add(p);
	
    }
    
}