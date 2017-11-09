package client;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import clock.VectorClock;
import clock.VectorClockComparator;
import message.Message;
import message.MessageComparator;
import message.MessageTypes;
import queue.PriorityQueue;

public class VectorClockClient {

	PriorityQueue<Message> messageQueue;
	VectorClock sendClock;
	VectorClock printClock;
	Integer pid;
	Scanner input;
	DatagramSocket server;
	InetAddress address;
	String userName;
	int port;

	public VectorClockClient() throws SocketException, UnknownHostException {
		messageQueue = new PriorityQueue<Message>(new MessageComparator());
		sendClock = new VectorClock();
		printClock = new VectorClock();
		input = new Scanner(System.in);
		int x = 2000;
		while (server == null && x < 30000) {
			try {
				server = new DatagramSocket(x);
			} catch (BindException e) {
				x++;
			}
		}
		address = InetAddress.getLocalHost();
		port = 8000;
		pid = -1;
	}

	public boolean register() {
		System.out.println("What is your name?");
		userName = input.nextLine();
		Message message = new Message(MessageTypes.REGISTER, userName, pid, sendClock, userName);
		Message.sendMessage(message, server, address, 8000);
		Message reply = Message.receiveMessage(server);
		pid = reply.pid;
		sendClock.addProcess(pid, 0);
		printClock.addProcess(pid, 0);
		if (reply.type == MessageTypes.ERROR) {
			System.out.println("Registration Error, goodbye");
			return false;
		}
		return true;
	}

	public static void main(String[] args) {

		VectorClockClient client;
		try {
			client = new VectorClockClient();
			if (!client.register()) {
				System.exit(1);
			}
			Thread listen = new Thread(new Listener(client));
			listen.start();

			Thread send = new Thread(new Sender(client));
			send.start();

			Thread print = new Thread(new Printer(client));
			print.start();

		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void print() {
		while (true) {
			synchronized (messageQueue) {
				if (!messageQueue.isEmpty()) {
					Message next = messageQueue.peek();
					// System.out.println("Trying: "+next.message+" PClock
					// "+printClock.toString()+" vs MClock
					// "+next.ts.toString());
					VectorClockComparator vcc = new VectorClockComparator();
					if (vcc.compare(next.ts, printClock) <= 0) {
						System.out.println(next.sender + ": " + next.message);
						printClock.update(next.ts);
						messageQueue.poll();
						continue;
					}
					VectorClock merge = new VectorClock();
					for (String pid : VectorClock.getPids(printClock.toString())) {
						merge.update(printClock);
					}
					for (String pid : VectorClock.getPids(next.ts.toString())) {
						merge.update(next.ts);
					}

					boolean print = false;
					if (next.ts.getTime(next.pid) == printClock.getTime(next.pid) + 1) {
						print = true;
						for (String pid : VectorClock.getPids(merge.toString())) {
							if (next.pid == Integer.valueOf(pid)) {
								continue;
							}
							if (next.ts.getTime(pid) > (printClock.getTime(pid))) {
								print = false;
							}
						}
					}
					if (print) {
						System.out.println(next.sender + ": " + next.message + " " + next.ts.toString());
						printClock.update(next.ts);
						messageQueue.poll();
					}
				}
			}
		}
	}

	public void send() {
		int x = 0;
		while (pid == -1) {
			try {
				Thread.sleep(100);
				x++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (x > 1000) {
				System.out.println("pid not set error, goodbye.");
				System.exit(1);
			}
		}
		String send = "";
		while (!send.equals("quit")) {
			if (!userName.contains("AutoType")) {
				System.out.print(userName + ": ");
				send = input.nextLine();
			} else {
				send = "" + (++x);
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (x >= 10) {
					send = "quit";
				}
			}
			sendClock.tick(pid);
			Message m = new Message(MessageTypes.CHAT_MSG, userName, pid, sendClock, send);
			Message.sendMessage(m, server, address, port);
		}
		System.exit(0);
	}

	public void listen() {
		while (true) {
			Message m = Message.receiveMessage(server);
			// System.out.println("Recieved: " + m.toString());
			sendClock.update(m.ts);
			if (m.type == MessageTypes.CHAT_MSG) {
				// System.out.println("Added: " + m.sender + ": " + m.message +
				// " " + m.ts.toString());
				synchronized (messageQueue) {
					messageQueue.add(m);
				}
			}
		}
	}
}
