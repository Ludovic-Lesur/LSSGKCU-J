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
	private double soundMaxVolume;
	private double soundCurrentVolume;
	private int soundDurationMs;
	private int soundIsPlaying;
	// Fade effects.
	private enum SoundFadeEquationEnum {SOUND_FADE_EQUATION_ELLIPSE, SOUND_FADE_EQUATION_LINEAR};
	private SoundFadeEquationEnum soundFadeEquation;
	private double soundFadeStartVolume;
	private int soundFadeStartPositionMs;
	// Debug.
	private long m;
	private long n;
	private long p;
	
	/* CONSTRUCTOR FOR CLASS SOUND.
	 * @param pName:		Name of the file (without path and extension).
	 * @param pVolumeMax:	Maximum volume of the sound (between 0.0 and 1.0).
	 * @return: 			None.
	 */
	public Sound(String pName, double pVolumeMax) {
		// Ensure volume max is between 0.0 and 1.0.
		soundMaxVolume = pVolumeMax;
		if (soundMaxVolume > 1.0) soundMaxVolume = 1.0;
		if (soundMaxVolume < 0.0) soundMaxVolume = 0.0;
		// Init members.
		soundCurrentVolume = 0.0;
		soundFadeStartVolume = 0.0;
		soundFadeStartPositionMs = 0;
		soundIsPlaying = 0;
		soundFadeEquation = SoundFadeEquationEnum.SOUND_FADE_EQUATION_LINEAR;
		// Create full file name.
		String wavFile = SOUND_WAV_FILES_PATH + pName + SOUND_WAV_EXTENSION;
		try {
			// Create clip.
			soundClip = AudioSystem.getClip();
			soundInputStream = AudioSystem.getAudioInputStream(new File(wavFile));
			soundClip.open(soundInputStream);
			System.out.println("SOUND *** Audio file " + wavFile + " successfully opened.");
			// Save length.
			soundDurationMs = (int) (soundClip.getMicrosecondLength() / 1000);
			// Mute sound by default.
			this.setVolume(0.0);
		}
		catch (IOException e) {
			System.err.println("SOUND *** Audio file " + wavFile + " not found.");
		}
		catch (Exception e){
			System.err.println(e.toString());
		}
		// Debug.
		m = 0;
		n = 0;
		p = 0;
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
		soundIsPlaying = 1;
	}
	
	/* STOP THE SOUND
	 * @param:	None.
	 * @return: None.
	 */
	public void stop() {
		soundClip.stop();
		soundClip.setFramePosition(0);
		soundIsPlaying = 0;
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
    	double realVolume = soundCurrentVolume * soundMaxVolume;
    	// Set volume.
    	try {
    		FloatControl gainControl = (FloatControl) soundClip.getControl(FloatControl.Type.MASTER_GAIN);
    		float gainDbMin = gainControl.getMinimum();
    		float gainDb = 20.0f * (float) Math.log10(realVolume);
    		// Clamp gain to minimum.
    		if (gainDb < gainDbMin) {
    			gainDb = gainDbMin;
    		}
    	    gainControl.setValue(gainDb);
    	    System.out.println("gainDb=" + gainDb);
    	    //if (System.currentTimeMillis() > p) {
    			//p = System.currentTimeMillis() + 100;
    			//System.out.println("gainDb=" + gainDb);
    		//}
    	}
    	catch (Exception e) {
    		
    	}
	}
	
	/* GET THE SOUND DURATION.
	 * @param:	None.
	 * @return:	Sound duration in milliseconds.
	 */
	public int getDurationMs() {
		return soundDurationMs;
	}
	
	/* GET THE CURRENT POSITION WITHIN THE SOUND.
	 * @param:	None.
	 * @return:	Current position in milliseconds.
	 */
	public int getPositionMs() {
		return (int) (soundClip.getMicrosecondPosition() / 1000);
	}
	
	/* STORE THE CURRENT SOUND POSITION AND VOLUME FOR FADE EFFECT.
	 * @param:	None.
	 * @return: None.
	 */
	public void saveFadeParameters() {
		soundFadeStartPositionMs = this.getPositionMs();
		soundFadeStartVolume = this.getVolume();
	}
	
	/* PERFORM FADE-IN EFFECT.
	 * @param pFadeDuration:	Fade effect duration in milliseconds.
	 * @return fadeEnd			'1' if the fade effect is finished, '0' otherwise.
	 */
	public int fadeIn(int pFadeDuration) {
		double fadeVolume = this.getVolume();
		int fadeEnd = 0;
		// Ensure sound is playing and current position is greater or equal the start position.
		if ((this.getPositionMs() >= soundFadeStartPositionMs) && (soundIsPlaying > 0)) {
			if ((this.getPositionMs() >= (soundFadeStartPositionMs + pFadeDuration)) || (this.getPositionMs() >= soundDurationMs)) {
				// Clamp zone.
				fadeVolume = 1.0;
				fadeEnd = 1;
			}
			else {
				// Fade zone: apply selected equation.
				switch (soundFadeEquation) {
				case SOUND_FADE_EQUATION_LINEAR:
					// Linear equation.
					fadeVolume = (double) (soundFadeStartVolume + ((1.0 - soundFadeStartVolume) * ((double) (this.getPositionMs()) - (double) (soundFadeStartPositionMs))) / ((double) pFadeDuration));
					break;
				case SOUND_FADE_EQUATION_ELLIPSE:
					// Ellipse equation.
					fadeVolume = (double) (soundFadeStartVolume + (1.0 - soundFadeStartVolume) * Math.sqrt(1 - (Math.pow((this.getPositionMs() - soundFadeStartPositionMs - pFadeDuration), 2) / Math.pow(pFadeDuration, 2))));
					break;
				default:
					break;
				}
			}
			// Apply new volume.
			this.setVolume(fadeVolume);
			//if (System.currentTimeMillis() > m) {
				//m = System.currentTimeMillis() + 100;
				//System.out.println("fadeInVolume=" + fadeVolume + " fadeEnd=" + fadeEnd);
			//}
		}
		else {
			// Error.
			fadeEnd = 1;
		}
		// Return end flag.
		return fadeEnd;
	}
	
	/* PERFORM FADE-OUT EFFECT.
	 * @param pFadeDuration:	Fade effect duration in milliseconds.
	 * @return fadeEnd			'1' if the fade effect is finished, '0' otherwise.
	 */
	public int fadeOut(int pFadeDuration) {
		double fadeVolume = this.getVolume();
		int fadeEnd = 0;
		// Ensure sound is playing and current position is greater or equal the start position.
		if ((this.getPositionMs() >= soundFadeStartPositionMs) && (soundIsPlaying > 0)) {
			if ((this.getPositionMs() >= (soundFadeStartPositionMs + pFadeDuration)) || (this.getPositionMs() >= soundDurationMs)) {
				// Clamp zone.
				fadeVolume = 0.0;
				fadeEnd = 1;
			}
			else {
				// Fade zone: apply selected equation.
				switch (soundFadeEquation) {
				case SOUND_FADE_EQUATION_LINEAR:
					// Linear equation.
					fadeVolume = (double) (soundFadeStartVolume - (soundFadeStartVolume * ((double) (this.getPositionMs()) - (double) (soundFadeStartPositionMs))) / ((double) pFadeDuration));
					break;
				case SOUND_FADE_EQUATION_ELLIPSE:
					// Ellipse equation.
					fadeVolume = (double) (soundFadeStartVolume * Math.sqrt(1 - (Math.pow((this.getPositionMs() - soundFadeStartPositionMs), 2) / Math.pow(pFadeDuration, 2))));
					break;
				default:
					break;
				}
			}
			// Apply new volume.
			this.setVolume(fadeVolume);
			if (System.currentTimeMillis() > n) {
				n = System.currentTimeMillis() + 100;
				System.out.println("fadeoutVolume=" + fadeVolume + " fadeEnd=" + fadeEnd);
			}
		}
		else {
			// Error.
			fadeEnd = 1;
		}
		// Return end flag.
		return fadeEnd;
	}
}
