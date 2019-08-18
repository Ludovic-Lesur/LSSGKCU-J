/**
 * Javadoc
 * 
 * @author Ludovic Lesur
 * @since 25/07/2019
 */

package sounds;

import peripherals.*;

public class Zba {

	/* Data members */
	private Sound zbaTurnOn;
	
	/* CONSTRUCTOR FOR CLASS ZBA.
	 * @param:	None.
	 * @return: None.
	 */
	public Zba() {
		// Init sound.
		zbaTurnOn = new Sound("zba", 1.0);
	}
	
	/* TURN ZBA ON.
	 * @param:	None.
	 * @return:	None.
	 */
	public void on() {
		zbaTurnOn.play();
	}
	
	/* TURN ZBA OFF.
	 * @param:	None.
	 * @return:	None.
	 */
	public void off() {
		zbaTurnOn.stop();
	}
}
