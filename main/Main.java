/**
 * Javadoc
 * 
 * @author Ludovic Lesur
 * @since 31/03/2018
 */

package main;

import peripherals.*;
import lsmcu.*;
import sounds.*;

public class Main {
	
	/* Data members */
	// Keyboard.
	private static Keyboard keyboard;
	// LSMCU serial port.
	private static Serial lsmcuSerial;
	private static final int LSMCU_RX_BUFFER_SIZE = 32;
	private static volatile int lsmcuRxBuffer[];
	private static volatile int lsmcuRxBufReadIdx;
	private static volatile int lsmcuRxBufWriteIdx;
	// Sounds.
	private static Zba zba;
	
	/* FILL RX COMMAND BUFFER.
	 * @param pNewByte:	Incoming byte to store.
	 * @return:			None.
	 */
	public static void fillRxBuffer(int pNewByte) {
		// Store byte.
		lsmcuRxBuffer[lsmcuRxBufWriteIdx] = pNewByte;
		// Update write index and manage rollover.
		lsmcuRxBufWriteIdx++;
		if (lsmcuRxBufWriteIdx == LSMCU_RX_BUFFER_SIZE) {
			lsmcuRxBufWriteIdx = 0;
		}
	}

	/* MAIN FUNCTION.
	 * @param: 	None.
	 * @return: None.
	 */
	public static void main(String[] args) {
		
		/* Keyboard */
		keyboard = new Keyboard();
		
		/* LSMCU serial port */
		// Open serial link.
		lsmcuSerial = new Serial("LSMCU", "COM12", 9600);
		boolean error = lsmcuSerial.open();
		// Init buffer and indexes.
		lsmcuRxBuffer = new int[LSMCU_RX_BUFFER_SIZE];
		lsmcuRxBufReadIdx = 0;
		lsmcuRxBufWriteIdx = 0;
		
		/* Sounds */
		zba = new Zba();
		
		/* Main loop */
		while (error == false) {
			// Process incoming commands.
			if (lsmcuRxBufWriteIdx != lsmcuRxBufReadIdx) {
				int lsmcuCommand = lsmcuRxBuffer[lsmcuRxBufReadIdx];
				System.out.println("LSMCU command = " + lsmcuCommand + ".");
				// Decode command.
				if (lsmcuCommand == Inputs.LSMCU_OUT_ZBA_ON.getNumber()) {
					zba.on();
					System.out.println("ZBA ON.");
				}
				else if (lsmcuCommand == Inputs.LSMCU_OUT_ZBA_OFF.getNumber()) {
					zba.off();
					System.out.println("ZBA OFF.");
				}
				// Update read index and manage rollover.
				lsmcuRxBufReadIdx++;
				if (lsmcuRxBufReadIdx == LSMCU_RX_BUFFER_SIZE) {
					lsmcuRxBufReadIdx = 0;
				}
			}
		}
		System.out.println("End of program.");
	}
}
