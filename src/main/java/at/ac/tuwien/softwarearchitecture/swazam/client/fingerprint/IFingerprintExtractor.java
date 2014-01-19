package at.ac.tuwien.softwarearchitecture.swazam.client.fingerprint;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import javax.sound.sampled.AudioInputStream;

public interface IFingerprintExtractor {
	

	public Fingerprint extractFingeprint(AudioInputStream audioInputStream) ;
 
}
