/**
 * Javadoc
 * 
 * @author Ludovic Lesur
 * @since 25/07/2018
 */

package peripherals;

import java.io.*;
import javax.sound.sampled.*;

public class Sound {
	
	/* Data members */
	private Clip soundClip;
	private AudioInputStream soundInputStream;
	private static final String SOUND_WAV_FILES_PATH = "wav/";
	private static final String SOUND_WAV_EXTENSION = ".wav";
	private double soundVolumeMax;
	
	/* CONSTRUCTOR FOR CLASS SOUND.
	 * @param pName:		Name of the file (without path and extension).
	 * @param pVolumeMax:	Maximum volume of the sound (between 0.0 and 1.0).
	 * @return: 			None.
	 */
	public Sound(String pName, double pVolumeMax) {
		// Ensure volume max is between 0.0 and 1.0.
		soundVolumeMax = pVolumeMax;
		if (soundVolumeMax > 1.0) soundVolumeMax = 1.0;
		if (soundVolumeMax < 0.0) soundVolumeMax = 0.0;
		// Create clip.
		String wavFile = SOUND_WAV_FILES_PATH + pName + SOUND_WAV_EXTENSION;
		try {
			soundClip = AudioSystem.getClip();
			soundInputStream = AudioSystem.getAudioInputStream(new File(wavFile));
			soundClip.open(soundInputStream);
			System.out.println("Audio file " + wavFile + " successfully opened.");
		}
		catch (IOException e) {
			System.err.println("Audio file " + wavFile + " not found.");
		}
		catch (Exception e){
			System.err.println(e.toString());
		}
	}
	
	/* PLAY THE SOUND
	 * @param:	None.
	 * @return: None.
	 */
	public void play() {
		soundClip.setFramePosition(0);
		soundClip.loop(0);
		soundClip.start();
	}
	
	/* STOP THE SOUND
	 * @param:	None.
	 * @return: None.
	 */
	public void stop() {
		soundClip.stop();
	}
	
	/* SET THE SOUND VOLUME.
	 * @param pVolume:	Sound volume (between 0.0 and 1.0). 1.0 correspondonds to soundVolumeMax.
	 * @return: 		None.
	 */
	public void setVolume(double pVolume) {
	    if ((pVolume >= 0f) && (pVolume <= 1f)) {
	    	// Apply maximum range.
	    	double realVolume = pVolume * soundVolumeMax;
	    	// Set volume.
	    	FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);        
		    gainControl.setValue(20f * (float) Math.log10(realVolume));
	    }
	}
}
