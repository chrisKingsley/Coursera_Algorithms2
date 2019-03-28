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
		
		// initialize energy matrix
		energy = new double[ pict.width() ][ pict.height() ];
		for (int i = 0; i < pict.width(); i++) {
			for (int j = 0; j < pict.width(); j++) {
				energy[i][j] = energy(i,j);
			}
		}
		
		// initialize distTo and edgeTo
		distTo = new double[2][ pict.width() ][ pict.height() ];
		edgeTo = new int[2][];
		edgeTo[ VERTICAL ] = new int[ pict.height() ];
		edgeTo[ HORIZONTAL ] = new int[ pict.width() ];
		
		relaxEdges(VERTICAL);
		relaxEdges(HORIZONTAL);
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
		return (pix1.getRed() - pix2.getRed())^2 + (pix1.getGreen() - pix2.getGreen())^2 + 
				(pix1.getBlue() - pix2.getBlue())^2;
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
	 * return energy gradient of pixel at column x and row y
	 * @return energy gradient of pixel at column x and row y
	 */
	private double energy(int x, int y) {
		validatePoints(x, y);
		if (x == 0 || x == pict.width() - 1 || y == 0 || y == pict.height() - 1)
			return EDGE_ENERGY;
		
		return energyDifference(pict.get(x + 1, y), pict.get(x - 1, y)) +
				energyDifference(pict.get(x,  y - 1), pict.get(x,  y + 1));
	}
	
	
	/**
	 * Find the shortest paths tree for either dimension in the image
	 * @param dimension the dimension to relax edges along - HORIZONTAL or VERTICAL
	 */
	private void relaxEdges(int dimension) {
		
	}
	
	/**
	 * return sequence of indices for horizontal seam
	 * @return
	 */
	public int[] findHorizontalSeam() {
		int[] hSeam = new int[ pict.width() ];
		
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
		
		// find minimum distance on bottom edge
		double minDist = Double.POSITIVE_INFINITY;
		int startIdx = 0;
		for (int i = 0; i < pict.width(); i++) {
			if (distTo[ VERTICAL ][ pict.height() - 1 ][i] < minDist) {
				minDist = distTo[ VERTICAL ][ pict.height() - 1 ][i];
				startIdx = i;
			}
		}
		
		// populate vSeam array
		vSeam[ pict.height() - 1 ] = startIdx;
		for (int i = pict.height() - 2; i >= 0; i--) {
			vSeam[i] = edgeTo[ VERTICAL ][ vSeam[i + 1] ];
		}
		
		return vSeam;
	}
	
	
	/**
	 * remove horizontal seam from picture
	 * @return
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
	}
	
	
	/**
	 * remove vertical seam from picture
	 * @return
	 */
	public void removeVerticalSeam(int[] a) {
		Picture newPict = new Picture(pict.width() - 1, pict.height());
		
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
	}

}