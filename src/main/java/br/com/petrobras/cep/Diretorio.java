package br.com.petrobras.cep;

import java.io.File;

import org.apache.log4j.Logger;

public class Diretorio {

	private static final Logger LOGGER = Logger.getLogger(Diretorio.class);
	
	public static void limpar(String diretorio) {
		LOGGER.debug("Limpando diretorio " + diretorio);
		File file = new File(diretorio);
		if (file.isDirectory()) {
			File[] arquivo = file.listFiles();
			for (File toDelete : arquivo) {
				toDelete.delete();
				LOGGER.debug("Apagou arquivo " + toDelete.getName());
			}
		}
	}
}
