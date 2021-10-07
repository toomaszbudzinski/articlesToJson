package sample;

import org.apache.commons.net.ftp.FTP;
import org.xbib.io.ftp.client.FTPClient;
import org.xbib.io.ftp.client.FTPFile;
import java.io.IOException;
import java.util.ArrayList;

public class FTPfileList {
    private ArrayList<String> attachments;
    private FTPClient ftpClient;
    private String urlFTP;

    public FTPfileList(String urlFTP, String workDirectory, String login, String pass) {
        attachments = new ArrayList<>();
        this.urlFTP = urlFTP;
        this.ftpClient = new FTPClient();
        ftpClient.setStrictReplyParsing(false);
        try {
            //ftpClient.connect(urlFTP, portNumber);
            ftpClient.connect(urlFTP);
            ftpClient.login(login, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.ASCII_FILE_TYPE);
            if (ftpClient.isConnected()) {
                ftpClient.changeToParentDirectory();
                ftpClient.changeWorkingDirectory(workDirectory);
                FTPFile[] files = ftpClient.listFiles();
                //System.out.println(ftpClient.printWorkingDirectory());
                for (FTPFile file : files) {
                    attachments.add(file.getName());
                }
            }
            ftpClient.logout();
        } catch (IOException e) {
            System.out.println("Connection failed " + e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                System.out.println("Connection disconnected " + e);
            }
        }
    }

    public ArrayList<String> getAttachments() {
        return attachments;
    }
}

