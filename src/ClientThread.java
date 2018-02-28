import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ClientThread extends Thread {
	Socket Socket;
	String username;
	BufferedReader inFromClient;
	DataOutputStream outToClient;
	int ServerNumber;

	public ClientThread(Socket Socket, BufferedReader inFromClient, DataOutputStream outToClient, int ServerNumber)
			throws IOException {
		this.Socket = Socket;
		this.inFromClient = inFromClient;
		this.outToClient = outToClient;
		this.ServerNumber = ServerNumber;
	}

	@Override
	public void run() {
		try {

			while (true) {

				String clientSentence = inFromClient.readLine();

				if (clientSentence.toLowerCase().equals("exit")) {
					ArrayList<ClientThread> ListOfClients = null;
					if (ServerNumber == 1)
						ListOfClients = Server1.getListOfClients();
					else if (ServerNumber == 2)
						ListOfClients = Server2.getListOfClients();

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
						ArrayList<ClientThread> myc = null;
						if (ServerNumber == 1)
							myc = Server1.getListOfClients();
						else if (ServerNumber == 2)
							myc = Server2.getListOfClients();

						if (user.equals("username")) {
							if (msg.equals("all")) {
								if (myc == null || myc.size() == 1)
									outToClient.writeBytes("No Active Clients" + '\n');
								else
									for (ClientThread s : myc) {
										if (s.getUsername() != getUsername())
											outToClient.writeBytes(s.getUsername() + '\n');
									}

								getFromTheOtherServer();

							} else {

								if (getUsername() != null) {
									System.out.println(getUsername() + " Changed his username to " + msg);
									setUsername(msg);
								} else {
									setUsername(msg);
									System.out.println(msg + " Joined");
								}
								outToClient.writeBytes("Your username is set to " + msg + '\n');
								outToClient.writeBytes("You are connected to Server " + ServerNumber + '\n');
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

							if (found) {
								found = false;
							} else {
								outToClient.writeBytes(
										"Username not found on the server we will check the other servers" + '\n');
								try {

									boolean found1 = false;

									if (ServerNumber == 1) {
										Socket clientSocket = null;
										clientSocket = new Socket("mmsmhh", 6022);

										DataOutputStream outToServer = new DataOutputStream(
												clientSocket.getOutputStream());

										BufferedReader inFromServer = new BufferedReader(
												new InputStreamReader(clientSocket.getInputStream()));

										outToServer.writeBytes(getUsername() + "#" + user + "#" + msg + '\n');
										String answer = null;
										System.out.println("Sending to server 2");
										while (true) {
											answer = inFromServer.readLine();
											System.out.println("Waiting for server 2");
											if (answer.equals("false") || answer.equals("true")) {
												System.out.println("Done");

												break;
											}
										}
										if (answer.equals("false")) {
											found1 = false;
										} else {
											found1 = true;
										}

									} else {

										Socket clientSocket = null;
										clientSocket = new Socket("mmsmhh", 6011);

										DataOutputStream outToServer = new DataOutputStream(
												clientSocket.getOutputStream());

										BufferedReader inFromServer = new BufferedReader(
												new InputStreamReader(clientSocket.getInputStream()));

										outToServer.writeBytes(getUsername() + "#" + user + "#" + msg + '\n');
										String answer = null;
										System.out.println("Sending to server 1");
										while (true) {
											answer = inFromServer.readLine();
											System.out.println("Waiting for server 2");
											if (answer.equals("false") || answer.equals("true")) {
												System.out.println("Done");

												break;
											}
										}
										if (answer.equals("false")) {
											found1 = false;
										} else {
											found1 = true;
										}

									}

									if (found1) {
										found1 = false;
										outToClient.writeBytes(
												"Found on the other server and massage sent succesfully" + '\n');
									} else {
										outToClient.writeBytes("User not found try again" + '\n');
									}

								} catch (Exception e) {
									outToClient.writeBytes("User not found try again" + '\n');
								}

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

	private void getFromTheOtherServer() throws IOException {
		
		outToClient.writeBytes("On Server " +ServerNumber+ '\n');
		try {

			boolean found1 = false;

			if (ServerNumber == 1) {
				Socket clientSocket = null;
				clientSocket = new Socket("mmsmhh", 6022);

				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				outToServer.writeBytes("username#all" + '\n');
				String answer = null;
				System.out.println("Getting clients from server 2");
				while (true) {
					answer = inFromServer.readLine();
					System.out.println("Waiting for server 2");
					if (answer != null) {
						System.out.println("Done");
						break;
					}

				}
				outToClient.writeBytes(answer + '\n');

			} else {

				Socket clientSocket = null;
				clientSocket = new Socket("mmsmhh", 6011);

				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

				outToServer.writeBytes("username#all" + '\n');
				String answer = null;
				System.out.println("Getting Clients from Server 1");
				while (true) {
					answer = inFromServer.readLine();
					System.out.println("Waiting for server 1");
					if (answer != null) {
						System.out.println("Done");
						break;

					}
				}

				outToClient.writeBytes(answer + '\n');

			}

		} catch (Exception e) {
			outToClient.writeBytes("Error Connecting to the server" + '\n');
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
