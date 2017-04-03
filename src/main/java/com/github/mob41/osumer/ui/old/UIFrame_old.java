/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Anthony Law
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package com.github.mob41.osumer.ui.old;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import com.github.mob41.osumer.Config;
import com.github.mob41.osumer.exceptions.DebugDump;
import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.exceptions.DumpManager;
import com.github.mob41.osumer.exceptions.NoBuildsForVersionException;
import com.github.mob41.osumer.exceptions.NoSuchBuildNumberException;
import com.github.mob41.osumer.exceptions.NoSuchVersionException;
import com.github.mob41.osumer.exceptions.OsuException;
import com.github.mob41.osumer.io.Osu;
import com.github.mob41.osumer.updater.UpdateInfo;
import com.github.mob41.osumer.updater.Updater;

public class UIFrame_old extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 785147756273965350L;
	private JPanel contentPane;
	private JTextField mapUrlFld;
	private JFileChooser chooser;
	private Updater updater;
	
	private Thread thread;
	private boolean checkingUpdate = false;
	
	private static Image icon256px = null;
	private JLabel lblVersion;
	private JTextPane updateTxtPn;
	private JRadioButtonMenuItem rdbtnmntmSnapshot;
	private JRadioButtonMenuItem rdbtnmntmBeta;
	private JRadioButtonMenuItem rdbtnmntmStable;

	/**
	 * Create the frame.
	 */
	public UIFrame_old(Config config) {
		if (icon256px == null){
			icon256px = Toolkit.getDefaultToolkit().getImage(UIFrame_old.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_256px.png"));
		}
		setIconImage(Toolkit.getDefaultToolkit().getImage(UIFrame_old.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_32px.png")));
		setResizable(false);
		setTitle("osumer UI");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 535);
		
		updater = new Updater(config);
		chooser = new JFileChooser();
		//Limit file format to .osz
		chooser.setFileFilter(new FileFilter(){

			@Override
			public boolean accept(File arg0) {
				if (arg0 == null){
					return false;
				}

				if (arg0.isDirectory()){
					return true;
				}
				
				String str = arg0.getName();
				final String ext = ".osz";
				
				if (str.length() < ext.length()){
					return false;
				}
				
				return str.endsWith(ext);
			}

			@Override
			public String getDescription() {
				return "osu! beatmap";
			}
			
		});
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnOsuBeatmap = new JMenu("osu! beatmap");
		menuBar.add(mnOsuBeatmap);
		
		JMenuItem mntmSearchForBeatmaps = new JMenuItem("Search for beatmaps...");
		mntmSearchForBeatmaps.setEnabled(false);
		mntmSearchForBeatmaps.setToolTipText("This will be implemented if there're enough stars and download count for osumer XD");
		mnOsuBeatmap.add(mntmSearchForBeatmaps);
		
		JMenu mnDebug = new JMenu("Debug");
		menuBar.add(mnDebug);
		
		JMenuItem mntmOpenConfigurationLocation = new JMenuItem("Open configuration location");
		mntmOpenConfigurationLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(System.getenv("localappdata") + "\\osumerExpress"));
				} catch (IOException e1) {
					DebugDump dump = new DebugDump(
							null,
							"(Function call)",
							"(Try scope) Open %localappdata%\\osumerExpress folder using Desktop.getDesktop().open()",
							"(End of function)",
							"Error when opening the folder",
							false,
							e1);
					DumpManager.getInstance().addDump(dump);
					DebugDump.showDebugDialog(dump);
				}
			}
		});
		mnDebug.add(mntmOpenConfigurationLocation);
		
		JMenuItem mntmViewDumps = new JMenuItem("View dumps");
		mntmViewDumps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ViewDumpDialog dialog = new ViewDumpDialog();
				dialog.setVisible(true);
				dialog.setLocationRelativeTo(UIFrame_old.this);
			}
		});
		mnDebug.add(mntmViewDumps);
		
		JMenuItem mntmGenerateAllEvents = new JMenuItem("Generate all events dump");
		mntmGenerateAllEvents.setEnabled(false);
		mnDebug.add(mntmGenerateAllEvents);
		
		JSeparator separator_1 = new JSeparator();
		mnDebug.add(separator_1);
		
		JMenuItem mntmPostANew = new JMenuItem("Post a new issue");
		mntmPostANew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/mob41/osumer/issues/new"));
				} catch (IOException | URISyntaxException e1) {
					DebugDump dump = new DebugDump(
							null,
							"(Function call)",
							"(Try scope) Opening GitHub osumer project issue new using Desktop.getDesktop().browse()",
							"(End of function)",
							"Error when opening the web page",
							false,
							e1);
					DumpManager.getInstance().addDump(dump);
					DebugDump.showDebugDialog(dump);
					e1.printStackTrace();
				}
			}
		});
		mnDebug.add(mntmPostANew);
		
		JMenu mnUpdate = new JMenu("Update");
		menuBar.add(mnUpdate);
		
		JMenuItem mntmRunUpdater = new JMenuItem("Check update");
		mntmRunUpdater.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				checkUpdate();
			}
		});
		mnUpdate.add(mntmRunUpdater);
		
		JSeparator separator = new JSeparator();
		mnUpdate.add(separator);
		
		JLabel lblUpdateBranch = new JLabel("Update branch");
		mnUpdate.add(lblUpdateBranch);
		
		rdbtnmntmSnapshot = new JRadioButtonMenuItem("Snapshot");
		rdbtnmntmSnapshot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				rdbtnmntmSnapshot.setSelected(rdbtnmntmSnapshot.isSelected());
				rdbtnmntmBeta.setSelected(!rdbtnmntmSnapshot.isSelected());
				rdbtnmntmStable.setSelected(!rdbtnmntmSnapshot.isSelected());
				saveUpdateBranchConfig(config);
			}
		});
		mnUpdate.add(rdbtnmntmSnapshot);
		
		rdbtnmntmBeta = new JRadioButtonMenuItem("Beta");
		rdbtnmntmBeta.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnmntmSnapshot.setSelected(!rdbtnmntmBeta.isSelected());
				rdbtnmntmBeta.setSelected(rdbtnmntmBeta.isSelected());
				rdbtnmntmStable.setSelected(!rdbtnmntmBeta.isSelected());
				saveUpdateBranchConfig(config);
			}
		});
		mnUpdate.add(rdbtnmntmBeta);
		
		rdbtnmntmStable = new JRadioButtonMenuItem("Stable");
		rdbtnmntmStable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnmntmSnapshot.setSelected(!rdbtnmntmStable.isSelected());
				rdbtnmntmBeta.setSelected(!rdbtnmntmStable.isSelected());
				rdbtnmntmStable.setSelected(rdbtnmntmStable.isSelected());
				saveUpdateBranchConfig(config);
			}
		});
		mnUpdate.add(rdbtnmntmStable);
		
		contentPane = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 3671374871361999683L;

			@Override
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				int width = getWidth();
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
				g2.drawImage(icon256px, width / 3, 20, contentPane);
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			}
		};
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.repaint();
		
		JLabel lblOsumer = new JLabel("osumer");
		lblOsumer.setHorizontalAlignment(SwingConstants.LEFT);
		lblOsumer.setFont(new Font("Tahoma", Font.PLAIN, 24));
		
		JLabel lblBeatmapUrl = new JLabel("Beatmap URL:");
		lblBeatmapUrl.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		
		mapUrlFld = new JTextField();
		mapUrlFld.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				mapUrlFld.setBackground(Osu.isVaildBeatMapUrl(mapUrlFld.getText()) ? Color.WHITE : Color.PINK);
				mapUrlFld.setForeground(Osu.isVaildBeatMapUrl(mapUrlFld.getText()) ? Color.BLACK : Color.WHITE);
			}
		});
		mapUrlFld.setFont(new Font("PMingLiU", Font.PLAIN, 16));
		mapUrlFld.setColumns(10);
		
		JButton btnDownloadImport = new JButton("Download & Import");
		btnDownloadImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String urlstr = mapUrlFld.getText();
				
				if (!Osu.isVaildBeatMapUrl(urlstr)){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a valid osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				URL url = null;
				try {
					url = new URL(urlstr);
				} catch (MalformedURLException e1){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a valid osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				DownloadDialog dialog = new DownloadDialog(config, url, false, false, true);
				dialog.setModal(true);
				dialog.setUndecorated(false);
				dialog.setVisible(true);
				dialog.setAlwaysOnTop(true);
				dialog.setLocationRelativeTo(UIFrame_old.this);
			}
		});
		btnDownloadImport.setFont(new Font("PMingLiU", Font.BOLD, 16));
		
		JPanel panel = new JPanel();
		
		JLabel lblCopyrightc = new JLabel("Copyright (c) 2016-2017 Anthony Law. Licenced under MIT Licence.");
		lblCopyrightc.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel lblHttpsgithubcommobosumer = new JLabel("https://github.com/mob41/osumer");
		lblHttpsgithubcommobosumer.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI("https://github.com/mob41/osumer"));
				} catch (IOException | URISyntaxException e) {
					DebugDump dump = new DebugDump(
							null,
							"(Function call)",
							"(Try scope) Opening GitHub osumer project page using Desktop.getDesktop().browse()",
							"(End of function)",
							"Error when opening the web page",
							false,
							e);
					DumpManager.getInstance().addDump(dump);
					DebugDump.showDebugDialog(dump);
					e.printStackTrace();
				}
			}
		});
		lblHttpsgithubcommobosumer.setForeground(Color.BLUE);
		lblHttpsgithubcommobosumer.setHorizontalAlignment(SwingConstants.CENTER);
		
		JLabel label = new JLabel("");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setIcon(new ImageIcon(UIFrame_old.class.getResource("/com/github/mob41/osumer/ui/osumerIcon_64px.png")));
		
		JLabel lblNewLabel = new JLabel("The easiest,express way to obtain beatmaps");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));

		JPopupMenu popupMenu = new JPopupMenu();
		JButton btnDownload = new JButton("Download...");
		btnDownload.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				popupMenu.show(btnDownload, arg0.getX(), arg0.getY());
			}
		});
		
		JMenuItem mntmDwnBatch = new JMenuItem("Download batch");
		mntmDwnBatch.setEnabled(false);
		mntmDwnBatch.setToolTipText("This will be implemented if there're enough stars and download count for osumer XD");
		popupMenu.add(mntmDwnBatch);
		
		JMenuItem mntmDwnFolder = new JMenuItem("Download to folder");
		mntmDwnFolder.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//Validate URL
				String urlstr = mapUrlFld.getText();
				
				if (!Osu.isVaildBeatMapUrl(urlstr)){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a valid osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				URL url = null;
				try {
					url = new URL(urlstr);
				} catch (MalformedURLException e1){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a valid osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
					
				}
				
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				int option = chooser.showSaveDialog(UIFrame_old.this);
				
				if (option == JFileChooser.CANCEL_OPTION){
					return;
				}
				
				File folder = chooser.getSelectedFile();
				
				//Download
				DownloadDialog dialog = new DownloadDialog(config, url, false, false, false);
				dialog.setModal(true);
				dialog.setUndecorated(false);
				dialog.setVisible(true);
				dialog.setAlwaysOnTop(true);
				dialog.setLocationRelativeTo(UIFrame_old.this);
				
				//Move file
				String filePath = dialog.getFilePath();
				System.out.println(filePath);
				
				if (filePath == null){
					return;
				}
				
				File dwnFile = new File(filePath);
				
				if (!dwnFile.exists()){
					return;
				}
				
				File moveFile = new File(folder.getAbsolutePath() + "\\" + dwnFile.getName());
				System.out.println(moveFile.getAbsolutePath());
				
				try {
					FileOutputStream out = new FileOutputStream(moveFile);
					Files.copy(dwnFile.toPath(), out);
					out.flush();
					out.close();
				} catch (IOException e1){
					DumpManager.getInstance().addDump(new DebugDump(
							null,
							"Create new File instance with \"" + folder.getAbsolutePath() + "\\" + dwnFile.getName() + "\" and assign to moveFile",
							"(Try scope) Copy file from dwnFile to moveFile using Files.copy()",
							"(Catch scope) Show error dialog",
							"Error when copying/moving the downloaded file",
							false,
							e1));
					JOptionPane.showMessageDialog(UIFrame_old.this, "Could not move the file to \"" + moveFile.getAbsolutePath() + "\"!\n\nException:\n" + e1 + "\nSee dump from \"View Dumps\" for more details.", "Error", JOptionPane.ERROR_MESSAGE);
					dwnFile.delete();
					return;
				}
				
				dwnFile.delete();
				JOptionPane.showMessageDialog(UIFrame_old.this, "Download completed at location:\n" + moveFile.getAbsolutePath(), "Info", JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		popupMenu.add(mntmDwnFolder);
		
		JMenuItem mntmDwnAs = new JMenuItem("Download as...");
		mntmDwnAs.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				//Validate URL
				String urlstr = mapUrlFld.getText();
				
				if (!Osu.isVaildBeatMapUrl(urlstr)){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a valid osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				URL url = null;
				try {
					url = new URL(urlstr);
				} catch (MalformedURLException e1){
					JOptionPane.showMessageDialog(null, "The beatmap URL provided isn't a valid osu! beatmap URL.", "Error", JOptionPane.WARNING_MESSAGE);
					return;
					
				}
				
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				
				int option = chooser.showSaveDialog(UIFrame_old.this);
				
				if (option == JFileChooser.CANCEL_OPTION){
					return;
				}
				
				File targetFile = chooser.getSelectedFile();
				
				//Download
				DownloadDialog dialog = new DownloadDialog(config, url, false, false, false);
				dialog.setModal(true);
				dialog.setUndecorated(false);
				dialog.setVisible(true);
				dialog.setAlwaysOnTop(true);
				dialog.setLocationRelativeTo(UIFrame_old.this);
				
				//Move file
				String filePath = dialog.getFilePath();
				System.out.println(filePath);
				
				if (filePath == null){
					return;
				}
				
				File dwnFile = new File(filePath);
				
				if (!dwnFile.exists()){
					return;
				}
				
				String path = targetFile.getAbsolutePath();
				if (!path.endsWith(".osz")){
					path += ".osz";
				}
				
				File moveFile = new File(path);
				System.out.println(moveFile.getAbsolutePath());
				
				try {
					FileOutputStream out = new FileOutputStream(moveFile);
					Files.copy(dwnFile.toPath(), out);
					out.flush();
					out.close();
				} catch (IOException e1){
					DumpManager.getInstance().addDump(new DebugDump(
							null,
							"Create new File instance with \"" + path + "\" and assign to moveFile",
							"(Try scope) Copy file from dwnFile to moveFile using Files.copy()",
							"(Catch scope) Show error dialog",
							"Error when copying/moving the downloaded file",
							false,
							e1));
					JOptionPane.showMessageDialog(UIFrame_old.this, "Could not move the file to \"" + moveFile.getAbsolutePath() + "\"!\n\nException:\n" + e1 + "\nSee dump from \"View Dumps\" for more details.", "Error", JOptionPane.ERROR_MESSAGE);
					dwnFile.delete();
					return;
				}
				
				dwnFile.delete();
				JOptionPane.showMessageDialog(UIFrame_old.this, "Download completed at location:\n" + moveFile.getAbsolutePath(), "Info", JOptionPane.INFORMATION_MESSAGE);
			}
			
		});
		popupMenu.add(mntmDwnAs);
		
		lblVersion = new JLabel("Version: " + Osu.OSUMER_VERSION);
		
		JLabel lblUpdateBranchLabel = new JLabel("This branch: " + Osu.OSUMER_BRANCH);
		
		updateTxtPn = new JTextPane();
		updateTxtPn.setEditable(false);
		updateTxtPn.setBackground(SystemColor.control);
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblCopyrightc, GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(label)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblOsumer)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 337, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(updateTxtPn, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
								.addComponent(lblUpdateBranchLabel, GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
								.addComponent(lblVersion, GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblBeatmapUrl)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(mapUrlFld, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDownloadImport)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDownload))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblHttpsgithubcommobosumer, GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblOsumer, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
							.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE))
						.addComponent(label, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblVersion)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblUpdateBranchLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(updateTxtPn, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
									.addComponent(btnDownload, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
									.addComponent(btnDownloadImport, GroupLayout.PREFERRED_SIZE, 28, Short.MAX_VALUE))
								.addComponent(lblBeatmapUrl))
							.addGap(7))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(mapUrlFld, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addComponent(panel, GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblCopyrightc)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblHttpsgithubcommobosumer, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
					.addGap(3))
		);
		
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		AccountSettingsPanel accPanel = new AccountSettingsPanel(config);
		accPanel.setBorder(new TitledBorder(null, "Using account", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(accPanel);
		
		ExpressSettingsPanel xpPanel = new ExpressSettingsPanel(config, updater);
		xpPanel.setBorder(new TitledBorder(null, "osumerExpress settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.add(xpPanel);
		
		if (!Osu.isWindows()){
			xpPanel.setEnabled(false);
			xpPanel.setToolTipText("osumerExpress does not support installation in non-Windows environment, currently not implemented.");
		}
		
		contentPane.setLayout(gl_contentPane);
		
		checkUpdate();
		updateUpdateSourceChoice(config);
	}
	
	private void checkUpdate(){
		if (!checkingUpdate){
			checkingUpdate = true;
			thread = new Thread(new Runnable(){
				public void run(){
					updateTxtPn.setForeground(Color.BLACK);
					updateTxtPn.setText("Checking update...");
					
					UpdateInfo verInfo = null;
					try {
						verInfo = updater.getLatestVersion();
					} catch (NoBuildsForVersionException e){
						updateTxtPn.setForeground(Color.RED);
						updateTxtPn.setText("No builds available for the new version. See dump");
						checkingUpdate = false;
						return;
					} catch (NoSuchVersionException e){
						updateTxtPn.setForeground(Color.RED);
						updateTxtPn.setText("No current version in the selected branch. See dump");
						JOptionPane.showMessageDialog(UIFrame_old.this, "We don't have version " + Osu.OSUMER_VERSION + " in the current update branch\n\nPlease try another update branch (snapshot, beta, stable).", "Version not available", JOptionPane.INFORMATION_MESSAGE);
						checkingUpdate = false;
						return;
					} catch (NoSuchBuildNumberException e){
						updateTxtPn.setForeground(Color.RED);
						updateTxtPn.setText("This version has a invalid build number. See dump");
						JOptionPane.showMessageDialog(UIFrame_old.this, 
								"We don't have build number greater or equal to " + Osu.OSUMER_BUILD_NUM + " in version " + Osu.OSUMER_VERSION + ".\n" +
								"If you are using a modified/development osumer,\n"
								+ " you can just ignore this message.\n" +
								"If not, this might be the versions.json in GitHub goes wrong,\n"
								+ " post a new issue about this.", "Build not available", JOptionPane.WARNING_MESSAGE);
						checkingUpdate = false;
						return;
					} catch (DebuggableException e){
						e.printStackTrace();
						updateTxtPn.setForeground(Color.RED);
						updateTxtPn.setText("Could not connect to update server.");
						checkingUpdate = false;
						return;
					}
					
					if (verInfo == null){
						updateTxtPn.setForeground(Color.RED);
						updateTxtPn.setText("Could not get latest update information.");
					} else if (!verInfo.isThisVersion()){
						updateTxtPn.setForeground(new Color(0,153,0));
						updateTxtPn.setText(
								(verInfo.isUpgradedVersion() ? "Upgrade" : "Update") +
								" available! New version: " + verInfo.getVersion() +
								"-" + Updater.getBranchStr(verInfo.getBranch()) +
								"-b" + verInfo.getBuildNum());
						
						int option;
						String desc = verInfo.getDescription();
						if (desc == null){
							option = JOptionPane.showOptionDialog(UIFrame_old.this,
									"New " +
									(verInfo.isUpgradedVersion() ? "upgrade" : "update") +
									" available! New version:\n" + verInfo.getVersion() +
									"-" + Updater.getBranchStr(verInfo.getBranch()) +
									"-b" + verInfo.getBuildNum() + "\n\n" +
									"Do you want to update it now?", "Update available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, JOptionPane.NO_OPTION);
						} else {
							option = JOptionPane.showOptionDialog(UIFrame_old.this,
									"New " +
									(verInfo.isUpgradedVersion() ? "upgrade" : "update") +
									" available! New version:\n" + verInfo.getVersion() +
									"-" + Updater.getBranchStr(verInfo.getBranch()) +
									"-b" + verInfo.getBuildNum() + "\n\n" +
									"Do you want to update it now?", "Update available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Yes", "No", "Description/Changelog"}, JOptionPane.NO_OPTION);
							
							if (option == 2){
								option = JOptionPane.showOptionDialog(UIFrame_old.this, new TextPanel(desc), "Update description/change-log", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, 0);
							}
						}
						
						if (option == JOptionPane.YES_OPTION){
							/*
							try {
								Desktop.getDesktop().browse(new URI(verInfo.getWebLink()));
							} catch (IOException | URISyntaxException e) {
								DebugDump dump = new DebugDump(
										verInfo.getWebLink(),
										"Show option dialog of updating osumer or not",
										"Set checkingUpdate to false",
										"(End of function / thread)",
										"Error when opening the web page",
										false,
										e);
								DumpManager.getInstance().addDump(dump);
								DebugDump.showDebugDialog(dump);
							}
							*/
							try {
								String updaterLink = Updater.getUpdaterLink();
								
								if (updaterLink == null){
									System.out.println("No latest updater .exe defined! Falling back to legacy updater!");
									updaterLink = Updater.LEGACY_UPDATER_JAR;
								}
								
								URL url;
								try {
									url = new URL(updaterLink);
								} catch (MalformedURLException e) {
									e.printStackTrace();
									JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
									return;
								}
								
								UpdaterDownloadDialog dialog = new UpdaterDownloadDialog(url);
								dialog.setModal(true);
								dialog.setVisible(true);
							} catch (OsuException e){
								e.printStackTrace();
								JOptionPane.showMessageDialog(null, "Error:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
							}
						}
					} else {
						updateTxtPn.setText("You are running the latest version of osumer!");
					}
					checkingUpdate = false;
				}
			});
			thread.start();
		}
	}
	
	private void updateUpdateSourceChoice(Config config){
		switch(config.getUpdateSource()){
		case Updater.UPDATE_SOURCE_SNAPSHOT:
			rdbtnmntmSnapshot.setSelected(true);
			rdbtnmntmBeta.setSelected(false);
			rdbtnmntmStable.setSelected(false);
			break;
		case Updater.UPDATE_SOURCE_BETA:
			rdbtnmntmSnapshot.setSelected(false);
			rdbtnmntmBeta.setSelected(true);
			rdbtnmntmStable.setSelected(false);
			break;
		case Updater.UPDATE_SOURCE_STABLE:
			rdbtnmntmSnapshot.setSelected(false);
			rdbtnmntmBeta.setSelected(false);
			rdbtnmntmStable.setSelected(true);
			break;
		default:
			//TODO: Handle error?
			break;
		}
	}
	
	private void saveUpdateBranchConfig(Config config){
		int updateSource = -1;
		if (rdbtnmntmSnapshot.isSelected()){
			updateSource = Updater.UPDATE_SOURCE_SNAPSHOT;
		} else if (rdbtnmntmBeta.isSelected()){
			updateSource = Updater.UPDATE_SOURCE_BETA;
		} else if (rdbtnmntmStable.isSelected()){
			updateSource = Updater.UPDATE_SOURCE_STABLE;
		} else {
			return;
		}
		
		config.setUpdateSource(updateSource);
		try {
			config.write();
		} catch (IOException e) {
			//Handle error
			e.printStackTrace();
			JOptionPane.showMessageDialog(UIFrame_old.this, "Could not save update source configuration to file :(\n\nException:\n" + e, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
