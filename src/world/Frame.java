package world;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Font;
import java.io.IOException;

import javax.swing.JFrame;
//import javax.swing.JLabel;


public class Frame extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


    public Frame() /* throws IOException */ {

       
        World w = new World();
        add(w);

        setSize(800, 740);
//        setSize(480,480);
        setResizable(false);
        
        setTitle("Sleepy Valley");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

   }


    public static void main(String[] args) throws IOException {

        Frame frame = new Frame();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    } 
}
