package de.tum.netsec;

import java.awt.List;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
 


public class Enrollment {
	private static final String SERVER = "fulcrum.net.in.tum.de";
	private static final int PORT = 34151;
	private static Socket socket;

	public static void main(String[] args) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		byte[] responseFromServer = new byte[12];
		String thirtyzero = "0000";
		System.out.println(ZonedDateTime.now(ZoneId.systemDefault()));
		
		socket = new Socket(SERVER, PORT);
		DataInputStream inStream = new DataInputStream(socket.getInputStream());
		DataOutputStream oStream = new DataOutputStream(socket.getOutputStream());
		
		inStream.read(responseFromServer, 0, responseFromServer.length);
//		new SecureRandom().nextBytes(responseFromServer);
		
		int packagesize=0;
		packagesize = 8+4+8;
		int teamNum = 65536; 
		int project =39943;
		int msgCode = 681;
		String data = "jawad.tahir@tum.de\r\nJawad\r\nTahir";
		packagesize += data.getBytes().length;
		ByteBuffer bb = java.nio.ByteBuffer.allocate(packagesize);
		bb.put(responseFromServer, 4, 8);
		
		bb.put((byte) (teamNum & 0xFF));
		bb.put((byte) ((teamNum >> 8) & 0xFF));
		bb.put((byte) (project & 0xFF));
		bb.put((byte) ((project >> 8) & 0xFF));
		
		boolean found = false;
		while (found == false) {
			byte[] rand = new byte[8];
			SecureRandom sr = new SecureRandom();
			sr.nextBytes(rand);
			bb.position(12);
			bb.put(rand);
			bb.put(data.getBytes());
			HashCode hc = Hashing.sha256().hashBytes(bb.array());
			String hash = hc.toString();
			if (hash.startsWith(thirtyzero)) {
				found = true;
				System.out.println(hash);
				System.out.println(ZonedDateTime.now(ZoneId.systemDefault()));
				
			}
			
		}
		ByteBuffer bytebuff = java.nio.ByteBuffer.allocate(4 + bb.capacity());
		bytebuff.put((byte) (bytebuff.capacity() & 0xFF));
		bytebuff.put((byte) ((bytebuff.capacity() >> 8) & 0xFF));
		bytebuff.put((byte) (msgCode & 0xFF));
		bytebuff.put((byte) ((msgCode >> 8) & 0xFF));
		bytebuff.put(bb);
		
		oStream.write(bytebuff.array());
		byte[] resp = new byte[1024];
		inStream.read(resp, 0, resp.length);
		
		
		socket.close();
		 
//		packet.addAll(Arrays.asList(ArrayUtils.toObject(Arrays.copyOf(original, newLength))))
//		int challenge = new BigInteger(Arrays.copyOfRange(responseFromServer, 4, 12)).intValue();
//		System.out.println(new BigInteger(Arrays.copyOfRange(responseFromServer, 4, 12)).intValue());
//		
//		System.out.println(Hashing.sha256().hashBytes(arr).toString());
//		System.out.println(Hashing.sha256().hashBytes(ArrayUtils.toPrimitive( packet.toArray(new Byte[packet.size()]))).toString());
		

	}

}
