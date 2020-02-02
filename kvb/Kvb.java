/**
 * Javadoc
 * 
 * @author Ludo
 * @since 01/02/2020
 */

package kvb;

import java.io.*;
import peripherals.*;
import lsmcu.*;
import main.*;

public class Kvb {

	/* Data members */
	// Sounds.
	private Sound kvbTurnOn;
	private Sound kvbUrgency;
	// State machine.
	private enum KvbStateEnum {
		KVB_STATE_OFF,
		KVB_STATE_PA400,
		KVB_STATE_PA400_OFF,
		KVB_STATE_UC512,
		KVB_STATE_888888,
		KVB_STATE_WAIT_VALIDATION,
		KVB_STATE_IDLE};
	private KvbStateEnum kvbState;
	private long kvbStateSwitchTime;
	// Inputs.
	private boolean kvbBlUnlocked;
	private boolean kvbBpvalPressed;
	private int kvbCurrentSpeedKmh;
	private int kvbSpeedLimitKmh;
	// Macros.
	private static final int KVB_PA400_DURATION_MS = 2000;
	private static final int KVB_PA400_OFF_DURATION_MS = 2000;
	private static final int KVB_UC512_DURATION_MS = 2000;
	private static final int KVB_888888_DURATION_MS = 3000;
	
	/* CONSTRUCTOR FOR CLASS KVB.
	 * @param:	None.
	 * @return: None.
	 */
	public Kvb() {
		// Init sounds.
		kvbTurnOn = new Sound("kvbTurnOn", 1.0);
		kvbUrgency = new Sound("kvbUrgency", 1.0);
		// Init state machine.
		kvbState = KvbStateEnum.KVB_STATE_OFF;
		kvbStateSwitchTime = 0;
		// Init inputs.
		kvbBlUnlocked = false;
		kvbCurrentSpeedKmh = 0;
		kvbSpeedLimitKmh = 0;
	}
	
	/* SET BL LOCK STATE.
	 * @param pBlUnlocked:	BL lock state (true for unlocked, false for locked).
	 * @return:				None.
	 */
	public void setBlLockState(boolean pBlUnlocked) {
		kvbBlUnlocked = pBlUnlocked;
	}
	
	/* SET BPVAL STATE.
	 * @param pBpvalPressed:	BPVAL state (true is pressed, false otherwise).
	 * @return:					None.
	 */
	public void setBpValState(boolean pBpvalPressed) {
		kvbBpvalPressed = pBpvalPressed;
	}
	
	/* SET CURRENT TRAIN SPEED.
	 * @param pCurrentSpeedKmh:	Current train speed retrieved from game (in km/h).
	 * @return:					None.
	 */
	public void setCurrentSpeed(int pCurrentSpeedKmh) {
		kvbCurrentSpeedKmh = pCurrentSpeedKmh;
	}
	
	/* SET CURRENT SPEED LIMIT.
	 * @param pSpeedLimitKmh:	Current speed limit retrieved from game (in km/h).
	 * @return:					None.
	 */
	public void setSpeedLimit(int pSpeedLimitKmh) {
		kvbSpeedLimitKmh = pSpeedLimitKmh;
	}
	
	/* MAIN TASK OF KVB CALCULATOR.
	 * @param:	None.
	 * @return:	None.
	 */
	public void task() throws IOException {
		/* Perform internal state machine */
		switch (kvbState) {
		case KVB_STATE_OFF:
			if (kvbBlUnlocked == true) {
				// Play turn-on sound.
				kvbTurnOn.play();
				// Start KVB init.
				Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_YG_PA400.getNumber());
				Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_LSSF_BLINK.getNumber());
				kvbState = KvbStateEnum.KVB_STATE_PA400;
				kvbStateSwitchTime = System.currentTimeMillis();
			}
			break;
		case KVB_STATE_PA400:
			// Check BL state.
			if (kvbBlUnlocked == false) {
				Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_ALL_OFF.getNumber());
				kvbState = KvbStateEnum.KVB_STATE_OFF;
			}
			else {
				// Wait PA400 display duration.
				if (System.currentTimeMillis() > (kvbStateSwitchTime + KVB_PA400_DURATION_MS)) {
					Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_YG_OFF.getNumber());
					kvbState = KvbStateEnum.KVB_STATE_PA400_OFF;
					kvbStateSwitchTime = System.currentTimeMillis();
				}
			}
			break;
		case KVB_STATE_PA400_OFF:
			// Check BL state.
			if (kvbBlUnlocked == false) {
				Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_ALL_OFF.getNumber());
				kvbState = KvbStateEnum.KVB_STATE_OFF;
			}
			else {
				// Wait transition duration.
				if (System.currentTimeMillis() > (kvbStateSwitchTime + KVB_PA400_OFF_DURATION_MS)) {
					Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_YG_UC512.getNumber());
					kvbState = KvbStateEnum.KVB_STATE_UC512;
					kvbStateSwitchTime = System.currentTimeMillis();
				}
			}
			break;
		case KVB_STATE_UC512:
			// Check BL state.
			if (kvbBlUnlocked == false) {
				Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_ALL_OFF.getNumber());
				kvbState = KvbStateEnum.KVB_STATE_OFF;
			}
			else {
				// Wait for UC512 display duration.
				if (System.currentTimeMillis() > (kvbStateSwitchTime + KVB_UC512_DURATION_MS)) {
					Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_YG_888.getNumber());
					Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_LVAL_BLINK.getNumber());
					kvbState = KvbStateEnum.KVB_STATE_888888;
					kvbStateSwitchTime = System.currentTimeMillis();
				}
			}
			break;
		case KVB_STATE_888888:
			// Check BL state.
			if (kvbBlUnlocked == false) {
				Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_ALL_OFF.getNumber());
				kvbState = KvbStateEnum.KVB_STATE_OFF;
			}
			else {
				// Wait for 888888 display duration.
				if (System.currentTimeMillis() > (kvbStateSwitchTime + KVB_888888_DURATION_MS)) {
					Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_YG_OFF.getNumber());
					kvbState = KvbStateEnum.KVB_STATE_WAIT_VALIDATION;
					kvbStateSwitchTime = System.currentTimeMillis();
				}
			}
			break;
		case KVB_STATE_WAIT_VALIDATION:
			// Check BL state.
			if (kvbBlUnlocked == false) {
				Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_ALL_OFF.getNumber());
				kvbState = KvbStateEnum.KVB_STATE_OFF;
			}
			else {
				// Check BPVAL.
				if (kvbBpvalPressed == true) {
					// Parameters validated, go to idle state.
					kvbState = KvbStateEnum.KVB_STATE_IDLE;
				}
			}
			break;
		case KVB_STATE_IDLE:
			// Check BL state.
			if (kvbBlUnlocked == false) {
				Main.lsmcu.sendCommand(Outputs.LSMCU_IN_KVB_ALL_OFF.getNumber());
				kvbState = KvbStateEnum.KVB_STATE_OFF;
			}
		default:
			break;
		}
	}
}
