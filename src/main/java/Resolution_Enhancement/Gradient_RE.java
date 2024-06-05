/*
    This file is part of Resolution_Enhancement

    Resolution_Enhancement is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Resolution_Enhancement is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with VolumetricAnalysis_Medicine. If not, see <http://www.gnu.org/licenses/>.

 PURPOSE: This code divides a stack of CT scans by 2 in every dimension and finds
 the new voxel value based on gradients established by neighboring voxels.

 Author: Jonathan Collard de Beaufort, jonathancdb@gmail.com
 May 2024

 If you download and/or use this script, please email me. I am curious to hear
 from physicians and researchers on their experience.
*/

package Resolution_Enhancement;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;

public class Gradient_RE implements PlugInFilter {

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_ALL;
    }

    @Override
    public void run(ImageProcessor ip) {
        ImagePlus imp = IJ.getImage();
        ImageStack stack = imp.getStack();
        int width = ip.getWidth();
        int height = ip.getHeight();
        int depth = stack.getSize();
        int newWidth = width * 2;
        int newHeight = height * 2;
        int newDepth = depth * 2;
        
        int totalCalculations = width * height * depth;
        int calculationsCount = 0;
        
        long startTime = System.currentTimeMillis();

        ImageStack newStack = new ImageStack(newWidth, newHeight);

        // Initialize each slice of the new stack
        for (int i = 0; i < newDepth; i++) {
            newStack.addSlice(stack.getProcessor(1).createProcessor(newWidth, newHeight));
        }

        for (int z = 0; z < depth; z++) {
            ImageProcessor currentSlice = stack.getProcessor(z + 1);
            ImageProcessor prevSlice = (z > 0) ? stack.getProcessor(z) : currentSlice;
            ImageProcessor nextSlice = (z < depth - 1) ? stack.getProcessor(z + 2) : currentSlice;

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    // Update calculation status
                    calculationsCount += 1;
                    int percentageIncrement = (int) Math.ceil(totalCalculations / 10.0);
            
		            if (calculationsCount % percentageIncrement == 0) {
		                int percentage = (calculationsCount * 100) / totalCalculations;
		                IJ.log("Status: " + percentage + "%");
		            }

					for (int dz = 0; dz < 2; dz++) {
                        for (int dy = 0; dy < 2; dy++) {
                            for (int dx = 0; dx < 2; dx++) {
                                int subpixelX = 2 * x + dx;
                                int subpixelY = 2 * y + dy;
                                int subpixelZ = 2 * z + dz;
                                
                                int[] neighbors = new int[4];
                                
                                if (dx == 0 || dy == 0 || dz == 0) {
                                	neighbors[0] = getPixelSafe(currentSlice, x - 1, y);
                                	neighbors[1] = getPixelSafe(currentSlice, x, y - 1);
                                	neighbors[2] = getPixelSafe(currentSlice, x - 1, y - 1);
                                	neighbors[3] = getPixelSafe(prevSlice, x, y);
                                } else if (dx == 1 || dy == 0 || dz == 0) {
                                	neighbors[0] = getPixelSafe(currentSlice, x, y - 1);
                                	neighbors[1] = getPixelSafe(currentSlice, x + 1, y);
                                	neighbors[2] = getPixelSafe(currentSlice, x + 1, y - 1);
                                	neighbors[3] = getPixelSafe(prevSlice, x, y);
                                } else if (dx == 0 || dy == 1 || dz == 0) {
                                	neighbors[0] = getPixelSafe(currentSlice, x - 1, y);
                                	neighbors[1] = getPixelSafe(currentSlice, x - 1, y + 1);
                                	neighbors[2] = getPixelSafe(currentSlice, x, y + 1);
                                	neighbors[3] = getPixelSafe(prevSlice, x, y);
                                } else if (dx == 1 || dy == 1 || dz == 0) {
                                	neighbors[0] = getPixelSafe(currentSlice, x, y + 1);
                                	neighbors[1] = getPixelSafe(currentSlice, x + 1, y + 1);
                                	neighbors[2] = getPixelSafe(currentSlice, x + 1, y);
                                	neighbors[3] = getPixelSafe(prevSlice, x, y);
                                } else if (dx == 0 || dy == 0 || dz == 1) {
                                	neighbors[0] = getPixelSafe(currentSlice, x - 1, y);
                                	neighbors[1] = getPixelSafe(currentSlice, x, y - 1);
                                	neighbors[2] = getPixelSafe(currentSlice, x - 1, y - 1);
                                	neighbors[3] = getPixelSafe(prevSlice, x, y);
                                } else if (dx == 1 || dy == 0 || dz == 1) {
                                	neighbors[0] = getPixelSafe(currentSlice, x, y - 1);
                                	neighbors[1] = getPixelSafe(currentSlice, x + 1, y);
                                	neighbors[2] = getPixelSafe(currentSlice, x + 1, y - 1);
                                	neighbors[3] = getPixelSafe(prevSlice, x, y);
                                } else if (dx == 0 || dy == 1 || dz == 1) {
                                	neighbors[0] = getPixelSafe(currentSlice, x - 1, y);
                                	neighbors[1] = getPixelSafe(currentSlice, x - 1, y + 1);
                                	neighbors[2] = getPixelSafe(currentSlice, x, y + 1);
                                	neighbors[3] = getPixelSafe(prevSlice, x, y);
                                } else if (dx == 1 || dy == 1 || dz == 1) {
                                	neighbors[0] = getPixelSafe(currentSlice, x, y + 1);
                                	neighbors[1] = getPixelSafe(currentSlice, x + 1, y + 1);
                                	neighbors[2] = getPixelSafe(currentSlice, x + 1, y);
                                	neighbors[3] = getPixelSafe(prevSlice, x, y);
                                }
                                
                                int pixelAverage = gradientFunction(neighbors);

                                // Set the new subpixel value
                                newStack.getProcessor(subpixelZ + 1).putPixel(subpixelX, subpixelY, pixelAverage);
                            }
                        }
                    }
                }
            }
        }

        ImagePlus newImp = new ImagePlus("Subpixel Averaging 3D: RMS (Quadratic)", newStack);
        newImp.show();
        
        long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		double runTimeSeconds = runTime / 1000.0;
		IJ.log("Total Run Time: " + runTimeSeconds + " seconds");
    }

    private int getPixelSafe(ImageProcessor ip, int x, int y) {
        if (x < 0 || x >= ip.getWidth() || y < 0 || y >= ip.getHeight()) {
            return 0;
        }
        return ip.getPixel(x, y);
    }

    private int gradientFunction(int[] pixelValues) {

        double sumOfSquares = 0.0;
        for (int pixel : pixelValues) {
            sumOfSquares += pixel * pixel;
        }
        
        double meanOfSquares = sumOfSquares / pixelValues.length;
        
        int pixelVal = (int) Math.sqrt(meanOfSquares);
        
        return pixelVal;
    }
}
