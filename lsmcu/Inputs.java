/**
 * Javadoc
 * 
 * @author Ludo
 * @since 31/03/2018
 */

package lsmcu;

public enum Inputs {
	LSMCU_OUT_ZBA_ON, 			// Sound only.
	LSMCU_OUT_ZBA_OFF, 			// Sound only.
	LSMCU_OUT_RSEC_ON, 			// Sound only.
	LSMCU_OUT_RSEC_OFF, 		// Sound only.
	LSMCU_OUT_ZDV_ON, 			// Sound + Keyboard.
	LSMCU_OUT_ZDV_OFF,			// Sound + Keyboard.
	LSMCU_OUT_ZPT_BACK_UP,		// Sound + Keyboard.
	LSMCU_OUT_ZPT_BACK_DOWN,	// Sound + Keyboard.
	LSMCU_OUT_ZPT_FRONT_UP,		// Sound + Keyboard.
	LSMCU_OUT_ZPT_FRONT_DOWN,	// Sound + Keyboard.
	LSMCU_OUT_ZDJ_OFF,			// Sound + Keyboard.
	LSMCU_OUT_ZEN_ON,			// Sound + Keyboard.
	LSMCU_OUT_ZCAZCD_STATE0,	// Sound only.
	LSMCU_OUT_ZCAZCD_STATE1,	// Sound only.
	LSMCU_OUT_ZCAZCD_STATE2,	// Sound only.
	LSMCU_OUT_ZCAZCD_STATE3,	// Sound only.
	LSMCU_OUT_ZCAZCD_STATE4,	// Sound only.
	LSMCU_OUT_FPB_ON,			// Sound + Keyboard.
	LSMCU_OUT_FPB_OFF,			// Sound + Keyboard.
	LSMCU_OUT_FPB_APPLY,		// Sound + Keyboard.
	LSMCU_OUT_FPB_NEUTRAL,		// Sound + Keyboard.
	LSMCU_OUT_FPB_RELEASE,		// Sound + Keyboard.
	LSMCU_OUT_BPGD,				// Sound only.
	LSMCU_OUT_ZVM_ON,			// Sound only.
	LSMCU_OUT_ZVM_OFF,			// Sound only.
	LSMCU_OUT_INV_FORWARD,		// Sound + Keyboard.
	LSMCU_OUT_INV_NEUTRAL,		// Sound + Keyboard.
	LSMCU_OUT_INV_BACKWARD,		// Sound + Keyboard.
	LSMCU_OUT_MP_0,				// Sound + Keyboard.
	LSMCU_OUT_MP_T_MORE,		// Sound + Keyboard.
	LSMCU_OUT_MP_T_LESS,		// Sound + Keyboard.
	LSMCU_OUT_MP_F_MORE,		// Sound + Keyboard.
	LSMCU_OUT_MP_F_LESS,		// Sound + Keyboard.
	LSMCU_OUT_MP_PR,			// Sound + Keyboard.
	LSMCU_OUT_MP_P,				// Sound + Keyboard.
	LSMCU_OUT_FD_APPLY,			// Sound + Keyboard.
	LSMCU_OUT_FD_NEUTRAL,		// Sound + Keyboard.
	LSMCU_OUT_FD_RELEASE,		// Sound + Keyboard.
	LSMCU_OUT_BPURG,			// Sound + Keyboard.
	LSMCU_OUT_S_HIGH_TONE,		// Sound only.
	LSMCU_OUT_S_LOW_TONE,		// Sound only.
	LSMCU_OUT_S_NEUTRAL,		// Sound only.
	LSMCU_OUT_BPEV_ON,			// Keyboard only.
	LSMCU_OUT_BPEV_OFF,			// Keyboard only.
	LSMCU_OUT_BPSA_ON,			// Keyboard only.
	LSMCU_OUT_BPSA_OFF,			// Keyboard only.
	LSMCU_OUT_ZFG_ON,			// Keyboard only.
	LSMCU_OUT_ZFG_OFF,			// Keyboard only.
	LSMCU_OUT_ZFD_ON,			// Keyboard only.
	LSMCU_OUT_ZFD_OFF,			// Keyboard only.
	LSMCU_OUT_ZPR_ON,			// Keyboard only.
	LSMCU_OUT_ZPR_OFF,			// Keyboard only.
	LSMCU_OUT_ZLFRG_ON,			// Keyboard only.
	LSMCU_OUT_ZLFRG_OFF,		// Keyboard only.
	LSMCU_OUT_ZLFRD_ON,			// Keyboard only.
	LSMCU_OUT_ZLFRD_OFF,		// Keyboard only.
	LSMCU_OUT_ACSF_ON,			// KVB only.
	LSMCU_OUT_ACSF_OFF,			// KVB only.
	LSMCU_OUT_KVB_BPVAL_ON,		// KVB only.
	LSMCU_OUT_KVB_BPVAL_OFF,	// KVB only.
	LSMCU_OUT_KVB_BPMV_ON,		// KVB only.
	LSMCU_OUT_KVB_BPMV_OFF,		// KVB only.
	LSMCU_OUT_KVB_BPFC_ON,		// KVB only.
	LSMCU_OUT_KVB_BPFC_OFF,		// KVB only.
	LSMCU_OUT_KVB_BPTEST_ON,	// KVB only.
	LSMCU_OUT_KVB_BPTEST_OFF,	// KVB only.
	LSMCU_OUT_KVB_BPSF_ON,		// KVB only.
	LSMCU_OUT_KVB_BPSF_OFF;		// KVB only.
	
	/* Data members */
	private final int commandNumber;
	private static final int CP_MAX_VALUE_DECIBARS = 100;
	
	private static class CommandCounterHolder {
		// Bytes 0 to 100 are reserved for coding CP pressure*10 in bars.
		private static int commandCounter = CP_MAX_VALUE_DECIBARS + 1;
	}
	
	/* CONSTRUCTOR OF EACH ELEMENT OF ENUMERATION.
	 * @param:	None.
	 * @return:	None.
	 */
	private Inputs() {
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
