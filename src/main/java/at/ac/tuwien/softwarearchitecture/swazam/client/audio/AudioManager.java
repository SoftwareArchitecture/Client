package at.ac.tuwien.softwarearchitecture.swazam.client.audio;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class AudioManager implements IAudioManager {

    public AudioInputStream submitAudioFile(String path) {
        try {
            return AudioSystem.getAudioInputStream(new File(path));
        } catch (Exception e) {
            Logger.getLogger(this.getClass()).log(Level.ERROR, e);
            return null;
        }
    }

    
}
