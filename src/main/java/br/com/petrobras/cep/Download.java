package br.com.petrobras.cep;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.log4j.Logger;

public class Download {
	
	private static final Logger LOGGER = Logger.getLogger(Download.class);

    private HttpURLConnection httpConn;
 
    public Download(String requestURL)throws Exception {
		try {
			Trust.all();
			httpConn = (HttpURLConnection) new URL(requestURL).openConnection();
			LOGGER.info("Retornou do servidor " + httpConn.getResponseCode());
		} catch (Exception ex) {
			throw new Exception("Erro ao abrir conexao na Url: " + requestURL, ex);
		}
    }
 
    public InputStream get() throws Exception{
    	try {
    		BufferedInputStream buf = new BufferedInputStream(httpConn.getInputStream());
    		return buf;
		} catch (Exception ex) {
			throw new Exception("Erro ao realizar download atraves da Url: " + httpConn.getURL().toString(), ex);
		}
    }
    
}
