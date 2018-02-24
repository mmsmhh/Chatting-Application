import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {
	Socket Socket;
	String username;
	BufferedReader inFromClient;
	DataOutputStream outToClient;

	public ClientThread(Socket Socket, BufferedReader inFromClient, DataOutputStream outToClient) throws IOException {
		this.Socket = Socket;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
	}

	@Override
	public void run() {
		try {

			while (true) {

				String clientSentence = inFromClient.readLine();

				if (clientSentence.toLowerCase().equals("exit")) {

					ArrayList<ClientThread> ListOfClients = Server.getListOfClients();

					for (int i = 0; i < ListOfClients.size(); i++) {

						if (ListOfClients.get(i).username.equals(getUsername())) {
							ListOfClients.remove(i);
							System.out.println(getUsername() + " Left the chat");
						}

					}

					return;
				} else {

					if (clientSentence.contains("#") && clientSentence.length() > 3) {
						String[] word = clientSentence.split("#");

						String user = word[0];
						String msg = word[1];

						ArrayList<ClientThread> myc = Server.getListOfClients();

						if (user.equals("username")) {
							
							
							if(msg.equals("all"))
							{
								for (ClientThread s : myc )
								{
									if(s.getUsername() != getUsername())
									outToClient.writeBytes(s.getUsername() + '\n');
								}
							}
							else
							{
							
							if (getUsername() != null) {
								System.out.println(getUsername() + " Changed his username to " + msg);
								setUsername(msg);
							} else {
								setUsername(msg);
								System.out.println(msg + " Joined");
							}
							outToClient.writeBytes("Your username is set to " + msg + '\n');
							outToClient.writeBytes("To change you username type username#blablabla" + '\n');
							
							}
							

						} else if (user.equals(getUsername())) {
							outToClient.writeBytes("You can't send a msg to yourself" + '\n');

						} else {

							boolean found = false;
							
							for (int i = 0; i < myc.size(); i++) {

								if (myc.get(i).username.equals(user)) {
									Socket connectionSocket = myc.get(i).getSocket();

									DataOutputStream outToClient1 = new DataOutputStream(
											connectionSocket.getOutputStream());
									found = true;
									outToClient1.writeBytes(getUsername() + ">>>" + msg + '\n');

								}

							}
							
							if(found)
							{
								found = false;
							}
							else
							{
								outToClient.writeBytes("User not found try again" + '\n');
							}
						}

					} else {
						outToClient.writeBytes("Wrong format username#blablabla" + '\n');
					}

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public Socket getSocket() {
		return Socket;
	}

	public void setSocket(Socket socket) {
		Socket = socket;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
