import java.awt.Color;

/**
 * Class for removing horizontal and/or vertical seams from images
 * @author ckingsley
 *
 */
public class SeamCarver {
	private static final double EDGE_ENERGY = 195075.0;
	private Picture pict;
	private double[][] energy;
	private double[][] distTo;
	
	
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
//		System.out.println("Minimum horizontal seam distance: " +
//				distTo[ pict.width() - 1 ][ hSeam[ pict.width() - 1 ] ]);
				
		// populate hSeam array
		for (int col = pict.width() - 1; col > 0; col--) {
			minDist = Double.POSITIVE_INFINITY;
			for (int k = -1; k <= 1; k++) {
				int row = hSeam[col] + k;
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
//		System.out.println("Minimum vertical seam distance: " +
//				distTo[ vSeam[ pict.height() - 1 ] ][ pict.height() - 1 ]);
		
		// populate vSeam array
		for (int row = pict.height() - 1; row > 0; row--) {
			minDist = Double.POSITIVE_INFINITY;
			for (int k = -1; k <= 1; k++) {
				int col = vSeam[row] + k;
				if (col >= 0 && col < pict.width() && distTo[col][row - 1] < minDist) {
					vSeam[row - 1] = col;
					minDist = distTo[col][row - 1];
				}
			}
		}
		
		return vSeam;
	}
	
	
	/**
	 * Remove horizontal seam from picture
	 * @param a array of column positions to remove
	 */
	public void removeHorizontalSeam(int[] a) {
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
	 * @param a array of column positions to remove
	 */
	public void removeVerticalSeam(int[] a) {
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
		System.out.printf("Colors for %d col by %d row picture\n", width(), height());
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
		System.out.printf("Energies for %d col by %d row picture\n", width(), height());
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
		System.out.printf("Distances for %d col by %d row picture\n", width(), height());
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
		String infile = "../testing_files/HJocean.png";
//		Picture pict = new Picture(infile);
//		SeamCarver seam = new SeamCarver(pict);
////		seam.printColors();
////		seam.printEnergies();
////		
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
//		for (int i = 0; i < 50; i++) {
//			seam.removeHorizontalSeam(seam.findHorizontalSeam());
//		}
//		seam.picture().show();
//		ShowSeams.main(new String[] {infile});
		
		ResizeDemo.main(new String[] {infile, "50", "50"});
	}

}