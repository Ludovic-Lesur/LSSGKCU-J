/**
 * Javadoc
 * 
 * @author Ludo
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
	private double soundCurrentVolume;
	private double soundLastSavedVolume;
	private int soundDurationMs;
	
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
		// Init members.
		soundCurrentVolume = 1.0;
		soundLastSavedVolume = 1.0;
		// Create full file name.
		String wavFile = SOUND_WAV_FILES_PATH + pName + SOUND_WAV_EXTENSION;
		try {
			// Create clip.
			soundClip = AudioSystem.getClip();
			soundInputStream = AudioSystem.getAudioInputStream(new File(wavFile));
			soundClip.open(soundInputStream);
			System.out.println("Audio file " + wavFile + " successfully opened.");
			// Save length.
			soundDurationMs = (int) (soundClip.getMicrosecondLength() / 1000);
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
		soundClip.stop();
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
		soundClip.setFramePosition(0);
	}
	
	/* GET THE CURRENT SOUND VOLUME.
	 * @param:						None.
	 * @return soundCurrentVolume:	Current volume of the sound (between 0.0 and 1.0). 1.0 correspondonds to soundVolumeMax.
	 */
	public double getVolume() {
	    return soundCurrentVolume;
	}
	
	/* SET THE SOUND VOLUME.
	 * @param pVolume:	Sound volume (between 0.0 and 1.0). 1.0 correspondonds to soundVolumeMax.
	 * @return: 		None.
	 */
	public void setVolume(double pVolume) {
		// Update current volume.
		soundCurrentVolume = pVolume;
		if (soundCurrentVolume > 1.0) soundCurrentVolume = 1.0;
		if (soundCurrentVolume < 0.0) soundCurrentVolume = 0.0;
    	// Apply maximum range.
    	double realVolume = soundCurrentVolume * soundVolumeMax;
    	// Set volume.
    	try {
    		FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);        
    	    gainControl.setValue(20f * (float) Math.log10(realVolume));
    	}
    	catch (Exception e){
    		
    	}
	}
	
	/* STORE THE CURRENT SOUND VOLUME.
	 * @param:	None.
	 * @return: None.
	 */
	public void saveVolume() {
		soundLastSavedVolume = soundCurrentVolume;
	}
	
	/* GET THE LAST SAVED VOLUME.
	 * @param:							None.
	 * @return soundLastSavedVolume:	Last volume saved with the previous method.
	 */
	public double getLastSavedVolume() {
		return soundLastSavedVolume;
	}
	
	/* GET THE SOUND DURATION.
	 * @param:	None.
	 * @return:	Sound duration in milliseconds.
	 */
	public int getDuration() {
		return soundDurationMs;
	}
	
	/* GET THE CURRENT POSITION WITHIN THE SOUND.
	 * @param:	None.
	 * @return:	Current position in milliseconds.
	 */
	public int getPosition() {
		return (int) (soundClip.getMicrosecondPosition() / 1000);
	}
}
