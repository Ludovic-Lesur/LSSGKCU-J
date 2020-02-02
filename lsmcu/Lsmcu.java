/**
 * Javadoc
 * 
 * @author Ludo
 * @since 26/01/2020
 */

package lsmcu;

import java.io.*;
import peripherals.*;
import main.*;

public class Lsmcu {

	/* Data members */
	private static Serial lsmcuSerial;
	private static boolean lsmcuError;
	private static final int LSMCU_RX_BUFFER_SIZE = 32;
	private static volatile int lsmcuRxBuffer[];
	private static volatile int lsmcuRxBufReadIdx;
	private static volatile int lsmcuRxBufWriteIdx;
	
	/* CONSTRUCTOR FOR CLASS LSMCU.
	 * @param pName:	Name of the device to connect (used for display).
	 * @param pNumPort:	Port number to connect, should be "COMx" where x is an integer.
	 * @return: 		None.
	 */
	public Lsmcu(String pNumPort) {
		// Init buffer.
		lsmcuRxBuffer = new int[LSMCU_RX_BUFFER_SIZE];
		lsmcuRxBufReadIdx = 0;
		lsmcuRxBufWriteIdx = 0;
		// Create serial port.
		lsmcuSerial = new Serial(pNumPort, 9600);
		lsmcuError = lsmcuSerial.open();
		// Debug print.
		System.out.print("LSMCU *** Open serial port " + pNumPort + ": ");
		if (lsmcuError == false) {
			System.out.println("Success.");
		}
		else {
			System.out.println("Failed.");
		}
	}
	
	/* STORE A BYTE RECEIVED FROM LSMCU.
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
		// Debug print.
		System.out.println("LSMCU *** RX command = " + pNewByte + ".");
	}
	
	/* SEND A BYTE TO LSMCU.
	 * @param pNewByte:	Byte to send.
	 * @return:			None.
	 */
	public void sendCommand(int pNewByte) throws IOException {
		lsmcuSerial.sendByte(pNewByte);
		// Debug print.
		System.out.println("LSMCU *** TX command = " + pNewByte + ".");
	}
	
	/* GET LSMCU MANAGER STATUS
	 * @param:				None.
	 * @return lsmcuError:	'true' if any error occured, 'false' otherwise.
	 */
	public boolean getStatus() {
		return lsmcuError;
	}
	
	/* MAIN TASK OF LSMCU MANAGER.
	 * @param:	None.
	 * @return:	None.
	 */
	public void task() {
		// Process incoming commands.
		if (lsmcuRxBufWriteIdx != lsmcuRxBufReadIdx) {
			int lsmcuCommand = lsmcuRxBuffer[lsmcuRxBufReadIdx];
			// Decode command.
			if (lsmcuCommand == Inputs.LSMCU_OUT_ZBA_ON.getNumber()) {
				Main.zba.on();
			}
			else if (lsmcuCommand == Inputs.LSMCU_OUT_ZBA_OFF.getNumber()) {
				Main.zba.off();
			}
			else if (lsmcuCommand == Inputs.LSMCU_OUT_ZVM_ON.getNumber()) {
				Main.zvm.on();
			}
			else if (lsmcuCommand == Inputs.LSMCU_OUT_ZVM_OFF.getNumber()) {
				Main.zvm.off();
			}
			// Update read index and manage rollover.
			lsmcuRxBufReadIdx++;
			if (lsmcuRxBufReadIdx == LSMCU_RX_BUFFER_SIZE) {
				lsmcuRxBufReadIdx = 0;
			}
		}
	}
}
