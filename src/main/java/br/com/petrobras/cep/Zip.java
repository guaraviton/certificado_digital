package br.com.petrobras.cep;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

public class Zip {

	private static final Logger LOGGER = Logger.getLogger(Zip.class);

    private InputStream stream;
    private String diretorioUnzip;
 
    public Zip(InputStream stream, String diretorioUnzip)throws Exception {
    	this.stream = stream;
    	this.diretorioUnzip = diretorioUnzip;
    }

    public void unzip() throws Exception {
    	byte[] buffer = new byte[1024];
		File file;
		String fileName;
		try {
			ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(stream));
			for (ZipEntry entry = null; (entry = zipInputStream.getNextEntry()) != null;) {
				fileName = diretorioUnzip + entry.getName();
				file = new File(fileName);
				if(file.exists()) {
					LOGGER.debug(entry.getName() + " ja existente");
				}else if(entry.isDirectory()) {
					file.mkdir();
	            }else{
	            	LOGGER.info("Unzip : "+ file.getAbsoluteFile());
		            new File(file.getParent()).mkdirs();
	            	FileOutputStream fos = new FileOutputStream(file);             
		            int len;
		            while ((len = zipInputStream.read(buffer)) > 0) {
		            	fos.write(buffer, 0, len);
		            }
		            fos.close();	
	            }
			}
			zipInputStream.closeEntry();
			zipInputStream.close();
			LOGGER.info("Unzip do arquivo realizado com sucesso");
		}catch (Exception ex) {
			LOGGER.info("Erro ao descompactar arquivo", ex);
			throw new Exception("Erro ao descompactar arquivo", ex);
		}finally {
			stream.close();
		}
    }
}
