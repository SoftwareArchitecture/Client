package at.ac.tuwien.softwarearchitecture.swazam.client.fingerprint;

import javax.sound.sampled.AudioInputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import ac.at.tuwien.infosys.swa.audio.FingerprintSystem;
import java.io.IOException;
 

public class FingerprintExtractorAndManager implements IFingerprintExtractorAndManager {

    public Fingerprint extractFingeprint(AudioInputStream audioInputStream) {
        Fingerprint fingerprint = null;
        try {
            fingerprint = FingerprintSystem.fingerprint(audioInputStream);
        } catch (IOException e) {

            Logger.getLogger(this.getClass()).log(Level.ERROR, e);
        }
       return fingerprint;
    }
 
}
