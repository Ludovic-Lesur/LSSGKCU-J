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
	private enum ZvmStateEnum {ZVM_STATE_OFF, ZVM_STATE_TURNON, ZVM_STATE_TURNON_TO_ON1, ZVM_STATE_ON1, ZVM_STATE_ON1_TO_ON2, ZVM_STATE_ON2, ZVM_STATE_ON2_TO_ON1, ZVM_STATE_TURNOFF};
	private ZvmStateEnum zvmState;
	private static final int ZVM_FADE_DURATION_MS = 2000;
	private static final double ZVM_FADE_END_THRESHOLD = 1.0;
	
	private long n;
	
	/* COMPUTE FADE-IN VOLUME.
	 * @param pPosition:		Current sound position in milliseconds.
	 * @param pStartVolume:		Sound volume at the beginning of the fade effect.
	 * @return fadeInVolume:	Computed volume for fade-in effect.
	 */
	private double computeFadeInVolume(int pPosition, double pStartVolume) {
		double radius = 1.01;
		double fadeVolume = (double) Math.sqrt(Math.pow(radius, 2) - (Math.pow((pPosition - ZVM_FADE_DURATION_MS), 2) / Math.pow(ZVM_FADE_DURATION_MS, 2)));
		if (fadeVolume < pStartVolume) {
			fadeVolume = pStartVolume;
		}
		fadeVolume = 1.01;
		return fadeVolume;
	}
	
	/* COMPUTE FADE-OUT VOLUME.
	 * @param pPosition:		Current sound position in milliseconds.
	 * @param pStartVolume:		Sound volume at the beginning of the fade effect.
	 * @return fadeInVolume:	Computed volume for fade-out effect.
	 */
	private double computeFadeOutVolume(int pPosition, double pStartVolume) {
		double radius = 1.01;
		double fadeVolume = (double) Math.sqrt(Math.pow(radius, 2) - (Math.pow(pPosition, 2) / Math.pow(ZVM_FADE_DURATION_MS, 2)));
		if (fadeVolume > pStartVolume) {
			fadeVolume = pStartVolume;
		}
		fadeVolume = 0.0;
		return fadeVolume;
	}
	
	/* CONSTRUCTOR FOR CLASS ZVM.
	 * @param:	None.
	 * @return: None.
	 */
	public Zvm() {
		// Init sounds.
		zvmTurnOn = new Sound("ventilateurs_allumage", 1.0);
		zvmTurnOn.setVolume(0.0);
		zvmOn1 = new Sound("ventilateurs_marche", 1.0);
		zvmOn1.setVolume(0.0);
		zvmOn2 = new Sound("ventilateurs_marche", 1.0);
		zvmOn2.setVolume(0.0);
		zvmTurnOff = new Sound("ventilateurs_extinction", 1.0);
		zvmTurnOff.setVolume(0.0);
		// Init state machine.
		zvmSwitch = false;
		zvmState = ZvmStateEnum.ZVM_STATE_OFF;
		
		n = 0;
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
		// Off.
		case ZVM_STATE_OFF:
			if (zvmSwitch == true) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNON;
				// Start playing turn-on sound.
				zvmTurnOn.setVolume(1.0); // Fade-in effect is allready integrated in the sound itself.
				zvmTurnOn.play();
				System.out.println("Start TurnOn");
			}
			break;
		// Turn-on.
		case ZVM_STATE_TURNON:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveVolume();
				zvmOn1.saveVolume();
				zvmOn2.saveVolume();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.saveVolume();
				zvmTurnOff.play();
			}
			else {
				if (zvmTurnOn.getPosition() > (zvmTurnOn.getDuration() - ZVM_FADE_DURATION_MS)) {
					zvmState = ZvmStateEnum.ZVM_STATE_TURNON_TO_ON1;
					// Save turn-on volume.
					zvmTurnOn.saveVolume();
					// Start playing On1 sound.
					zvmOn1.setVolume(0.0);
					zvmOn1.saveVolume();
					zvmOn1.play();
					System.out.println("Start On1");
					// Stop playing turn-off sound (in case it was running).
					zvmTurnOff.stop();
				}
			}
			break;
		// Turn-on to On1.
		case ZVM_STATE_TURNON_TO_ON1:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveVolume();
				zvmOn1.saveVolume();
				zvmOn2.saveVolume();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.saveVolume();
				zvmTurnOff.play();
			}
			else {
				// Perform turn-on fade-out and On1 fade-in. 
				double on1Volume = this.computeFadeInVolume(zvmOn1.getPosition(), zvmOn1.getLastSavedVolume());
				double turnOnVolume = this.computeFadeOutVolume(zvmOn1.getPosition(), zvmTurnOn.getLastSavedVolume());
				zvmOn1.setVolume(on1Volume);
				zvmTurnOn.setVolume(turnOnVolume);
				System.out.println("TurnOn=" + (turnOnVolume) + " On1=" + on1Volume);
				// Change state when effect is complete.
				if (on1Volume >= ZVM_FADE_END_THRESHOLD) {
					zvmState = ZvmStateEnum.ZVM_STATE_ON1;
					// Stop turn-on sound.
					zvmTurnOn.stop();
				}
			}
			break;
		// On1.
		case ZVM_STATE_ON1:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveVolume();
				zvmOn1.saveVolume();
				zvmOn2.saveVolume();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.saveVolume();
				zvmTurnOff.play();
			}
			else {
				if (zvmOn1.getPosition() > (zvmOn1.getDuration() - ZVM_FADE_DURATION_MS)) {
					zvmState = ZvmStateEnum.ZVM_STATE_ON1_TO_ON2;
					// Save On1 volume.
					zvmOn1.saveVolume();
					// Start playing On2 sound.
					zvmOn2.setVolume(0.0);
					zvmOn2.saveVolume();
					zvmOn2.play();
					System.out.println("Start On2");
				}
			}
			break;
		// On1 to On2.
		case ZVM_STATE_ON1_TO_ON2:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveVolume();
				zvmOn1.saveVolume();
				zvmOn2.saveVolume();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.saveVolume();
				zvmTurnOff.play();
			}
			else {
				// Perform On1 fade-out and On2 fade-in.
				double on2Volume = this.computeFadeInVolume(zvmOn2.getPosition(), zvmOn2.getLastSavedVolume());
				double on1Volume = this.computeFadeOutVolume(zvmOn2.getPosition(), zvmOn1.getLastSavedVolume());
				zvmOn2.setVolume(on2Volume);
				zvmOn1.setVolume(on1Volume);
				System.out.println("On1=" + (on1Volume) + " On2=" + on2Volume);
				// Change state when effect is complete.
				if (on2Volume > ZVM_FADE_END_THRESHOLD) {
					zvmState = ZvmStateEnum.ZVM_STATE_ON2;
					// Stop On1 sound.
					zvmOn1.stop();
				}
			}
			break;
		// On2.
		case ZVM_STATE_ON2:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveVolume();
				zvmOn1.saveVolume();
				zvmOn2.saveVolume();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.saveVolume();
				zvmTurnOff.play();
			}
			else {
				if (zvmOn2.getPosition() > (zvmOn2.getDuration() - ZVM_FADE_DURATION_MS)) {
					zvmState = ZvmStateEnum.ZVM_STATE_ON2_TO_ON1;
					// Save On2 volume.
					zvmOn2.saveVolume();
					// Start playing On1 sound.
					zvmOn1.setVolume(0.0);
					zvmOn1.saveVolume();
					zvmOn1.play();
					System.out.println("Start On1");
				}
			}
			break;
		// On2 to On1.
		case ZVM_STATE_ON2_TO_ON1:
			if (zvmSwitch == false) {
				zvmState = ZvmStateEnum.ZVM_STATE_TURNOFF;
				// Save all other sounds volume.
				zvmTurnOn.saveVolume();
				zvmOn1.saveVolume();
				zvmOn2.saveVolume();
				// Start playing turn-off sound.
				zvmTurnOff.setVolume(0.0);
				zvmTurnOff.saveVolume();
				zvmTurnOff.play();
			}
			else {
				// Perform On2 fade-out and On1 fade-in.
				double on1Volume = this.computeFadeInVolume(zvmOn1.getPosition(), zvmOn1.getLastSavedVolume());
				double on2Volume = this.computeFadeOutVolume(zvmOn1.getPosition(), zvmOn2.getLastSavedVolume());
				zvmOn1.setVolume(on1Volume);
				zvmOn2.setVolume(on2Volume);
				System.out.println("On2=" + (on2Volume) + " On1=" + on1Volume);
				// Change state when effect is complete.
				if (on1Volume > ZVM_FADE_END_THRESHOLD) {
					zvmState = ZvmStateEnum.ZVM_STATE_ON1;
					// Stop On2 sound.
					zvmOn2.stop();
				}
			}
			break;
		// Turn-off.
		case ZVM_STATE_TURNOFF:
			// Perform On1, On2 and turn-on fade-out and turn-off fade-in.
			double turnOffVolume = this.computeFadeInVolume(zvmTurnOff.getPosition(), zvmTurnOff.getLastSavedVolume());
			double turnOnVolume = this.computeFadeOutVolume(zvmTurnOff.getPosition(), zvmTurnOn.getLastSavedVolume());
			double on1Volume = this.computeFadeOutVolume(zvmTurnOff.getPosition(), zvmOn1.getLastSavedVolume());
			double on2Volume = this.computeFadeOutVolume(zvmTurnOff.getPosition(), zvmOn2.getLastSavedVolume());
			zvmTurnOff.setVolume(turnOffVolume);
			zvmTurnOn.setVolume(turnOnVolume);
			zvmOn1.setVolume(on1Volume);
			zvmOn2.setVolume(on2Volume);
			if (System.currentTimeMillis() > n) {
				System.out.println("position=" + zvmTurnOff.getPosition() + " TurnOn=" + turnOnVolume + " On1=" + on1Volume + " On2=" + on2Volume + " TurnOff=" + turnOffVolume);
				n = System.currentTimeMillis() + 100;
			}
			
			// Change state when effect is complete.
			if (turnOffVolume > ZVM_FADE_END_THRESHOLD) {
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
