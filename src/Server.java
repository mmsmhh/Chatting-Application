import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

	private static ArrayList<ClientThread> ListOfClients;

	public static void main(String args[]) throws Exception {

		ListOfClients = new ArrayList<ClientThread>();

		ServerSocket welcomeSocket = new ServerSocket(6002);

		System.out.println("Server started successfully");

		try {
			while (true) {

				Socket connectionSocket = welcomeSocket.accept();

				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));

				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

				ClientThread t = new ClientThread(connectionSocket, inFromClient, outToClient);
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
}
