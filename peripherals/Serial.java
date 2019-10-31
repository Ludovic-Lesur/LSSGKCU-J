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
import main.*;

public class Serial implements SerialPortEventListener {
	
	/* Data members */
	private String serialName;
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
	public Serial(String pName, String pNumPort, int pBaudRate) {
		serialName = pName;
		serialNumPort = pNumPort;
		serialBaudRate = pBaudRate;
	}
	
	/* OPEN SERIAL PORT.
	 * @param:			None.
	 * @return error: 	false if serial port was successfully opened, true otherwise.
	 */
	public boolean open() {
		boolean error = false;
		CommPortIdentifier portId = null;
		// Get all the COM ports currently connected.
		@SuppressWarnings("rawtypes")
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		// Check if 'numPort' is connected.
		System.out.print("List of serial ports:");
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			System.out.print(" " + currPortId.getName());
			if (currPortId.getName().equals(serialNumPort)) {
				portId = currPortId;
			}
		}
		System.out.print(".\n" + serialName + " serial port opening: ");
		if (portId == null) {
			System.out.println("failed.");
		}
		else {
			try {
				// Open serial port
				serialPort = (SerialPort) portId.open(this.getClass().getName(), SERIAL_TIME_OUT);
				serialPort.setSerialPortParams(serialBaudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				// Input and output streams
				serialInput = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
				serialOutput = serialPort.getOutputStream();
				System.out.println("OK.");
			}
			catch (PortInUseException e) {
				System.out.println("allready in use !");
				error = true;
			}
			catch (Exception e) {
				System.err.println(e.toString());
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
	    	System.out.println("Send data '" + Integer.toBinaryString(pByteToSend) + "' = " + pByteToSend + " to " + serialName);
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
					Main.fillRxBuffer(rxByte);
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
