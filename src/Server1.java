import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server1 {

	private static ArrayList<ClientThread> ListOfClients;

	public static void main(String args[]) throws Exception {

		ListOfClients = new ArrayList<ClientThread>();

		Thread WaitForServer = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					ServerSocket welcomeSocket = new ServerSocket(6011);

					while (true) {

						Socket connectionSocket = welcomeSocket.accept();

						BufferedReader inFromClient = new BufferedReader(
								new InputStreamReader(connectionSocket.getInputStream()));

						DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

						String s = inFromClient.readLine();

						if (!s.equals("username#all")) {

							String[] word = s.split("#");

							String sender = word[0];
							String reciever = word[1];
							String msg = word[2];
							Boolean found = false;
							ArrayList<ClientThread> ListOfClientss = getListOfClients();

							for (ClientThread c : ListOfClientss) {
								if (c.getUsername().equals(reciever)) {

									Socket connectionSocket2 = c.getSocket();

									DataOutputStream outToClient1 = new DataOutputStream(
											connectionSocket2.getOutputStream());
									outToClient1.writeBytes(sender + " : " + msg + '\n');
									found = true;
								}
							}

							if (found) {
								outToClient.writeBytes("true" + '\n');
								found = false;
								System.out
										.println(sender + " from server 2 sent a msg to " + reciever + " Succesfully");
							} else {
								outToClient.writeBytes("false" + '\n');
								System.out.println(sender + " from server 2 want to sent a msg to " + reciever
										+ " but our server doesn't have this user");

							}

						} else {
							String x = "";
							if (ListOfClients.size() > 0) {
								for (ClientThread c : Server1.ListOfClients) {
									if (Server1.ListOfClients.get(Server1.ListOfClients.size()-1).equals(c))
										x += c.getUsername();
									else
										x += c.getUsername() + ",";
								}
								outToClient.writeBytes(x + '\n');

							}
							else {
								outToClient.writeBytes("null" + '\n');

							}

						}
						connectionSocket.close();
						outToClient.close();
						inFromClient.close();

					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		WaitForServer.start();

		ServerSocket welcomeSocket = new ServerSocket(6001);

		System.out.println("Server 1 started successfully");

		try {
			while (true) {

				Socket connectionSocket = welcomeSocket.accept();

				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));

				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

				ClientThread t = new ClientThread(connectionSocket, inFromClient, outToClient, 1);
				getListOfClients().add(t);
				t.start();
			}
		} finally {
			welcomeSocket.close();
		}
	}

	public static ArrayList<ClientThread> getListOfClients() {
		return ListOfClients;
	}

	public static void setListOfClients(ArrayList<ClientThread> listOfClients) {
		ListOfClients = listOfClients;
	}

	public static boolean isServerOnline() {
		try (Socket s = new Socket("mmsmhh", 6001)) {
			return true;
		} catch (IOException ex) {
		}
		return false;
	}
}
