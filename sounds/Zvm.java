/**
 * Javadoc
 * 
 * @author Ludovic Lesur
 * @since 25/07/2019
 */

package sounds;

import peripherals.*;

public class Zvm {

	/* Data members */
	// Sounds.
	private Sound zvmTurnOn;
	private Sound zvmOn;
	private Sound zvmTurnOff;
	// Internal state machine.
	private boolean ZvmSwitch;
	private enum ZvmStateEnum {ZVM_OFF, ZVM_TURN_ON, ZVM_ON1, ZVM_ON12, ZVM_ON21, ZVM_ON2, ZVM_TURN_OFF};
	private ZvmStateEnum zvmState;
	
	/* CONSTRUCTOR FOR CLASS ZVM.
	 * @param:	None.
	 * @return: None.
	 */
	public Zvm() {
		// Init sounds.
		zvmTurnOn = new Sound("ventilateurs_allumage", 1.0);
		zvmOn = new Sound("ventilateurs_marche", 1.0);
		zvmTurnOff = new Sound("ventilateurs_extinction", 1.0);
		// Init state machine.
		ZvmSwitch = false;
		zvmState = ZvmStateEnum.ZVM_OFF;
	}
	
	/* TURN MOTOR FANS ON.
	 * @param:	None.
	 * @return:	None.
	 */
	public void on() {
		ZvmSwitch = true;
	}
	
	/* TURN MOTOR FANS OFF.
	 * @param:	None.
	 * @return:	None.
	 */
	public void off() {
		ZvmSwitch = false;
	}
	
	/* MAIN TASK OF MOTOR FANS SOUND.
	 * @param:	None.
	 * @return:	None.
	 */
	public void task() {
		/* Perform internal state machine */
		switch (zvmState) {
		// Off.
		case ZVM_OFF:
			if (ZvmSwitch == true) {
				zvmState = ZvmStateEnum.ZVM_TURN_ON;
			}
			break;
		// Turn on.
		case ZVM_TURN_ON:
			if (ZvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_TURN_OFF;
			}
			else {
				zvmTurnOn.setVolume(0.0);
			}
		// Unknown state.
		default:
			break;
		}
	}
}
