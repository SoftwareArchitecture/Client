package at.ac.tuwien.softwarearchitecture.swazam.client.serverCommunication;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;

public interface IServerCommunicationManager {

    public String searchAudio(ClientInfo clientInfo, Fingerprint fingerprintToSearch);

    public void pollAboutSearchResult(String sessionKey);
}
