import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client {

	public static void main(String argv[]) throws IOException {

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String username = JOptionPane.showInputDialog("Please enter your username.");

		Socket clientSocket = null;
		String n = "";
		while (true) {

			System.out.println("Which server do you want to connect to?");
			n = inFromUser.readLine();

			if (n.equals("1")) {
				clientSocket = new Socket("mmsmhh", 6001);
				break;
			} else if (n.equals("2")) {
				clientSocket = new Socket("mmsmhh", 6002);
				break;
			} else {
				System.out.println("Please choose from server 1 and server 2");
			}

		}

		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


		outToServer.writeBytes("username#" + username + '\n');

		Thread read = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while (true) {
						String FromServer = inFromServer.readLine();
						System.out.println(FromServer);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		read.start();

		String sentence = "";

		while (!sentence.toLowerCase().equals("exit")) {
			try {
				sentence = inFromUser.readLine();
				outToServer.writeBytes(sentence + '\n');
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		read.stop();
		clientSocket.close();

	}
}
