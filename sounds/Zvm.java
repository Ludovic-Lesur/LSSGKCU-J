/**
 * Javadoc
 * 
 * @author Ludo
 * @since 25/07/2019
 */

package sounds;

import peripherals.*;

public class Zvm {

	/* Data members */
	// Sounds.
	private Sound zvmTurnOn;
	private Sound zvmOn1;
	private Sound zvmOn2;
	private Sound zvmTurnOff;
	// Internal state machine.
	private boolean zvmSwitch;
	private enum ZvmStateEnum {
		ZVM_STATE_OFF,
		ZVM_STATE_TURNON,
		ZVM_STATE_TURNON_TO_ON1,
		ZVM_STATE_ON1,
		ZVM_STATE_ON1_TO_ON2,
		ZVM_STATE_ON2,
		ZVM_STATE_ON2_TO_ON1,
		ZVM_STATE_TURNOFF};
	private ZvmStateEnum zvmState;
	// Macros.
	private static final int ZVM_FADE_DURATION_MS = 3000;
	private static final int ZVM_FADE_OFF_DURATION_MS = 1000;
	private static final int ZVM_FADE_MARGIN_MS = 1000; // Added to fade duration.
	
	/* CONSTRUCTOR FOR CLASS ZVM.
	 * @param:	None.
	 * @return: None.
	 */
	public Zvm() {
		// Init sounds.
		zvmTurnOn = new Sound("ventilateurs_allumage", 1.0);
		zvmOn1 = new Sound("ventilateurs_marche", 1.0);
		zvmOn2 = new Sound("ventilateurs_marche", 1.0);
		zvmTurnOff = new Sound("ventilateurs_extinction", 1.0);
		// Init state machine.
		zvmSwitch = false;
		zvmState = ZvmStateEnum.ZVM_STATE_OFF;
	}
	
	/* TURN MOTOR FANS ON.
	 * @param:	None.
	 * @return:	None.
	 */
	public void on() {
		zvmSwitch = true;
		System.out.println("ZVM on.");
	}
	
	/* TURN MOTOR FANS OFF.
	 * @param:	None.
	 * @return:	None.
	 */
	public void off() {
		zvmSwitch = false;
		System.out.println("ZVM off.");
	}
	
	/* MAIN TASK OF MOTOR FANS SOUND.
	 * @param:	None.
	 * @return:	None.
	 */
	public void task() {
		/* Perform internal state machine */
		switch (zvmState) {
		case ZVM_STATE_OFF:
			if (zvmSwitch == true) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNON;
				// Start playing turn-on sound.
				zvmTurnOn.setVolume(1.0); // Fade-in effect is allready integrated in the sound itself.
				zvmTurnOn.play();
			}
			break;
		case ZVM_STATE_TURNON:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveFadeParameters();
				zvmOn1.saveFadeParameters();
				zvmOn2.saveFadeParameters();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.play();
				zvmTurnOff.saveFadeParameters();
			}
			else {
				if (zvmTurnOn.getPositionMs() > (zvmTurnOn.getDurationMs() - (ZVM_FADE_DURATION_MS + ZVM_FADE_MARGIN_MS))) {
					zvmState = ZvmStateEnum.ZVM_STATE_TURNON_TO_ON1;
					// Save turn-on volume.
					zvmTurnOn.saveFadeParameters();
					// Start playing On1 sound.
					zvmOn1.setVolume(0.0);
					zvmOn1.play();
					zvmOn1.saveFadeParameters();
					// Stop playing turn-off sound (in case it was running).
					zvmTurnOff.stop();
				}
			}
			break;
		case ZVM_STATE_TURNON_TO_ON1:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveFadeParameters();
				zvmOn1.saveFadeParameters();
				zvmOn2.saveFadeParameters();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.play();
				zvmTurnOff.saveFadeParameters();
			}
			else {
				// Perform turn-on fade-out and On1 fade-in. 
				int on1FadeEnd = zvmOn1.fadeIn(ZVM_FADE_DURATION_MS);
				int turnOnFadeEnd = zvmTurnOn.fadeOut(ZVM_FADE_DURATION_MS);
				// Change state when effect is complete.
				if ((on1FadeEnd > 0) && (turnOnFadeEnd > 0)) {
					zvmState = ZvmStateEnum.ZVM_STATE_ON1;
					// Stop turn-on sound.
					zvmTurnOn.stop();
				}
			}
			break;
		case ZVM_STATE_ON1:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveFadeParameters();
				zvmOn1.saveFadeParameters();
				zvmOn2.saveFadeParameters();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.play();
				zvmTurnOff.saveFadeParameters();
			}
			else {
				if (zvmOn1.getPositionMs() > (zvmOn1.getDurationMs() - (ZVM_FADE_DURATION_MS + ZVM_FADE_MARGIN_MS))) {
					zvmState = ZvmStateEnum.ZVM_STATE_ON1_TO_ON2;
					// Save On1 volume.
					zvmOn1.saveFadeParameters();
					// Start playing On2 sound.
					zvmOn2.setVolume(0.0);
					zvmOn2.play();
					zvmOn2.saveFadeParameters();
				}
			}
			break;
		case ZVM_STATE_ON1_TO_ON2:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveFadeParameters();
				zvmOn1.saveFadeParameters();
				zvmOn2.saveFadeParameters();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.play();
				zvmTurnOff.saveFadeParameters();
			}
			else {
				// Perform On1 fade-out and On2 fade-in.
				int on2FadeEnd = zvmOn2.fadeIn(ZVM_FADE_DURATION_MS);
				int on1FadeEnd = zvmOn1.fadeOut(ZVM_FADE_DURATION_MS);
				// Change state when effect is complete.
				if ((on2FadeEnd > 0) && (on1FadeEnd > 0)) {
					zvmState = ZvmStateEnum.ZVM_STATE_ON2;
					// Stop On1 sound.
					zvmOn1.stop();
				}
			}
			break;
		case ZVM_STATE_ON2:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveFadeParameters();
				zvmOn1.saveFadeParameters();
				zvmOn2.saveFadeParameters();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.play();
				zvmTurnOff.saveFadeParameters();
			}
			else {
				if (zvmOn2.getPositionMs() > (zvmOn2.getDurationMs() - (ZVM_FADE_DURATION_MS + ZVM_FADE_MARGIN_MS))) {
					zvmState = ZvmStateEnum.ZVM_STATE_ON2_TO_ON1;
					// Save On2 volume.
					zvmOn2.saveFadeParameters();
					// Start playing On1 sound.
					zvmOn1.setVolume(0.0);
					zvmOn1.play();
					zvmOn1.saveFadeParameters();
				}
			}
			break;
		case ZVM_STATE_ON2_TO_ON1:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveFadeParameters();
				zvmOn1.saveFadeParameters();
				zvmOn2.saveFadeParameters();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.play();
				zvmTurnOff.saveFadeParameters();
			}
			else {
				// Perform On2 fade-out and On1 fade-in.
				int on1FadeEnd = zvmOn1.fadeIn(ZVM_FADE_DURATION_MS);
				int on2FadeEnd = zvmOn2.fadeOut(ZVM_FADE_DURATION_MS);
				// Change state when effect is complete.
				if ((on1FadeEnd > 0) && (on2FadeEnd > 0)) {
					zvmState = ZvmStateEnum.ZVM_STATE_ON1;
					// Stop On2 sound.
					zvmOn2.stop();
				}
			}
			break;
		case ZVM_STATE_TURNOFF:
			// Perform On1, On2 and turn-on fade-out and turn-off fade-in.
			int turnOffFadeEnd = zvmTurnOff.fadeIn(ZVM_FADE_OFF_DURATION_MS);
			int turnOnFadeEnd = zvmTurnOn.fadeOut(ZVM_FADE_OFF_DURATION_MS);
			int on1FadeEnd = zvmOn1.fadeOut(ZVM_FADE_OFF_DURATION_MS);
			int on2FadeEnd = zvmOn2.fadeOut(ZVM_FADE_OFF_DURATION_MS);
			// Change state when effect is complete.
			if ((turnOffFadeEnd > 0) && (turnOnFadeEnd > 0) && (on1FadeEnd > 0) && (on2FadeEnd > 0)) {
				zvmState = ZvmStateEnum.ZVM_STATE_OFF;
				// Stop TurnOn, On1 and On2 sounds.
				zvmTurnOn.stop();
				zvmOn1.stop();
				zvmOn2.stop();
			}
			break;
		// Unknown state.
		default:
			break;
		}
	}
}
