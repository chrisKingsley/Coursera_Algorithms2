import java.awt.Color;

/**
 * Class for removing horizontal and/or vertical seams from images
 * @author ckingsley
 *
 */
public class SeamCarver {
	private static final double EDGE_ENERGY = 195075.0;
	private static final int VERTICAL = 0, HORIZONTAL = 1;
	private Picture pict;
	private int[][] edgeTo;
	private double[][] energy;
	private double[][][] distTo;
	
	
	/**
	 * Constructor
	 * @param pict Picture object to be modified by seam removal
	 */
	public SeamCarver(Picture pict) {
		this.pict = new Picture(pict);
		calculateEnergyMatrix();
		
		// initialize distTo and edgeTo
		distTo = new double[2][ pict.width() ][ pict.height() ];
		edgeTo = new int[2][];
		edgeTo[ VERTICAL ] = new int[ pict.height() ];
		edgeTo[ HORIZONTAL ] = new int[ pict.width() ];
	}
	
	/**
	 * return current picture
	 * @return
	 */
	public Picture picture() {
		return pict;
	}
	
	/**
	 * return width of current picture
	 * @return
	 */
	public int width() {
		return pict.width();
	}
	
	/**
	 * return height of current picture
	 * @return
	 */
	public int height() {
		return pict.height();
	}
	
	
	/**
	 * Returns the RGB energy difference between the two passed pixels
	 * @param pix1 first pixel to use to compute energy difference
	 * @param pix2 second pixel to use to compute energy difference
	 * @return the RGB energy difference between the two pixels
	 */
	private double energyDifference(Color pix1, Color pix2) {
		return Math.pow(pix1.getRed() - pix2.getRed(), 2) + Math.pow(pix1.getGreen() - pix2.getGreen(), 2) + 
				Math.pow(pix1.getBlue() - pix2.getBlue(), 2);
	}
	
	
	/**
	 * Make sure pixel positions are in bounds with respect to the image dimensions
	 * @param x x-coordinate of pixel
	 * @param y y-coordinate of pixel
	 */
	private void validatePoints(int x, int y) {
		if (x < 0 || x >= pict.width() || y < 0 || y >= pict.height()) {
			throw new IndexOutOfBoundsException("Pixel positions (" + x + "," + y + ") are out of bounds");
		}
	}
	
	
	/**
	 * 
	 * 
	 */
	/**
	 * return energy gradient of pixel at column col and row row
	 * @param col
	 * @param y
	 * @return energy gradient of pixel at column col and row row
	 */
	public double energy(int col, int row) {
		validatePoints(col, row);
		if (col == 0 || col == pict.width() - 1 || row == 0 || row == pict.height() - 1)
			return EDGE_ENERGY;
		
		return energyDifference(pict.get(col + 1, row), pict.get(col - 1, row)) +
				energyDifference(pict.get(col,  row - 1), pict.get(col,  row + 1));
	}
	
	
	/**
	 * Calculate the energy matrix for all pixels in the picture
	 */
	private void calculateEnergyMatrix() {
		// initialize energy matrix
		energy = new double[ pict.width() ][ pict.height() ];
		for (int i = 0; i < pict.width(); i++) {
			for (int j = 0; j < pict.height(); j++) {
				energy[i][j] = energy(i,j);
			}
		}	
	}
	
	
	/**
	 * Relax the graph edges along vertical seams in the image
	 */
	private void relaxEdgesVerticalSeam() {
		for (int row = 0; row < pict.height(); row++) {
			for (int col = 0; col < pict.width(); col++) {
				double minDist = Double.MAX_VALUE;
				int distIdx = 0;
				
				for (int k = -1; k <= 1; k++) {
					if (row > 0 && col + k >= 0 && col + k < pict.width() && 
							minDist > distTo[ VERTICAL ][col + k][row - 1]) {
						minDist = distTo[ VERTICAL ][col + k][row - 1];
						distIdx = col + k;
					}
				}
				distTo[ VERTICAL ][col][row] = energy[col][row] + (row == 0 ? 0 : minDist);
				edgeTo[ VERTICAL ][row] = distIdx;
			}
		}
	}
	
	private void relaxEdgesHorizontalSeam() {
		
	}
	
	/**
	 * return sequence of indices for horizontal seam
	 * @return
	 */
	public int[] findHorizontalSeam() {
		int[] hSeam = new int[ pict.width() ];
		relaxEdgesHorizontalSeam();
		
		// find minimum distance on right edge
		double minDist = Double.POSITIVE_INFINITY;
		int startIdx = 0;
		for (int i = 0; i < pict.height(); i++) {
			if (distTo[ HORIZONTAL ][i][ pict.width() - 1] < minDist) {
				minDist = distTo[ HORIZONTAL ][i][ pict.width() - 1];
				startIdx = i;
			}
		}
		
		// populate hSeam array
		hSeam[ pict.width() - 1] = startIdx;
		for (int i = pict.width() - 2; i >= 0; i--) {
			hSeam[i] = edgeTo[ HORIZONTAL ][ hSeam[i + 1] ];
		}
		
		return hSeam;
	}
	
	
	/**
	 * return sequence of indices for vertical seam
	 * @return
	 */
	public int[] findVerticalSeam() {
		int[] vSeam = new int[ pict.height() ];
		relaxEdgesVerticalSeam();
		
		// find minimum distance on bottom edge
		double minDist = Double.POSITIVE_INFINITY;
		int startIdx = 0;
		for (int col = 0; col < pict.width(); col++) {
			if (distTo[ VERTICAL ][col][ pict.height() - 1 ] < minDist) {
				minDist = distTo[ VERTICAL ][col][ pict.height() - 1 ];
				startIdx = col;
			}
		}
		
		// populate vSeam array
		vSeam[ pict.height() - 1 ] = startIdx;
		for (int row = pict.height() - 2; row >= 0; row--) {
			vSeam[row] = edgeTo[ VERTICAL ][ vSeam[row + 1] ];
		}
		
		return vSeam;
	}
	
	
	/**
	 * remove horizontal seam from picture
	 */
	public void removeHorizontalSeam(int[] a) {
		Picture newPict = new Picture(pict.width(), pict.height() - 1);
		
		for (int col = 0; col < pict.width(); col++) {
			int d = 0;
			for (int row = 0; row < pict.height(); row++) {
				if (a[col] == row) {
					d = 1;
					continue;
				}
				newPict.set(row - d, col, pict.get(row, col));
			}
		}
		
		pict = newPict;
		calculateEnergyMatrix();
	}
	
	
	/**
	 * remove vertical seam from picture
	 */
	public void removeVerticalSeam(int[] a) {
		Picture newPict = new Picture(pict.width() - 1, pict.height());
		relaxEdgesVerticalSeam();
		
		for(int row = 0; row < pict.height(); row++) {
			int d = 0;
			for (int col = 0; col < pict.width(); col++) {
				if (a[row] == col) {
					d = 1;
					continue;
				}
				newPict.set(row, col - d, pict.get(row, col));
			}
		}
		
		pict = newPict;
		calculateEnergyMatrix();
	}
	
	
	/**
	 * Main method for debugging test cases
	 * @param args cmd line arguments
	 */
	public static void main(String[] args) {
		String infile = "../testing_files/6x5.png";
		Picture pict = new Picture(infile);
		SeamCarver seam = new SeamCarver(pict);
		int[] vertSeam = seam.findVerticalSeam();
		
		PrintEnergy.main(new String[] {infile});
//		ShowEnergy.main(new String[] {"../testing_files/HJocean.png"});
		
		for (int row = 0; row < pict.height(); row++) {
			for (int col = 0; col < pict.width(); col++) {
//				Color pix = pict.get(col, row);
//				System.out.printf("(%03d,%03d,%03d) ", pix.getRed(), pix.getGreen(), pix.getBlue());
				System.out.print(seam.distTo[VERTICAL][col][row] + " ");
			}
			System.out.println();
		}
		
		for (int i = 0; i < vertSeam.length; i++) {
			System.out.println(vertSeam[i] + " " + seam.edgeTo[VERTICAL][i]);
		}
	}

}