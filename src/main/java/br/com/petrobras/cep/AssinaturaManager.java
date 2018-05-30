package br.com.petrobras.cep;

import java.io.File;
import java.security.cert.CertificateEncodingException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import br.com.certisign.certisigner.bean.Certificate;
import br.com.certisign.certisigner.bean.CertificateList;
import br.com.certisign.certisigner.exceptions.SignerException;
import br.com.certisign.certisigner.interfaces.repository.CapiSignerInterface;
import br.com.certisign.certisigner.services.RepositoryServiceFactory;
import br.com.certisign.certisigner.signature.CryptoProcess;

public class AssinaturaManager {

	private static final Logger LOGGER = Logger.getLogger(AssinaturaManager.class);

	public static void main(String[] args) throws Exception {
		File diretorioArquivos = new File("C:\\Users\\y2jm\\Desktop\\diretorioArquivos\\");
		assinar(diretorioArquivos.listFiles());
	}
	
	public static void assinar(File[] arquivos) throws Exception {
		try {
			Progress.exibir("Buscando certificados...");
			Certificate certificado = buscarCertificados();
			if (certificado == null) {
				throw new Exception("Certificado n\u00E3o selecionado");
			}
			assinarArquivos(arquivos, certificado);
		}catch(Exception e) {
			throw e;
		}finally {
			Progress.esconder();
		}
	}

	private static void assinarArquivos(File[] arquivos, Certificate certificado) throws SignerException {
		for (File arquivo : arquivos) {
			String extensao = FilenameUtils.getExtension(arquivo.getName());
			if (extensao.equals("p7s") || extensao.equals("old")) {
				LOGGER.debug("Ignorando: " + arquivo.getAbsolutePath());
				continue;
			}
			Progress.exibir("Assinando arquivo " + arquivo.getName());
			LOGGER.info("Assinatura arquivo " + arquivo.getAbsolutePath());
			CryptoProcess.CMS_Detached.signDocument(certificado, arquivo.getAbsolutePath());
			//ResultSignatureGeneration resultado = CryptoProcess.CMS_Detached.signDocument(certificado, arquivo.getAbsolutePath());
			/*LOGGER.info("Origem: " + resultado.getOriginalFilePath());
			LOGGER.info("Destino: " + resultado.getProcessedFilePath());*/
			LOGGER.info("Assinatura feita com sucesso para arquivo " + arquivo.getAbsolutePath());
		}
	}

	private static Certificate buscarCertificados() throws SignerException, Exception {
		Certificate certificado = null;
		CapiSignerInterface capi = RepositoryServiceFactory.getInstance();
		CertificateList certificadosDisponiveis = null;
		try {
			certificadosDisponiveis = capi.getCertificatesWithPrivateKey(false);	
		}catch(SignerException signerException) {
			throw new Exception("Certificado n\u00E3o encontrado");
		}
		String[] valoresPossiveis = getCertificadosDisponiveisInformacoes(certificadosDisponiveis);

		if (valoresPossiveis.length > 1) {
			String valorSelecionado = (String) JOptionPane.showInputDialog(null, "Por favor, selecione um certificado:", "Certificados Dispon\u00EDveis", -1, null, valoresPossiveis, valoresPossiveis[0]);
			if ((valorSelecionado != null) && (!valorSelecionado.equals(""))) {
				int indiceValorSelecionado = -1;
				for (int i = 0; i < valoresPossiveis.length; i++) {
					if (valoresPossiveis[i].equals(valorSelecionado)) {
						indiceValorSelecionado = i;
					}
				}
				certificado = certificadosDisponiveis.getCertificate(indiceValorSelecionado);
			}
		} else {
			int resposta = JOptionPane.showConfirmDialog(null,"Apenas o certificado abaixo foi encontrado para realizar a assinatura: \n"+ valoresPossiveis[0] + "\nDeseja prosseguir?", "Certificado Dispon\u00EDvel", 0);
			if (resposta == 0) {
				certificado = certificadosDisponiveis.getCertificate(0);
			}
		}
		return certificado;
	}
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public static String[] getCertificadosDisponiveisInformacoes(CertificateList certificadosDisponiveis) {
		String[] certificadosDisponiveisInformacoes = new String[certificadosDisponiveis.size()];
		for (int i = 0; i < certificadosDisponiveis.size(); i++) {
			Certificate certificate = certificadosDisponiveis.getCertificate(i);
			X500Name x500name = null;
			try {
				x500name = new JcaX509CertificateHolder(certificate.getCertificate()).getSubject();
			} catch (CertificateEncodingException ex) {
				java.util.logging.Logger.getLogger(AssinaturaManager.class.getName()).log(Level.SEVERE, null, ex);
			}
			RDN cn = x500name.getRDNs(org.bouncycastle.asn1.x500.style.BCStyle.CN)[0];
			certificadosDisponiveisInformacoes[i] = (IETFUtils.valueToString(cn.getFirst().getValue())+ " V\u00E1lido at\u00E9: " + sdf.format(certificate.getCertificate().getNotAfter()));
		}
		return certificadosDisponiveisInformacoes;
	}
}
