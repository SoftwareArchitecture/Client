package at.ac.tuwien.softwarearchitecture.swazam.client.serverCommunication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.bind.JAXBContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import ac.at.tuwien.infosys.swa.audio.Fingerprint;
import at.ac.tuwien.softwarearchitecture.swazam.client.util.ConfigurationManagement;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ClientInfo;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.FingerprintSearchRequest;
import at.ac.tuwien.softwarearchitecture.swazam.common.infos.ServerInfo;
import java.io.StringWriter;
import java.net.MalformedURLException;
import javax.swing.JOptionPane;
import javax.xml.bind.Marshaller;

public class ServerCommunicationManager implements IServerCommunicationManager {

    // to execute file searches asynchronously
    private ServerInfo serverInfo;

    public ServerCommunicationManager() {
        super();
        this.serverInfo = ConfigurationManagement.loadServerInfo();
    }

    @Override
    public String searchAudio(ClientInfo clientInfo, Fingerprint fingerprintToSearch) {

        String sessionKey = "";
        Logger.getLogger(ServerCommunicationManager.class).log(Level.INFO, "Initiating search");

        FingerprintSearchRequest f = new FingerprintSearchRequest(clientInfo, fingerprintToSearch);

        try {

            JAXBContext context = JAXBContext.newInstance(FingerprintSearchRequest.class);

            StringWriter stringWriter = new StringWriter();
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            m.marshal(f, stringWriter);


            URL url = new URL("http://" + serverInfo.getIp() + ":" + serverInfo.getPort() + "/SWazam/webapi/searchmanagement/search");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/xml");
            conn.setRequestProperty("Accept", "plain/txt");
            conn.setDoOutput(true);

            String input = stringWriter.getBuffer().toString();

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();

//            sessionKey = conn.getResponseMessage();


            {
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    sessionKey = output;
                    return sessionKey;
                }
            }

            {
                BufferedReader br = new BufferedReader(new InputStreamReader((conn.getErrorStream())));


                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                }
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessionKey;
    }

    public void pollAboutSearchResult(final String sessionKey) {
        Logger.getLogger(ServerCommunicationManager.class).log(Level.INFO, "Polling for " + sessionKey);

        Thread pollingThread = new Thread() {
            public void run() {

                String response = "";
                do {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        java.util.logging.Logger.getLogger(ServerCommunicationManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                    try {

                        URL url = new URL("http://" + serverInfo.getIp() + ":" + serverInfo.getPort() + "/SWazam/webapi/searchmanagement/searchresult?sessionkey=" + sessionKey);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                        {
                            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

                            String output;
                            System.out.println("Output from Server .... \n");
                            while ((output = br.readLine()) != null) {
                                response = output;
                            }
                        }
//             
                        conn.disconnect();
                    } catch (MalformedURLException e) {
                        Logger.getLogger(this.getClass()).log(Level.ERROR, e);
                    } catch (Exception e) {
                        Logger.getLogger(this.getClass()).log(Level.ERROR, e);
                    }

                    Logger.getLogger(ServerCommunicationManager.class).log(Level.INFO, "Response from server " + response);

                } while (response.contains("In Progress!"));

                JOptionPane.showConfirmDialog(null, "Song found: " + response);
            }
        };

        pollingThread.setDaemon(true);
        pollingThread.start();

    }
}
