/**
 * Javadoc
 * 
 * @author Ludo
 * @since 31/03/2018
 */

package main;

import java.io.*;
import peripherals.*;
import lsmcu.*;
import sounds.*;
import kvb.*;

public class Main {
	
	/* Data members */
	// Keyboard.
	public static Keyboard keyboard;
	// LSMCU.
	public static Lsmcu lsmcu;
	// Sounds.
	public static Zba zba;
	public static Zvm zvm;
	// KVB calculator.
	public static Kvb kvb;

	/* MAIN FUNCTION.
	 * @param: 	None.
	 * @return: None.
	 */
	public static void main(String[] args) throws IOException {
		
		/* Init objects */
		// Keyboard.
		keyboard = new Keyboard();
		// LSMCU manager.
		lsmcu = new Lsmcu("COM3");
		// Sounds.
		zba = new Zba();
		zvm = new Zvm();
		// KVB calculator.
		kvb = new Kvb();
		
		Sound test;
		test = new Sound("ventilateurs_extinction", 1.0);
		test.play();
		
		/* Main loop */
		while (lsmcu.getStatus() == false) {
			// Process incoming commands.
			lsmcu.task();
			// Run sounds tasks.
			zvm.task();
			// Run KVB calculator task.
			kvb.task();
			test.fadeIn(1000);
		}
		
		/* End of program */
		System.out.println("MAIN *** End of program.");
	}
}
