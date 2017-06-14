package com.github.mob41.osumer.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.github.mob41.osumer.exceptions.DebuggableException;
import com.github.mob41.osumer.io.beatmap.Osu;

import java.awt.CardLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ChgCredentialsDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField userFld;
    
    private final Osu osu;
    private JButton loginButton;
    private JProgressBar pb;
    private JLabel lblLoggingIn;
    private JPasswordField pwdFld;
    private JPanel loginPanel;
    private JPanel loadingPanel;
    
    private boolean useCredentials = false;
    
    /**
     * Create the dialog.
     */
    public ChgCredentialsDialog() {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        osu = new Osu();
        
        setTitle("Login into osu!");
        setBounds(100, 100, 451, 183);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new CardLayout(0, 0));
        {
            loginPanel = new JPanel();
            contentPanel.add(loginPanel, "name_357355419911426");
            JLabel lblYourLoginInformation = new JLabel("Your login information is only used for logging into osu! forum.");
            lblYourLoginInformation.setFont(new Font("Tahoma", Font.PLAIN, 12));
            lblYourLoginInformation.setHorizontalAlignment(SwingConstants.CENTER);
            JLabel lblUsername = new JLabel("Username:");
            lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 12));
            lblUsername.setHorizontalAlignment(SwingConstants.RIGHT);
            
            userFld = new JTextField();
            userFld.setColumns(10);
            
            JLabel lblPassword = new JLabel("Password:");
            lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 12));
            lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
            
            pwdFld = new JPasswordField();
            GroupLayout gl_loginPanel = new GroupLayout(loginPanel);
            gl_loginPanel.setHorizontalGroup(
                gl_loginPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_loginPanel.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(gl_loginPanel.createParallelGroup(Alignment.LEADING)
                            .addComponent(lblYourLoginInformation, GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                            .addGroup(gl_loginPanel.createSequentialGroup()
                                .addGroup(gl_loginPanel.createParallelGroup(Alignment.TRAILING, false)
                                    .addComponent(lblPassword, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblUsername, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addGroup(gl_loginPanel.createParallelGroup(Alignment.LEADING)
                                    .addComponent(pwdFld, GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                                    .addComponent(userFld, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))))
                        .addContainerGap())
            );
            gl_loginPanel.setVerticalGroup(
                gl_loginPanel.createParallelGroup(Alignment.LEADING)
                    .addGroup(gl_loginPanel.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblYourLoginInformation)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_loginPanel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(lblUsername)
                            .addComponent(userFld, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_loginPanel.createParallelGroup(Alignment.BASELINE)
                            .addComponent(lblPassword)
                            .addComponent(pwdFld, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(28, Short.MAX_VALUE))
            );
            loginPanel.setLayout(gl_loginPanel);
        }
        
        loadingPanel = new JPanel();
        loadingPanel.setVisible(false);
        contentPanel.add(loadingPanel, "name_357502614460050");
        
        lblLoggingIn = new JLabel("Logging in...");
        lblLoggingIn.setHorizontalAlignment(SwingConstants.CENTER);
        lblLoggingIn.setFont(new Font("Tahoma", Font.PLAIN, 18));
        
        pb = new JProgressBar();
        pb.setIndeterminate(true);
        GroupLayout gl_loadingPanel = new GroupLayout(loadingPanel);
        gl_loadingPanel.setHorizontalGroup(
            gl_loadingPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, gl_loadingPanel.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_loadingPanel.createParallelGroup(Alignment.TRAILING)
                        .addComponent(pb, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
                        .addComponent(lblLoggingIn, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
                    .addContainerGap())
        );
        gl_loadingPanel.setVerticalGroup(
            gl_loadingPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_loadingPanel.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(lblLoggingIn, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(pb, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addContainerGap())
        );
        loadingPanel.setLayout(gl_loadingPanel);
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                loginButton = new JButton("Login");
                loginButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        loginButton.setEnabled(false);
                        loginPanel.setVisible(false);
                        loadingPanel.setVisible(true);
                        
                        new Thread(){
                            public void run(){
                                final String usr = userFld.getText();
                                final String pwd = new String(pwdFld.getPassword());
                                
                                try {
                                    osu.login(usr, pwd);
                                } catch (DebuggableException e1) {
                                    int option = JOptionPane.showOptionDialog(ChgCredentialsDialog.this, "Cannot login into this osu! account.\nDo you still want to save it?", "Login failed", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, JOptionPane.NO_OPTION);
                                    
                                    if (option != JOptionPane.YES_OPTION){
                                        loginButton.setEnabled(true);
                                        loginPanel.setVisible(true);
                                        loadingPanel.setVisible(false);
                                        return;
                                    }
                                }
                                
                                useCredentials = true;
                                dispose();
                                return;
                            }
                        }.start();
                    }
                });
                buttonPane.add(loginButton);
                getRootPane().setDefaultButton(loginButton);
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        dispose();
                        return;
                    }
                });
                buttonPane.add(cancelButton);
            }
        }
    }
    
    protected String getUsername(){
        return userFld.getText();
    }
    
    protected String getPassword(){
        return new String(pwdFld.getPassword());
    }

    public boolean isUseCredentials() {
        return useCredentials;
    }
}
