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

					if (clientSentence.contains("#") && clientSentence.length() > 4) {
						String[] word = clientSentence.split("#");

						String user = word[0];
						String msg = word[1];
						String ttl = word[2];

						ArrayList<ClientThread> myc = null;
						if (ServerNumber == 1)
							myc = Server1.getListOfClients();
						else if (ServerNumber == 2)
							myc = Server2.getListOfClients();

						if (user.equals("username")) {
							if (msg.equals("all")) {

								String allusers = "";
								/////////
								if (myc == null || myc.size() == 1)
									allusers = "null";
								else
									for (ClientThread s : myc) {
										if (s.getUsername() != getUsername()) {
											if (myc.get(myc.size() - 1).equals(s))
												allusers += s.getUsername();
											else
												allusers += s.getUsername() + ",";

										}
									}

								allusers += "#1#2#" + getFromTheOtherServer();

								outToClient.writeBytes(allusers + '\n');

								//////////

							} else {

								if (getUsername() != null) {

									if (ServerNumber == 1) {
										if (userIsInside(Server1.getListOfClients(), msg)
												|| userIsInside(Server2.getListOfClients(), msg)) {
											outToClient.writeBytes("username#already#exists" + '\n');

										} else {
											System.out.println(getUsername() + " Changed his username to " + msg);
											setUsername(msg);
										}

									} else {
										if (userIsInside(Server2.getListOfClients(), msg)
												|| userIsInside(Server1.getListOfClients(), msg)) {
											outToClient.writeBytes("username#already#exists" + '\n');

										} else {
											System.out.println(getUsername() + " Changed his username to " + msg);
											setUsername(msg);
										}

									}

								} else {
									if (ServerNumber == 1) {
										if (userIsInside(Server1.getListOfClients(), msg)
												|| userIsInside(Server2.getListOfClients(), msg)) {
											outToClient.writeBytes("username#already#exists" + '\n');
										} else {
											setUsername(msg);
											System.out.println(msg + " Joined");
										}
									} else {
										if (userIsInside(Server2.getListOfClients(), msg)
												|| userIsInside(Server1.getListOfClients(), msg)) {
											outToClient.writeBytes("username#already#exists" + '\n');
										} else {
											setUsername(msg);
											System.out.println(msg + " Joined");
										}
									}
								}

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
									if (Integer.parseInt(ttl) > 1)
										outToClient1.writeBytes(getUsername() + ": " + msg + '\n');
									else
										outToClient.writeBytes("#ttl#" + '\n');

								}

							}

							if (found) {
								found = false;
							} else {
								// outToClient.writeBytes(
								// "Username not found on the server we will check the other servers" + '\n');
								try {

									boolean found1 = false;

									if (Integer.parseInt(ttl) > 2) {

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
											// outToClient.writeBytes(
											// "Found on the other server and massage sent succesfully" + '\n');
										} else {
											outToClient.writeBytes("User not found try again" + '\n');
										}

									} else {
										outToClient.writeBytes("#ttl#" + '\n');
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

	private boolean userIsInside(ArrayList<ClientThread> listOfClients, String msg) {
		// TODO Auto-generated method stub
		if (listOfClients != null)
			for (ClientThread x : listOfClients) {
				if (x.getUsername() != null && x.getUsername().equals(msg))
					return true;
			}
		return false;
	}

	private String getFromTheOtherServer() throws IOException {

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
				return answer;

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

				return answer;

			}

		} catch (Exception e) {
			outToClient.writeBytes("Error Connecting to the server" + '\n');
			e.printStackTrace();
			return "Error";

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
