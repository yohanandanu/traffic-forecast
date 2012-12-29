package vn.edu.hcmut.cse.trafficdirection.network;

//response to client
//UPDATE:ID:Speed:Density:Time
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import vn.edu.hcmut.cse.trafficdirection.database.DatabaseHelper;

public class TCPClient extends Thread {

	public DatagramSocket socket = null;
	private DatabaseHelper md;
	public PrintWriter out;
	public ArrayList<String> stack = new ArrayList<String>();

	public TCPClient(DatabaseHelper md) {
		this.md = md;
		stack.clear();
	}

	public void run() {
		try {
			// InetAddress serverAddr =
			// InetAddress.getByName("www.vre.cse.hcmut.edu.vn");

			// InetAddress serverAddr = InetAddress.getByName("10.0.2.2");
			InetAddress serverAddr = InetAddress.getByName("192.168.2.1");

			Socket socket = new Socket(serverAddr, 6655);

			String update = "";

			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));

			while (true) {
				String serverMessage = in.readLine();

				if (serverMessage != null) {
					update = "UPDATE " + DatabaseHelper.NODE_TABLE_NAME
							+ " SET Speed = " + serverMessage.split(":")[2]
							+ ", Density = " + serverMessage.split(":")[3]
							+ ", TimeStamp = " + serverMessage.split(":")[4]
							+ " WHERE ID = " + serverMessage.split(":")[1];
					md.getWritableDatabase().execSQL(update);
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class SentData extends Thread {

	}
}
