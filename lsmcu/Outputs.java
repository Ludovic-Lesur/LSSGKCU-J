/**
 * Javadoc
 * 
 * @author Ludovic Lesur
 * @since 25/07/2019
 */

package lsmcu;

public enum Outputs {
	// KVB lights.
	LSMCU_IN_KVB_LVAL_BLINK,
	LSMCU_IN_KVB_LVAL_ON,
	LSMCU_IN_KVB_LVAL_OFF,
	LSMCU_IN_KVB_LMV_ON,
	LSMCU_IN_KVB_LMV_OFF,
	LSMCU_IN_KVB_LFC_ON,
	LSMCU_IN_KVB_LFC_OFF,
	LSMCU_IN_KVB_LV_ON,
	LSMCU_IN_KVB_LV_OFF,
	LSMCU_IN_KVB_LFU_ON,
	LSMCU_IN_KVB_LFU_OFF,
	LSMCU_IN_KVB_LPS_ON,
	LSMCU_IN_KVB_LPS_OFF,
	LSMCU_IN_KVB_LSSF_BLINK,
	LSMCU_IN_KVB_LSSF_ON,
	LSMCU_IN_KVB_LSSF_OFF,
	// KVB 7-segments displays.
	LSMCU_IN_KVB_YG_OFF,
	LSMCU_IN_KVB_YG_PA400,
	LSMCU_IN_KVB_YG_UC512,
	LSMCU_IN_KVB_YG_888,
	LSMCU_IN_KVB_YG_DASH,
	LSMCU_IN_KVB_G_B,
	LSMCU_IN_KVB_Y_B,
	LSMCU_IN_KVB_G_P,
	LSMCU_IN_KVB_Y_P,
	LSMCU_IN_KVB_G_L,
	LSMCU_IN_KVB_Y_L,
	LSMCU_IN_KVB_G_00,
	LSMCU_IN_KVB_Y_00,
	LSMCU_IN_KVB_G_000,
	LSMCU_IN_KVB_Y_000;
	
	/* Data members */
	private final int commandNumber;
	
	private static class CommandCounterHolder {
		// Bytes 0 to 160 are reserved for coding speed in km/h.
		private static int commandCounter = 161;
	}
	
	/* CONSTRUCTOR OF EACH ELEMENT OF ENUMERATION.
	 * @param:	None.
	 * @return:	None.
	 */
	private Outputs() {
		commandNumber = CommandCounterHolder.commandCounter;
		CommandCounterHolder.commandCounter++;
	}
	
	/* GET THE ENUMERATION ITEM NUMBER.
	 * @param:					None.
	 * @return commandNumber:	Command number.
	 */
	public final int getNumber() {
		return commandNumber;
	}
}
