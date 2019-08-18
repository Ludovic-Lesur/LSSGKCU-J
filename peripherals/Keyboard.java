/**
 * Javadoc
 * 
 * @author Ludovic Lesur
 * @since 25/07/2019
 */

package peripherals;

import java.awt.*;

public class Keyboard {

	/* Data members */
	private Robot keyboardRobot;

	/* CONSTRUCTOR FOR CLASS KEYBOARD.
	 * @param:	None.
	 * @return: None.
	 */
	public Keyboard() {
		try {
			keyboardRobot = new Robot();
		}
		catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	/* SIMULATE A KEY PRESS.
	 * @param pKey:	Char to press.
	 * @return 		None.
	 */
	public void write(char pKey) {
		try {
			keyboardRobot.keyPress(pKey);
			keyboardRobot.keyRelease(pKey);
		}
		catch (Exception e) {
			System.err.println(e.toString());
		}
	}
}
