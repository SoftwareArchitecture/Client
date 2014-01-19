package at.ac.tuwien.softwarearchitecture.swazam.client.audio;

import java.util.HashMap;

import javax.sound.sampled.AudioInputStream;

public interface IAudioManager {
	public AudioInputStream submitAudioFile(String file);
}
