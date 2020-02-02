/**
 * Javadoc
 * 
 * @author Ludo
 * @since 31/03/2018
 */

package peripherals;

import java.util.*;
import java.io.*;
import gnu.io.*;
import lsmcu.*;

public class Serial implements SerialPortEventListener {
	
	/* Data members */
	private String serialNumPort;
	private int serialBaudRate;
	private SerialPort serialPort;
	private BufferedReader serialInput;
	private OutputStream serialOutput;
	private static final int SERIAL_TIME_OUT = 2000; // Time to wait while trying to open serial port

	/* CONSTRUCTOR FOR CLASS SERIAL.
	 * @param pName:	Name of the device to connect (used for display).
	 * @param pNumPort:	Port number to connect, should be "COMx" where x is an integer.
	 * @return: 		None.
	 */
	public Serial(String pNumPort, int pBaudRate) {
		serialNumPort = pNumPort;
		serialBaudRate = pBaudRate;
	}
	
	/* OPEN SERIAL PORT.
	 * @param:			None.
	 * @return error: 	false if serial port was successfully opened, true otherwise.
	 */
	public boolean open() {
		boolean error = false;
		int numberOfPorts = 0;
		CommPortIdentifier portId = null;
		// Get all the COM ports currently connected.
		@SuppressWarnings("rawtypes")
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		// Check if 'numPort' is connected.
		System.out.print("SERIAL *** List of available serial ports:");
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			System.out.print(" " + currPortId.getName());
			if (currPortId.getName().equals(serialNumPort)) {
				portId = currPortId;
			}
			numberOfPorts++;
		}
		// Debug print.
		if (numberOfPorts == 0) {
			System.out.print(" None");
		}
		System.out.println(".");
		// Check port ID.
		if (portId == null) {
			error = true;
		}
		else {
			System.out.print("SERIAL *** " + serialNumPort + " serial port opening: ");
			try {
				// Open serial port
				serialPort = (SerialPort) portId.open(this.getClass().getName(), SERIAL_TIME_OUT);
				serialPort.setSerialPortParams(serialBaudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				// Input and output streams
				serialInput = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
				serialOutput = serialPort.getOutputStream();
				System.out.println("Success.");
			}
			catch (PortInUseException e) {
				System.out.println("Failed (allready in use).");
				error = true;
			}
			catch (Exception e) {
				System.out.println("Failed (" + e.toString() + ").");
				error = true;
			}
		}
		return error;
	}
	
	/* SEND A BYTE TO SERIAL PORT.
	 * @param byteToSend:	The byte to send.
	 * @return 				None.
	 */
	public void sendByte(int pByteToSend) throws IOException {
	    if ((serialOutput != null) && (pByteToSend >= 0) && (pByteToSend <= 255)) {
	    	// Display the binary and decimal representation of the byte sent
	    	System.out.println("Send data '" + Integer.toBinaryString(pByteToSend) + "' = " + pByteToSend + " to " + serialNumPort);
	    	serialOutput.write(pByteToSend);
	    }
	}
	
	/* READ A BYTE FROM SERIAL PORT (INTERRUPT HANDLER).
	 * @param e:	Serial port event.
	 * @return 		None.
	 */
	public synchronized void serialEvent(SerialPortEvent pEvent) {
		if (pEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int rxByte = serialInput.read();
				//System.out.println("Serial RX = " + rxByte);
				if (rxByte != -1) {
					// Fill LSMCU RX buffer.
					Lsmcu.fillRxBuffer(rxByte);
				}
			}
			catch (Exception e) {
				System.err.println(e.toString());
			}
		}
	}

	/* CLOSE SERIAL PORT.
	 * @param	None.
	 * @return: None.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.close();
			serialPort.removeEventListener();
		}
	}
}
