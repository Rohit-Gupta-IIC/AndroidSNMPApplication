package telnet;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;

import org.apache.commons.net.telnet.TelnetClient;

import android.util.Log;

public class telnetInstance {

	private static TelnetClient telnet = new TelnetClient();
	private static InputStream in;
	private static PrintStream out;

	public static void telnetMethod(String server, String login, String pass,
			String command) throws SocketException, IOException {

		Log.e("", "started " + server);

		telnet.connect(server, 23);
		in = telnet.getInputStream();
		out = new PrintStream(telnet.getOutputStream());

		try {

			// Log the user on
			readUntil("login: ");

			write(login);
			readUntil("password: ");

			write(pass);
			readUntil("test>");

			write(command);

			readUntil("terminated");
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void telnetKillProcess(String server, String login,
			String pass) throws IOException {
		//
		String[] list = new String[] { "notepad.exe", "firefox.exe",
				"iTunes.exe" };
		Log.e("", "started " + server);

		in = new BufferedInputStream(in, 8192);

		telnet = new TelnetClient();
		telnet.connect(server, 23);
		in = telnet.getInputStream();
		out = new PrintStream(telnet.getOutputStream());

		try {
			// Log the user on
			readUntil("login: ");

			out.println(login + "\r");
			out.flush();
			readUntil("password: ");

			out.println(pass + "\r");
			out.flush();
			readUntil("test>");

			// Log.e("", "here be faggotry");

			for (int i = 0; i < list.length; i++) {
				Log.e("", list[i]);

				String command = "Taskkill /IM " + list[i] + " /F";

				out.println(command + " \r");
				out.flush();
				//
				String note = readUntil("test>");
				Log.e("", note);

				if (note.contains("PID")) {

					Log.e("", "PID keyword found");
					out.println("Taskkill /IM " + list[i] + " /F" + " \r");
					out.flush();
				} else {

					Log.e("", "not found");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//readUntil("terminated");
		Log.e("", "telnet disconnect");
		telnet.disconnect();

	}

	public void disconnect() {
		try {
			in.close();
			out.close();
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readUntil(String pattern)
			throws UnsupportedEncodingException {

		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();

			char ch = (char) in.read();

			while (true) {
				//Log.e("", ch+"");

				sb.append(ch);
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return sb.toString();
					}
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void write(String value) {
		try {
			out.println(value);
			out.flush();
			System.out.println(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readUntil2(String pattern)
			throws UnsupportedEncodingException {

		try {
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();

			byte[] buf = new byte[in.available()];
			char ch = (char) in.read(buf);

			while (true) {

				sb.append(ch);
				if (ch == lastChar) {
					if (sb.toString().endsWith(pattern)) {
						return sb.toString();
					}
				}
				ch = (char) in.read();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
