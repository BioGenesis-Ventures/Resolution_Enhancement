/*
    This file is part of ########.

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


 PURPOSE: Resolution_Enhancement attempts to provide a package of various functions useful in
 analyzing CT scan images in FIJI Is Just ImageJ.

 Author: Jonathan Collard de Beaufort, jonathancdb@gmail.com
 May 2024

 If you download and/or use this script, please email me. I am curious to hear
 from physicians and researchers on their experience.
*/
package Resolution_Enhancement;

import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.scijava.vecmath.Point3f;
import customnode.CustomPointMesh;
import ij3d.Content;
import ij3d.Image3DUniverse;


public class RI_UserInterface implements PlugIn {

    @Override
    public void run(String arg) {

        // Creates frame
        JFrame frame = new JFrame("Resolution Enhancement");
        frame.setSize(500, 100); //width, height
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        // Creates buttons
        JButton ln_avg = new JButton("Linear Average");			// Linear average
        JButton quad_avg = new JButton("Quadratic Average");	// Quadratic average
        JButton cubic_avg = new JButton("Cubic Average");		// Cubic average
        JButton help_button = new JButton("Help");
        JButton contact_button = new JButton("Contact");
        
        ln_avg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create an instance of Linear_RE
                Linear_RE linearRE = new Linear_RE();
                // Get the ImageProcessor from ImagePlus
                ImageProcessor ip = imagePlus.getProcessor();
                // Call the performEnhancement method
                linearRE.performEnhancement(ip);
                // Update the ImagePlus with the enhanced ImageProcessor
                imagePlus.updateAndDraw();
            }
        });
        
        quad_avg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IJ.log("Quad pressed");
            }
        });
        
        cubic_avg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IJ.log("Cubic pressed");
            }
        });

        help_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IJ.showMessage("Help Button pressed");
            }
        });

        contact_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IJ.showMessage("Jonathan Collard de Beaufort \n jonathancdb@gmail.com");
            }
        });

        // Add buttons to the frame
        frame.add(ln_avg);
        frame.add(quad_avg);
        frame.add(cubic_avg);
        frame.add(help_button);
        frame.add(contact_button);
        

        // Make the frame visible
        frame.setVisible(true);
    }

	// --- USER INTERFACE CODE ABOVE ---
	
	// BEGIN PROGRAMS

    public static void main(String[] args) {
        new RI_UserInterface().run("");
    }
}