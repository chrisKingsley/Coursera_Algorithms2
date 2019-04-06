import java.awt.Color;

/**
 * Class for removing horizontal and/or vertical seams from images
 * @author ckingsley
 *
 */
public class SeamCarver {
	private static final double EDGE_ENERGY = 195075.0;
	private Picture pict;
	private double[][] energy, distTo;
	
	
	/**
	 * Constructor
	 * @param pict Picture object to be modified by seam removal
	 */
	public SeamCarver(Picture pict) {
		this.pict = new Picture(pict);
		calculateEnergyMatrix();
	}
	
	/**
	 * return current picture
	 * @return Picture object
	 */
	public Picture picture() {
		return pict;
	}
	
	/**
	 * return width of current picture
	 * @return picture width
	 */
	public int width() {
		return pict.width();
	}
	
	/**
	 * return height of current picture
	 * @return picture height
	 */
	public int height() {
		return pict.height();
	}
	
	/**
	 * return energy gradient of pixel at column col and row row
	 * @param col the pixel column (x coordinate)
	 * @param row the pixel row (y coordinate)
	 * @return energy gradient of pixel at column col and row row
	 */
	public double energy(int col, int row) {
		if (col < 0 || col >= pict.width() || row < 0 || row >= pict.height()) {
			throw new IndexOutOfBoundsException("Pixel positions (" +
					col + "," + row + ") are out of bounds");
		}
		return energy[col][row];
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
	 * Calculate the energy matrix for all pixels in the picture
	 */
	private void calculateEnergyMatrix() {
		// initialize energy matrix
		energy = new double[ pict.width() ][ pict.height() ];
		for (int col = 0; col < pict.width(); col++) {
			for (int row = 0; row < pict.height(); row++) {
				if (col == 0 || col == pict.width() - 1 || row == 0 || row == pict.height() - 1) {
					energy[col][row] = EDGE_ENERGY;
				} else {
					energy[col][row] = energyDifference(pict.get(col + 1, row), pict.get(col - 1, row)) +
							energyDifference(pict.get(col,  row - 1), pict.get(col,  row + 1));
				}
			}
		}	
	}
	
	
	/**
	 * Relax the graph edges along the vertical axis in the image
	 */
	private void relaxEdgesVerticalSeam() {
		distTo = new double[ pict.width() ][ pict.height() ];
		
		// initialize distTo values in first row
		for (int col = 0; col < pict.width(); col++) {
			distTo[col][0] = energy[col][0];
		}
		
		// populate distTo matrix
		for (int row = 1; row < pict.height(); row++) {
			for (int col = 0; col < pict.width(); col++) {
				distTo[col][row] = Double.MAX_VALUE;
				
				// choose best distance from upper pixels
				for (int k = -1; k <= 1; k++) {
					if (col + k >= 0 && col + k < pict.width()) {
						double newDist = distTo[col + k][row - 1] + energy[col][row];
						if (newDist < distTo[col][row]) {
							distTo[col][row] = newDist;
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Relax the graph edges along the horizontal axis in the image
	 */
	private void relaxEdgesHorizontalSeam() {
		distTo = new double[ pict.width() ][ pict.height() ];
		
		// initialize distTo values in first column
		for (int row = 0; row < pict.height(); row++) {
			distTo[0][row] = energy[0][row];
		}
		
		// populate distTo matrix
		for (int col = 1; col < pict.width(); col++) {
			for (int row = 0; row < pict.height(); row++) {
				distTo[col][row] = Double.MAX_VALUE;
				
				// choose best distance from left pixels
				for (int k = -1; k <= 1; k++) {
					if (row + k >= 0 && row + k < pict.height()) {
						double newDist = distTo[col - 1][row + k] + energy[col][row];
						if (newDist < distTo[col][row]) {
							distTo[col][row] = newDist;
						}
					}
				}
			}
		}
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
		for (int row = 0; row < pict.height(); row++) {
			if (distTo[pict.width() - 1][row] < minDist) {
				minDist = distTo[pict.width() - 1][row];
				hSeam[ pict.width() - 1 ] = row;
			}
		}
				
		// populate hSeam array
		for (int col = pict.width() - 1; col > 0; col--) {
			minDist = Double.POSITIVE_INFINITY;
			for (int row = hSeam[col] - 1; row <= hSeam[col] + 1; row++) {
				if (row >= 0 && row < pict.height() && distTo[col - 1][row] < minDist) {
					hSeam[col - 1] = row;
					minDist = distTo[col - 1][row];
				}
			}
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
		for (int col = 0; col < pict.width(); col++) {
			if (distTo[col][ pict.height() - 1 ] < minDist) {
				minDist = distTo[col][ pict.height() - 1 ];
				vSeam[ pict.height() - 1 ] = col;
			}
		}
		
		// populate vSeam array
		for (int row = pict.height() - 1; row > 0; row--) {
			minDist = Double.POSITIVE_INFINITY;
			for (int col = vSeam[row] - 1; col <= vSeam[row] + 1; col++) {
				if (col >= 0 && col < pict.width() && distTo[col][row - 1] < minDist) {
					vSeam[row - 1] = col;
					minDist = distTo[col][row - 1];
				}
			}
		}
		
		return vSeam;
	}
	
	
	/**
	 * Checks that the image can be further resized, that the passed seam is the correct length for
	 * the image, and that sequential values in the seam do not differ by more than 1
	 * @param a Seam array
	 * @param pictDimension1 Dimension of the picture (height or width) to compare against seam length
	 * @param pictDimension2 Dimension of the picture (width or height) to check if image can be resized
	 * @param seamType Type of seam ("horizontal" or "vertical")
	 */
	private void validateSeam(int[] a, int pictDimension1, int pictDimension2, String seamType) {
		if (pictDimension2 <= 1) {
			String msg = String.format("Cannot remove %s seam with image dimension <= 1", seamType);
			throw new IllegalArgumentException(msg);
		}
		
		if (a.length != pictDimension1) {
			String msg = String.format("Wrong length of %s seam:%d  Should be:%d",
					seamType, a.length, pictDimension1);
			throw new IllegalArgumentException(msg);
		}
		
		for (int i = 0; i < a.length; i++) {
			if (a[i] < 0 || a[i] >= pictDimension2) {
				String msg = String.format("Bad value in %s seam: seam[%d]=%d. Should be in {0..%d}",
						seamType, i, a[i], pictDimension2 - 1);
				throw new IllegalArgumentException(msg);
			}
			if (i > 0 && Math.abs(a[i - 1] - a[i]) > 1) {
				String msg = String.format("Bad sequential %s seam increment: seam[%d]=%d seam[%d]=%d",
						seamType, i - 1, a[i - 1], i, a[i]);
				throw new IllegalArgumentException(msg);
			}
		}
	}
	
	/**
	 * Remove horizontal seam from picture
	 * @param a array of row positions to remove at each successive column
	 */
	public void removeHorizontalSeam(int[] a) {
		validateSeam(a, pict.width(), pict.height(), "horizontal");
			
		Picture newPict = new Picture(pict.width(), pict.height() - 1);
		
		for (int col = 0; col < pict.width(); col++) {
			int d = 0;
			for (int row = 0; row < pict.height(); row++) {
				if (a[col] == row) {
					d = 1;
				} else {
					newPict.set(col, row - d, pict.get(col, row));
				}
			}
		}
		
		pict = newPict;
		calculateEnergyMatrix();
	}
	
	
	/**
	 * Remove vertical seam from picture
	 * @param a array of column positions to remove at each successive row
	 */
	public void removeVerticalSeam(int[] a) {
		validateSeam(a, pict.height(), pict.width(), "vertical");
		
		Picture newPict = new Picture(pict.width() - 1, pict.height());
		
		// copy pixel and energy values while eliminating the seam
		for(int row = 0; row < pict.height(); row++) {
			int d = 0;
			for (int col = 0; col < pict.width(); col++) {
				if (a[row] == col) {
					d = 1;
				} else {
					newPict.set(col - d, row, pict.get(col, row));
				}
			}
		}
		
		pict = newPict;
		calculateEnergyMatrix();
	}
	
	
	/**
	 * Print the color matrix
	 */
	public void printColors() {
		System.out.printf("Colors for %d col by %d row picture:\n", width(), height());
		for (int row = 0; row < pict.height(); row++) {
			for (int col = 0; col < pict.width(); col++) {
				Color pix = pict.get(col, row);
				System.out.printf("(%03d,%03d,%03d) ", pix.getRed(), pix.getGreen(), pix.getBlue());
			}
			System.out.println();
		}
	}
	
	/**
	 * Print the energy matrix
	 */
	public void printEnergies() {
		System.out.printf("Energies for %d col by %d row picture:\n", width(), height());
		for (int row = 0; row < pict.height(); row++) {
			for (int col = 0; col < pict.width(); col++) {
				System.out.printf("%9.0f ", energy[col][row]);
			}
			System.out.println();
		}
	}
	
	/**
	 * Print the distances matrix
	 */
	public void printDistances() {
		System.out.printf("Distances for %d col by %d row picture:\n", width(), height());
		if (distTo == null) {
			System.out.println("No distances calculated yet");
			return;
		}
		
		for (int row = 0; row < pict.height(); row++) {
			for (int col = 0; col < pict.width(); col++) {
				System.out.printf("%9.0f ", distTo[col][row]);
			}
			System.out.println();
		}
	}
	
	
	/**
	 * Main method for debugging test cases
	 * @param args cmd line arguments
	 */
	public static void main(String[] args) {
		String infile = "../testing_files/6x5.png";
		Picture pict = new Picture(infile);
		SeamCarver seam = new SeamCarver(pict);
		
		seam.printColors();
		seam.printEnergies();
		seam.relaxEdgesVerticalSeam();
		seam.printDistances();
		
//		int[] vSeam = seam.findVerticalSeam();
////		seam.printDistances();
//		for (int i = 0; i < vSeam.length; i++) {
//			System.out.println(vSeam[i]);
//		}
//		
//		int[] hSeam = seam.findHorizontalSeam();
////		seam.printDistances();
//		for (int i = 0; i < hSeam.length; i++) {
//			System.out.println(hSeam[i]);
//		}
//		seam.picture().show();
//		for (int i = 0; i < 6; i++) {
//			seam.removeVerticalSeam(seam.findVerticalSeam());
//		}
//		seam.picture().show();
//		ShowSeams.main(new String[] {infile});
		
//		ResizeDemo.main(new String[] {infile, "50", "50"});
	}
}