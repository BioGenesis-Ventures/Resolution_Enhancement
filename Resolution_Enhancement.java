/*
    This file is part of ########.

    VolumetricAnalysis_Medicine is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    VolumetricAnalysis_Medicine is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with VolumetricAnalysis_Medicine. If not, see <http://www.gnu.org/licenses/>.


 WRITE PURPOSE AND INTRODUCTION HERE

 Author: Jonathan Collard de Beaufort, jonathancdb@gmail.com
 May 2024

 If you download and/or use this script, please email me. I am curious to hear
 from physicians and researchers on their experience.
*/

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.ColorProcessor;
import ij.process.StackProcessor;

public class Resolution_Enhancement implements PlugInFilter {

    @Override
    public int setup(String arg, ImagePlus imp) {
        return DOES_8G | DOES_RGB | STACK_REQUIRED; // Accepts 8-bit grayscale and RGB stacks
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

        ImageStack newStack = new ImageStack(newWidth, newHeight);

        for (int z = 0; z < depth; z++) {
            ImageProcessor currentSlice = stack.getProcessor(z + 1);
            ImageProcessor prevSlice = (z > 0) ? stack.getProcessor(z) : currentSlice;
            ImageProcessor nextSlice = (z < depth - 1) ? stack.getProcessor(z + 2) : currentSlice;

            ImageProcessor newIp = (ip instanceof ColorProcessor) ? new ColorProcessor(newWidth, newHeight) : ip.createProcessor(newWidth, newHeight);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int[] pixels = new int[8];
                    pixels[0] = currentSlice.getPixel(x, y);
                    pixels[1] = (x < width - 1) ? currentSlice.getPixel(x + 1, y) : pixels[0];
                    pixels[2] = (y < height - 1) ? currentSlice.getPixel(x, y + 1) : pixels[0];
                    pixels[3] = (x < width - 1 && y < height - 1) ? currentSlice.getPixel(x + 1, y + 1) : pixels[0];
                    pixels[4] = prevSlice.getPixel(x, y);
                    pixels[5] = (x < width - 1) ? prevSlice.getPixel(x + 1, y) : pixels[4];
                    pixels[6] = (y < height - 1) ? prevSlice.getPixel(x, y + 1) : pixels[4];
                    pixels[7] = (x < width - 1 && y < height - 1) ? prevSlice.getPixel(x + 1, y + 1) : pixels[4];

                    int avg1 = average(pixels[0], pixels[1], pixels[4], pixels[5]);
                    int avg2 = average(pixels[0], pixels[2], pixels[4], pixels[6]);
                    int avg3 = average(pixels[1], pixels[3], pixels[5], pixels[7]);
                    int avg4 = average(pixels[2], pixels[3], pixels[6], pixels[7]);

                    newIp.putPixel(2 * x, 2 * y, avg1);
                    newIp.putPixel(2 * x + 1, 2 * y, avg2);
                    newIp.putPixel(2 * x, 2 * y + 1, avg3);
                    newIp.putPixel(2 * x + 1, 2 * y + 1, avg4);
                }
            }
            newStack.addSlice(newIp);
        }
        ImagePlus newImp = new ImagePlus("Subpixel Averaging 3D", newStack);
        newImp.show();
    }

    private int average(int... values) {
        int sum = 0;
        for (int value : values) {
            sum += value;
        }
        return sum / values.length;
    }
}