import java.awt.Color;

/**
 * Class for removing horizontal and/or vertical seams from images
 * @author ckingsley
 *
 */
public class SeamCarver {
	private static final double EDGE_ENERGY = 195075.0;
	private Picture pict;
	private double shortestPath;
	
	/**
	 * Constructor
	 * @param pict Picture object to be modified by seam removal
	 */
	public SeamCarver(Picture pict) {
		this.pict = new Picture(pict);
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
	public double energy(int x, int y) {
		validatePoints(x, y);
		if (x == 0 || x == pict.width() - 1 || y == 0 || y == pict.height() - 1)
			return EDGE_ENERGY;
		
		return energyDifference(pict.get(x + 1, y), pict.get(x - 1, y)) +
				energyDifference(pict.get(x,  y - 1), pict.get(x,  y + 1));
	}
	
	/**
	 * return sequence of indices for horizontal seam
	 * @return
	 */
	public int[] findHorizontalSeam() {
		int[] hSeam = new int[ pict.width() ];
		
		return hSeam;
	}
	
	/**
	 * return sequence of indices for vertical seam
	 * @return
	 */
	public int[] findVerticalSeam() {
		int[] vSeam = new int[ pict.height() ];
		
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