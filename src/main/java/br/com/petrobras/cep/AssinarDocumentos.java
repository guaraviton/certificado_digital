package br.com.petrobras.cep;

import java.io.File;
import java.io.InputStream;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

public class AssinarDocumentos {

	private static final Logger LOGGER = Logger.getLogger(AssinarDocumentos.class);
	
	private static final String DIRETORIO_BASE = System.getProperty("user.dir") + File.separator;
	private static final String URL_ARQUIVOS_CONFIGURACAO = System.getProperty("jnlp.url.arquivo.configuracao");//"http://MI00308369:8080/poc-assinatura-online/assinatura/configuracao/config.zip";
	
	private static final String URL_ARQUIVOS_ASSINAR = System.getProperty("jnlp.url.arquivo.assinatura");//"http://MI00308369:8080/poc-assinatura-online/assinatura/arquivos/arquivos.zip";
	private static final String DIRETORIO_DOWNLOAD_ARQUIVOS = DIRETORIO_BASE + System.getProperty("jnlp.diretorio.temp.arquivos") + File.separator;
	
	private static final String URL_ARQUIVOS_UPLOAD = System.getProperty("jnlp.url.arquivo.upload");//"http://MI00308369:8080/poc-assinatura-online/assinatura/arquivos/arquivos.zip";

	public static void main(String[] args) {
		LOGGER.debug("DIRETORIO_BASE: " + DIRETORIO_BASE);
		LOGGER.debug("URL_ARQUIVOS_CONFIGURACAO: " + URL_ARQUIVOS_CONFIGURACAO);
		LOGGER.debug("URL_ARQUIVOS_ASSINAR: " + URL_ARQUIVOS_ASSINAR);
		LOGGER.debug("DIRETORIO_DOWNLOAD_ARQUIVOS: " + DIRETORIO_DOWNLOAD_ARQUIVOS);
		try {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception ex) {
				LOGGER.error("Nao foi possivel definir o look and feel, seguindo com visual default");
			}
			configurarAmbiente();
			File[] arquivos = downloadArquivos();
			assinarArquivos(arquivos);
			uploadArquivo(new File(DIRETORIO_DOWNLOAD_ARQUIVOS).listFiles());
			JOptionPane.showMessageDialog(null, "Arquivos assinados com sucesso.\nDiretorio base: " + DIRETORIO_BASE);
		} catch (Exception e) {
			Progress.esconder();
			JOptionPane.showMessageDialog(null, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
			LOGGER.error("Erro na assinatura", e);
		}finally {
			Progress.esconder();
		}
	}

	private static void assinarArquivos(File[] arquivos) throws Exception {
		AssinaturaManager.assinar(arquivos);
	}

	private static File[] downloadArquivos() throws Exception {
		Progress.exibir("Download dos arquivos...");
		Diretorio.limpar(DIRETORIO_DOWNLOAD_ARQUIVOS);
		descompactarArquivo(downloadArquivo(URL_ARQUIVOS_ASSINAR), DIRETORIO_DOWNLOAD_ARQUIVOS);
		File diretorioArquivos = new File(DIRETORIO_DOWNLOAD_ARQUIVOS);
		return diretorioArquivos.listFiles();
	}

	private static void configurarAmbiente() throws Exception {
		Progress.exibir("Iniciando aplica\u00E7\u00E3o...");
		descompactarArquivo(downloadArquivo(URL_ARQUIVOS_CONFIGURACAO), DIRETORIO_BASE);
	}

	private static void descompactarArquivo(InputStream stream, String diretorioUnzip) throws Exception {
		new Zip(stream, diretorioUnzip).unzip();
	}

	private static InputStream downloadArquivo(String url) throws Exception {
		return new Download(url).get();
	}
	
	private static void uploadArquivo(File[] arquivos) throws Exception {
        Progress.exibir("Enviando os arquivos assinados para o cep...");
    	new Upload(URL_ARQUIVOS_UPLOAD, arquivos).post();
    	Progress.esconder();
	}
}
