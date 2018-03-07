import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window.Type;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

public class ClientGUI {

	public JFrame frame;
	public JLabel username, ServerNumber;
	JTextField myMsg;
	JList<String> Msgs, Clients;
	DefaultListModel<String> listOfMsgs, listOfClients;
	public Button Send;
	public Button refresh;
	private JScrollPane scrollPane_1;
	private JLabel lblNewLabel;

	JCheckBox autoRefresh;

	public ClientGUI() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame("Chatting Application");
		ImageIcon img = new ImageIcon("src/assest/icon.png");
		frame.setIconImage(img.getImage());
		frame.setType(Type.NORMAL);
		frame.setResizable(false);
		frame.setForeground(new Color(240, 240, 240));
		frame.setBackground(new Color(240, 240, 240));
		frame.setSize(new Dimension(800, 500));
		frame.getContentPane().setLayout(null);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

		listOfMsgs = new DefaultListModel<>();
		listOfClients = new DefaultListModel<>();

		myMsg = new JTextField();
		myMsg.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
		myMsg.setBounds(10, 402, 635, 48);
		frame.getContentPane().add(myMsg);

		username = new JLabel("Username");
		username.setBackground(Color.WHITE);
		username.setForeground(new Color(0, 0, 0));
		username.setHorizontalAlignment(SwingConstants.CENTER);
		username.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
		username.setBounds(10, 0, 586, 53);
		frame.getContentPane().add(username);

		Send = new Button("Send");

		Send.setForeground(Color.WHITE);
		Send.setBackground(new Color(0, 102, 204));
		Send.setFont(new Font("Comic Sans MS", Font.BOLD, 22));
		Send.setBounds(651, 402, 123, 48);
		frame.getContentPane().add(Send);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(606, 191, 168, 200);
		frame.getContentPane().add(scrollPane_1);

		Clients = new JList<String>(listOfClients);
		Clients.setBounds(606, 110, 168, 282);
		Clients.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		scrollPane_1.setViewportView(Clients);

		ServerNumber = new JLabel("Server #");
		ServerNumber.setHorizontalAlignment(SwingConstants.CENTER);
		ServerNumber.setForeground(Color.BLACK);
		ServerNumber.setFont(new Font("Comic Sans MS", Font.PLAIN, 25));
		ServerNumber.setBounds(606, 0, 168, 53);
		frame.getContentPane().add(ServerNumber);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 56, 590, 336);
		frame.getContentPane().add(scrollPane);

		UIManager.put("List.focusCellHighlightBorder", BorderFactory.createEmptyBorder());

		Msgs = new JList<String>(listOfMsgs);
		// Msgs.setse
		Msgs.setForeground(Color.BLACK);
		Msgs.setSelectionBackground(Color.white);
		Msgs.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
		Msgs.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		Msgs.setBounds(10, 56, 590, 336);
		Msgs.setAutoscrolls(true);
		Msgs.setCellRenderer(new MyListRenderer());
		scrollPane.setViewportView(Msgs);

		refresh = new Button("Refresh");
		refresh.setFont(new Font("Comic Sans MS", Font.PLAIN, 22));
		refresh.setForeground(Color.WHITE);
		refresh.setBackground(new Color(0, 102, 204));
		refresh.setBounds(606, 56, 168, 45);
		frame.getContentPane().add(refresh);

		lblNewLabel = new JLabel("Member List");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 23));
		lblNewLabel.setBounds(606, 154, 168, 26);
		frame.getContentPane().add(lblNewLabel);

		autoRefresh = new JCheckBox("Auto Refresh");
		autoRefresh.setSelected(true);
		autoRefresh.setHorizontalAlignment(SwingConstants.CENTER);
		autoRefresh.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
		autoRefresh.setBounds(606, 117, 168, 23);
		autoRefresh.setFocusable(false);
		frame.getContentPane().add(autoRefresh);

	}

	public void setUsername(String Username) {
		username.setText(Username);
	}

	public void setServer(String serverNumber) {
		ServerNumber.setText("Server " + serverNumber);

	}

	public void receiveMsg(String fromServer) {
		listOfMsgs.add(listOfMsgs.getSize(), fromServer);

	}

	public void receiveClients(String fromServer) {

		Clients.enable(true);

		String[] all = fromServer.split("#1#2#");
		listOfClients.clear();
		String[] all1 = all[0].split(",");
		String[] all2 = all[1].split(",");

		if (all[0].equals("null") && all[1].equals("null")) {
			listOfClients.add(listOfClients.getSize(), "No Active users");
			Clients.enable(false);

		} else if (all[1].equals("null")) {
			for (String user : all1) {
				if (!user.isEmpty())
					listOfClients.add(listOfClients.getSize(), user);
			}
		} else if (all[0].equals("null")) {
			for (String user : all2) {
				if (!user.isEmpty())
					listOfClients.add(listOfClients.getSize(), user);
			}
		} else {
			for (String user : all1) {
				if (!user.isEmpty())
					listOfClients.add(listOfClients.getSize(), user);
			}
			for (String user : all2) {
				if (!user.isEmpty())
					listOfClients.add(listOfClients.getSize(), user);
			}
		}

	}

	public String getMyMsg() {
		// TODO Auto-generated method stub
		String msg = myMsg.getText();

		if (getMyClient() != null && !msg.isEmpty()) {
			listOfMsgs.add(listOfMsgs.getSize(), "You: @" + getMyClient() + " : " + msg);
			myMsg.setText("");
		}

		return msg;

	}

	public String getMyClient() {
		String msg = Clients.getSelectedValue();
		return msg;
	}

	public boolean getAutoRefreshStatus() {
		return autoRefresh.isSelected();
	}
}

class MyListRenderer extends DefaultListCellRenderer {
	private HashMap theChosen = new HashMap();

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		String v = (String) value;

		if (v.contains("@")) {
			setForeground(Color.blue);

		} 

		return (this);
	}
}
