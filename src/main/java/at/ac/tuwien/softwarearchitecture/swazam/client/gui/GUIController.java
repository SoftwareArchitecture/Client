/**
 * Copyright 2013 Technische Universitaet Wien (TUW), Distributed Systems Group
 * E184
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package at.ac.tuwien.softwarearchitecture.swazam.client.gui;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.client.audio.AudioManager;
import at.ac.tuwien.softwarearchitecture.swazam.client.audio.IAudioManager;
import at.ac.tuwien.softwarearchitecture.swazam.client.fingerprint.FingerprintExtractorAndManager;
import at.ac.tuwien.softwarearchitecture.swazam.client.fingerprint.IFingerprintExtractorAndManager;
import at.ac.tuwien.softwarearchitecture.swazam.client.serverCommunication.IServerCommunicationManager;
import at.ac.tuwien.softwarearchitecture.swazam.client.serverCommunication.ServerCommunicationManager;
import at.ac.tuwien.softwarearchitecture.swazam.client.util.ConfigurationManagement;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import javax.sound.sampled.AudioInputStream;
import javax.swing.JFileChooser;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @Author Daniel Moldovan
 * @E-mail: d.moldovan@dsg.tuwien.ac.at
 *
 */
public class GUIController {

    private IAudioManager audioManager;
    private IFingerprintExtractorAndManager fingerprintExtractorAndManager;
    private IServerCommunicationManager serverCommunicationManager;
    private ClientInfo clientInfo;
    private Gui gui;

    {
        clientInfo = ConfigurationManagement.loadClientInfo();
        audioManager = new AudioManager();
        fingerprintExtractorAndManager = new FingerprintExtractorAndManager();
        serverCommunicationManager = new ServerCommunicationManager();
    }

    {

        // initiate Log4J
        // initiate logger
        {
            String date = new Date().toString();
            date = date.replace(" ", "_");
            date = date.replace(":", "_");
            System.getProperties().put("recording_date", date);

            try {
                InputStream log4jStream = ConfigurationManagement.getLog4JConfig();

                if (log4jStream != null) {
                    PropertyConfigurator.configure(log4jStream);
                    log4jStream.close();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public GUIController(Gui gui) {
        this.gui = gui;
        this.gui.addFileSelectActionListener(searchFileActionListener);
    }
    private ActionListener searchFileActionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JFileChooser theFileChooser = (JFileChooser) e.getSource();
            String command = e.getActionCommand();
            if (command.equals(JFileChooser.APPROVE_SELECTION)) {
                File selectedFile = theFileChooser.getSelectedFile();
                Logger.getLogger(this.getClass()).log(Level.INFO, "Extracting audio stream for " + selectedFile.getName());

                AudioInputStream stream = audioManager.submitAudioFile(selectedFile.getAbsolutePath());

                Logger.getLogger(this.getClass()).log(Level.INFO, "Extracted fingeprint for stream " + stream);

                Fingerprint fingerprint = fingerprintExtractorAndManager.extractFingeprint(stream);

                Logger.getLogger(this.getClass()).log(Level.INFO, "Initiating search for " + fingerprint.getStartTime() + " with client " + clientInfo);

                String searchSessionKey = serverCommunicationManager.searchAudio(clientInfo, fingerprint);

                Logger.getLogger(this.getClass()).log(Level.INFO, "Starting result poll for sessionkey " + searchSessionKey);

                serverCommunicationManager.pollAboutSearchResult(searchSessionKey);

            } else if (command.equals(JFileChooser.CANCEL_SELECTION)) {
                System.out.println(JFileChooser.CANCEL_SELECTION);
            }
        }
    };
}
