package br.com.petrobras.cep;

import java.awt.Dimension;

import javax.swing.GroupLayout;

public class Progress extends javax.swing.JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static javax.swing.JProgressBar barra;
	private static javax.swing.JPanel jp;
	private static javax.swing.JLabel label;

	public Progress() {
		setModal(false);
		jp = new javax.swing.JPanel();
		label = new javax.swing.JLabel();
		barra = new javax.swing.JProgressBar();
		label.setHorizontalAlignment(0);
		label.setText("");

		barra.setIndeterminate(true);

		GroupLayout jpLayout = new GroupLayout(jp);
		jp.setLayout(jpLayout);
		jpLayout.setHorizontalGroup(jpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(jpLayout.createSequentialGroup().addGroup(jpLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jpLayout.createSequentialGroup().addGap(20, 20, 20).addComponent(barra, -2, 224, -2))
						.addGroup(
								jpLayout.createSequentialGroup().addContainerGap().addComponent(label, -1, 234, 32767)))
						.addContainerGap()));

		jpLayout.setVerticalGroup(jpLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
				javax.swing.GroupLayout.Alignment.TRAILING,
				jpLayout.createSequentialGroup().addContainerGap(-1, 32767).addComponent(label)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
						.addComponent(barra, -2, -1, -2)
						.addGap(38, 38, 38)));

		getContentPane().add(jp);
		pack();
		Dimension tela = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((tela.width - getSize().width) / 2, (tela.height - getSize().height) / 2);
	}

	private static Progress p = null;

	@SuppressWarnings("rawtypes")
	private static javax.swing.SwingWorker worker;
	

	@SuppressWarnings("rawtypes")
	public static void exibir(String mensagem) {
		if (p == null) {
			p = new Progress();
			for (int c = 0; c < 2; c++) {
				p.setVisible(false);
				p.setTitle("Aguarde");

				worker = new javax.swing.SwingWorker() {
					protected Object doInBackground() throws Exception {
						if (Progress.p == null) {
							return null;
						}

						while (Progress.p != null) {
							Thread.sleep(1000L);
						}
						return null;
					}

					protected void done() {
						if (Progress.p != null) {
							Progress.p.setVisible(false);
						}
					}
				};
				p.setVisible(true);
				worker.execute();
			}
		}
		label.setText(mensagem);
	}
	
	public static void esconder() {
		if(p != null) {
			p.dispose();
			worker = null;
			p = null;	
		}
	}
}
