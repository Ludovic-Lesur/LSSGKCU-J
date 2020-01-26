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
	private double soundStartVolume;
	private int soundStartPosition;
	// Debug.
	//private long m;
	//private long n;
	
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
		soundCurrentVolume = 1.0;
		soundStartVolume = 1.0;
		soundStartPosition = 0;
		soundIsPlaying = 0;
		soundFadeEquation = SoundFadeEquationEnum.SOUND_FADE_EQUATION_ELLIPSE;
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
		// Debug.
		//m = 0;
		//n = 0;
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
    	    gainControl.setValue(20f * (float) Math.log10(realVolume));
    	}
    	catch (Exception e){
    		
    	}
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
	
	/* STORE THE CURRENT SOUND POSITION AND VOLUME FOR FADE EFFECT.
	 * @param:	None.
	 * @return: None.
	 */
	public void saveFadeParameters() {
		soundStartPosition = this.getPosition();
		soundStartVolume = this.getVolume();
	}
	
	/* PERFORM FADE-IN EFFECT.
	 * @param pFadeDuration:	Fade effect duration in milliseconds.
	 * @return fadeEnd			'1' if the fade effect is finished, '0' otherwise.
	 */
	public int computeFadeInVolume(int pFadeDuration) {
		double fadeVolume = this.getVolume();
		int fadeEnd = 0;
		// Ensure sound is playing and current position is greater or equal the start position.
		if ((this.getPosition() >= soundStartPosition) && (soundIsPlaying > 0)) {
			if ((this.getPosition() >= (soundStartPosition + pFadeDuration)) || (this.getPosition() >= soundDurationMs)) {
				// Clamp zone.
				fadeVolume = 1.0;
				fadeEnd = 1;
			}
			else {
				// Fade zone.
				// Fade zone: apply selected equation.
				switch (soundFadeEquation) {
				case SOUND_FADE_EQUATION_LINEAR:
					// Linear equation.
					break;
				case SOUND_FADE_EQUATION_ELLIPSE:
					// Ellipse equation.
					fadeVolume = (double) (soundStartVolume + (1.0 - soundStartVolume) * Math.sqrt(1 - (Math.pow((this.getPosition() - soundStartPosition - pFadeDuration), 2) / Math.pow(pFadeDuration, 2))));
					break;
				default:
					break;
				}
			}
		}
		else {
			// Error.
			fadeEnd = 1;
		}
		// Apply new volume.
		this.setVolume(fadeVolume);
		//if (System.currentTimeMillis() > m) {
			//m = System.currentTimeMillis() + 100;
			//System.out.println("fadeInVolume=" + fadeVolume + " fadeEnd=" + fadeEnd);
		//}
		// Return end flag.
		return fadeEnd;
	}
	
	/* PERFORM FADE-OUT EFFECT.
	 * @param pFadeDuration:	Fade effect duration in milliseconds.
	 * @return fadeEnd			'1' if the fade effect is finished, '0' otherwise.
	 */
	public int computeFadeOutVolume(int pFadeDuration) {
		double fadeVolume = this.getVolume();
		int fadeEnd = 0;
		// Ensure sound is playing and current position is greater or equal the start position.
		if ((this.getPosition() >= soundStartPosition) && (soundIsPlaying > 0)) {
			if ((this.getPosition() >= (soundStartPosition + pFadeDuration)) || (this.getPosition() >= soundDurationMs)) {
				// Clamp zone.
				fadeVolume = 0.0;
				fadeEnd = 1;
			}
			else {
				// Fade zone: apply selected equation.
				switch (soundFadeEquation) {
				case SOUND_FADE_EQUATION_LINEAR:
					// Linear equation.
					break;
				case SOUND_FADE_EQUATION_ELLIPSE:
					// Ellipse equation.
					fadeVolume = (double) (soundStartVolume * Math.sqrt(1 - (Math.pow((this.getPosition() - soundStartPosition), 2) / Math.pow(pFadeDuration, 2))));
					break;
				default:
					break;
				}
			}
		}
		else {
			// Error.
			fadeEnd = 1;
		}
		// Apply new volume.
		this.setVolume(fadeVolume);
		//if (System.currentTimeMillis() > n) {
			//n = System.currentTimeMillis() + 100;
			//System.out.println("fadeoutVolume=" + fadeVolume + " fadeEnd=" + fadeEnd);
		//}
		// Return end flag.
		return fadeEnd;
	}
}
