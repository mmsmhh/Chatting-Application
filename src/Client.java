import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client {

	static String ServerNumber = "";
	static String username = null;
	static ClientGUI window;
	static Socket clientSocket = null;
	static int TTL = 4;

	public static void main(String argv[]) throws IOException {

		while (username == null || username.isEmpty()) {
			username = JOptionPane.showInputDialog("Please enter your username.");
		}

		while (true) {

			String[] choices = { "1", "2" };
			ServerNumber = (String) JOptionPane.showInputDialog(null, "Which server do you want to connect to?",
					"Choose your server", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

			if (ServerNumber != null && ServerNumber.equals("1")) {
				clientSocket = new Socket("mmsmhh", 6001);
				break;
			} else if (ServerNumber != null && ServerNumber.equals("2")) {
				clientSocket = new Socket("mmsmhh", 6002);
				break;
			} else {
				ServerNumber = (String) JOptionPane.showInputDialog(null, "Which server do you want to connect to?",
						"Choose your server", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
			}

		}

		window = new ClientGUI();
		window.frame.setVisible(true);
		window.setUsername(username+"-"+ServerNumber);
		window.setServer(ServerNumber);

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

		outToServer.writeBytes("username#" + username+"-"+ServerNumber + "#" + TTL + '\n');

		Thread read = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while (true) {
						String FromServer = inFromServer.readLine();

						if (FromServer.contains("#1#2#")) {

							window.receiveClients(FromServer);
						} else if (FromServer.contains("username#already#exists")) {
							username = null;
							while (username == null || username.isEmpty()) {
								username = JOptionPane
										.showInputDialog("Username Already exists! ,Please enter your username.");
							}
							outToServer.writeBytes("username#" + username+"-"+ServerNumber + "#" + TTL + '\n');
							window.setUsername(username+"-"+ServerNumber);

						} else if (FromServer.contains("#ttl#"))
						{
							JOptionPane.showMessageDialog(window.frame, "TTL not Enough!",
									"Warning", JOptionPane.WARNING_MESSAGE);
						}
						else {
							window.receiveMsg(FromServer);
						}

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		read.start();

		Thread AutoRefresh = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					System.out.println(window.getAutoRefreshStatus() && window.Clients.getSelectedValue() == null);

					if (window.getAutoRefreshStatus() && window.Clients.getSelectedValue() == null) {
						try {
							outToServer.writeBytes("username#all" + "#" + TTL + '\n');

							Thread.sleep(3000);

						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		});
		AutoRefresh.start();

		window.refresh.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				try {
					outToServer.writeBytes("username#all" + "#" + TTL + '\n');
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		});

		window.Send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (TTL > 0) {
					String sentence = window.getMyMsg();
					String client = window.getMyClient();

					if (client == null) {
						JOptionPane.showMessageDialog(window.frame, "You must select a Member from the member list!",
								"Warning", JOptionPane.WARNING_MESSAGE);
					} else if (sentence.isEmpty()) {
						JOptionPane.showMessageDialog(window.frame, "Msg can't be EMPTY!", "Warning",
								JOptionPane.WARNING_MESSAGE);
					} else {

						String msg = client + "#" + sentence;
						try {

							outToServer.writeBytes(msg + "#" + TTL + '\n');

						} catch (IOException e) {
							e.printStackTrace();
						}

						window.Clients.clearSelection();

					}

				} else {
					JOptionPane.showMessageDialog(window.frame, "TTL is not enough", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}

			}
		});

		window.frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(WindowEvent winEvt) {
				try {
					outToServer.writeBytes("exit");
				} catch (IOException e) {
					e.printStackTrace();
				}
				read.stop();
				AutoRefresh.stop();
				try {
					clientSocket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.exit(0);
			}
		});

	}

}
