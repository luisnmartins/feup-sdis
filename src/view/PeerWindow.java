package view;

import Peer.*;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.BorderLayout;
import javax.swing.JTextField;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class PeerWindow {
	
	private Peer peer;
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		System.setProperty("java.net.preferIPv4Stack", "true");
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PeerWindow window = new PeerWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public PeerWindow() throws IOException {
		this.peer = new Peer("localhost", 5555, 1);
		initialize(peer);
	}
	
	public void seed() {
		
		 String filePath ="";
		 String torrentPath="";
		
		//CHOOSE FILE TO SEED
		JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
        	File file = fileChooser.getSelectedFile();
            if (file == null) {
                return;
            }

            filePath = fileChooser.getSelectedFile().getAbsolutePath();
            System.out.println("FILE: " + filePath);
        }
        
      //CHOOSE WHERE TO SAVE TORRENT
        JFileChooser folderChooser = new JFileChooser(); 
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderChooser.setAcceptAllFileFilterUsed(false);
           
        if (folderChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) { 
        	File file =  folderChooser.getCurrentDirectory();
        	 if (file == null) {
                 return;
             }
        	torrentPath = folderChooser.getSelectedFile().getAbsolutePath();
        	System.out.println("TORRENT: " + torrentPath);
        }else {
        	System.out.println("No Selection ");
         }
   
		try {
			this.peer.seed(filePath, torrentPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void download() {
		
		String torrentPath ="";
		String filePath = "";
		//CHOOSE FILE TO SEED
		JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
        	File file = fileChooser.getSelectedFile();
            if (file == null) {
                return;
            }

            torrentPath = fileChooser.getSelectedFile().getAbsolutePath();
            System.out.println("TORRENT: " + torrentPath);
        }
        
        
      //CHOOSE WHERE TO SAVE TORRENT
        JFileChooser folderChooser = new JFileChooser(); 
        folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        folderChooser.setAcceptAllFileFilterUsed(false);
           
        if (folderChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) { 
        	File file =  folderChooser.getCurrentDirectory();
        	 if (file == null) {
                 return;
             }
        	filePath = folderChooser.getSelectedFile().getAbsolutePath();
        	System.out.println("FILE: " + filePath);
        }else {
        	System.out.println("No Selection ");
         }
      
		try {
			this.peer.download(torrentPath, filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(Peer peer) {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 104);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}
		});
		frame.getContentPane().add(btnRegister, BorderLayout.NORTH);
		
		JButton btnDownload = new JButton("Download");
		btnDownload.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				download();
			}
		});
		frame.getContentPane().add(btnDownload, BorderLayout.SOUTH);
		
		JButton btnSeed = new JButton("Seed");
		btnSeed.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				seed();
			}
		});
		frame.getContentPane().add(btnSeed, BorderLayout.CENTER);
	}

}
