package br.com.petrobras.cep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class Upload {

	private static final Logger LOGGER = Logger.getLogger(Upload.class);

	private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private PrintWriter writer;
 
    public Upload(String requestURL, File[] arquivos)throws Exception {
    	Trust.all();
        boundary = "===" + System.currentTimeMillis() + "===";
        httpConn = (HttpURLConnection) new URL(requestURL).openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
        for(File arquivo : arquivos) {
    		addFilePart(arquivo.getName(), arquivo);	
    	}
    }

    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + fieldName+ "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
        writer.append("Content-Type: "+ URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
 
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
         
        writer.append(LINE_FEED);
        writer.flush();    
    }
 
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }
     
    public void post() throws IOException {
    	LOGGER.info("Inicio upload arquivo " + httpConn.getURL().toString());
        writer.append(LINE_FEED).flush();
        writer.append("--" + boundary + "--").append(LINE_FEED);
        writer.close();
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line = null;
            LOGGER.info("Resposta servidor:");
            while ((line = reader.readLine()) != null) {
                LOGGER.info(line);
            }
            reader.close();
            httpConn.disconnect();
            LOGGER.info("Fim upload arquivo");
        } else {
        	LOGGER.error("Servidor retornou status: " + status);
            throw new IOException("Erro ao realizar o upload para a url : " + httpConn.getURL().toString()+ ". Status " + status);
        }
    }
}
