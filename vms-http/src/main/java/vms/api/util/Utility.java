package vms.api.util;

import java.util.List;

import java.util.ArrayList;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This utility contain basic functions for simple repeated problems
 * 
 * @author Parveen Jangra
 */

@Component
public class Utility {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	static volatile int seed = 0;
	

	public Utility() {
	}

	/**
	 * send String over UDP with prefixed length
	 * 
	 * @param data
	 *            data String need to send over
	 * @param ip
	 *            ip of host server
	 * @param port
	 *            port of host server
	 * @param lengthDigits
	 *            total digit of prefix length , like for "00000" set lengthDigits =
	 *            5
	 * @return boolean status true/false ( success/failed)
	 */
	public boolean sendOverUdpPefixLength(String data, String ip, int port, int lengthDigits) {

		if (lengthDigits > 0) {
			if (lengthDigits < ("" + data.length()).length())
				lengthDigits = ("" + data.length()).length();
			String base = null;
			for (int i = 0; i < lengthDigits; i++)
				base = base + "0";

			data = base.substring(("" + data.length()).length()) + data.length() + data;
		}
		return sendOverUdp(data, ip, port);
	}

	/**
	 * send String over UDP
	 * 
	 * @param data
	 *            data String need to send over
	 * @param ip
	 *            ip of host server
	 * @param port
	 *            port of host server
	 * @return boolean status true/false ( success/failed)
	 */
	public boolean sendOverUdp(String data, String ip, int port) {
		boolean status = true;
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(ip);
			DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.length(), IPAddress, port);
			clientSocket.send(sendPacket);
			clientSocket.close();
			logger.debug(data + " " + ip + ":" + port);
		} catch (Exception e) {
			status = false;
		}
		return status;
	}

	public void sendOverUdpPacket(String data, String ip, int port) {
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(ip);
			DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.length(), IPAddress, port);
			clientSocket.send(sendPacket);
			clientSocket.close();
			// logger.debug(data+" "+ip+":"+port);
		} catch (Exception e) {
			logger.error("Exception ip:" + ip + ":" + port);
			e.printStackTrace();
		}
	}

	public void sendBufferOverUdp(byte[] data, String ip, int port) {
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName(ip);
			DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
			clientSocket.send(sendPacket);
			clientSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Validate if string contains all numberic number ( 0-9 )
	 * 
	 * @param number
	 *            String need to check
	 * @return boolean true if numeric , false if non-numeric
	 */
	public boolean isNumeric(String number) {
		if (number == null || number.length() == 0)
			return false;
		for (int i = 0; i < number.length(); i++) {
			if (number.charAt(i) < '0' || number.charAt(i) > '9')
				return false;
		}
		return true;
	}

	/**
	 * get calculated date with added minutes
	 * 
	 * @param min
	 *            total number of minute to be calculated on current date , also if
	 *            required previos dates send value in -ve , like for date 60 min
	 *            old send -60
	 * @return String formated date , format will be "yyyy-MM-dd HH:mm:ss"
	 */
	public String addMinuteInDate(int min) {
		return getCalculatedDate(Calendar.MINUTE, min, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * get calculated date with added hours
	 * 
	 * @param hour
	 *            total number of hour to be calculated on current date , also if
	 *            required previos dates send value in -ve , like for previous hour
	 *            send -1
	 * @return String formated date , format will be "yyyy-MM-dd HH:mm:ss"
	 */
	public String addHourInDate(int hour) {
		return getCalculatedDate(Calendar.HOUR, hour, "yyyy-MM-dd HH:mm:ss");
	}

	public String addDayInDate(String date, int days) {
		String convertedDate = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateFormat.parse(date));
			cal.add(Calendar.DATE, days);
			convertedDate = dateFormat.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}

	public Date addDayInDate(Date date, int days) {
		Calendar cal = null;
		try {
			cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, days);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cal.getTime();
	}

	public java.sql.Date getConvertedDateTime(String date, String format) {

		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			java.util.Date d = dateFormat.parse(date);
			java.sql.Date sqlStartDate = new java.sql.Date(d.getTime());
			return sqlStartDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String covertSqlDateToString(java.sql.Date date, String format) {
		java.util.Date newDate = new Date(date.getTime());
		return getDateTime(newDate, format);
	}

	/**
	 * get calculated date
	 * 
	 * @param calenderField
	 *            java.util.Calendar parameters for various bases need to modify ,
	 *            like Calender.MINUTE , Calender.HOUR , Calender.DATE ..etc
	 * @param changeValue
	 *            value need to add in specified above parameters
	 * @param format
	 *            Format of return date in string , support fields from
	 *            java.text.SimpleDateFormat
	 * @return String formated date
	 */
	public String getCalculatedDate(int calenderField, int changeValue, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c1 = Calendar.getInstance();
		c1.add(calenderField, changeValue);
		return sdf.format(c1.getTime());
	}

	/**
	 * get current date time
	 * 
	 * @return String formated date , format will be "yyyy-MM-dd HH:mm:ss"
	 */
	public String getDateTime() {
		return getDateTime("yyyy-MM-dd HH:mm:ss");
	}

	public Date getDate(String format) {

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c1 = Calendar.getInstance();
		return c1.getTime();
	}

	public Date addMinuteInDate(Date date, int min) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, min);
		return cal.getTime();
	}

	/**
	 * get current date time
	 * 
	 * @param format
	 *            Format of return date in string , support fields from
	 *            java.text.SimpleDateFormat
	 * @return String current date in specified format
	 */
	public String getDateTime(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Calendar c1 = Calendar.getInstance();
		return sdf.format(c1.getTime());
	}

	public String getDateTime(Date date, String format) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(cal.getTime());

	}

	/**
	 * get full stack trace of Exception in string
	 * 
	 * @param exception
	 *            Exception object need to analyze
	 * @return String full stack trace as String
	 */
	public String getPrintStackTrace(Exception exception) {
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		exception.printStackTrace();
		return sw.toString();
	}

	/**
	 * get full stack trace of Exception in string
	 * 
	 * @param throwable
	 *            Throwable object need to analyze
	 * @return String full stack trace as String
	 */

	public static String getStackTrace(Exception exception) {
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		exception.printStackTrace();
		return sw.toString();
	}

	public int dateDiff(Date date1, Date date2) {
		long diff = date2.getTime() - date1.getTime();
		return (int) (diff / (86400 * 1000));
		// return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public int compareDate(String paramdate1, String paramdate2) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date1 = sdf.parse(paramdate1);
			Date date2 = sdf.parse(paramdate2);
			return compareDate(date1, date2);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -2;
	}

	public int compareDate(Date date1, Date date2) {
		try {
			if (date1.after(date2)) {
				return +1;
			}
			if (date1.before(date2)) {
				return -1;
			}
			if (date1.equals(date2)) {
				return 0;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -2;
	}

	public String getPrintStackTrace(Throwable throwable) {
		StringWriter sw = new StringWriter();
		throwable.printStackTrace(new PrintWriter(sw));
		throwable.printStackTrace();
		return sw.toString();
	}

	public String getDate(Date date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	public int callCPUrl(String urlString, int connectTimeout, int readTimeout, StringBuilder freeFlow,
			StringBuilder response) {
		String result = "";
		HttpURLConnection urlConnection = null;
		BufferedReader in = null;
		try {
			URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(connectTimeout * 1000);
			urlConnection.setReadTimeout(readTimeout * 1000);
			in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String freeflow = urlConnection.getHeaderField("FREEFLOW");
			if (freeflow == null) {
				freeflow = "FB";
			}
			System.out.println("freeflow ==" + freeflow);
			freeFlow.append(freeflow);
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println("test" + inputLine);

				result = result + inputLine;

				System.out.println("result==" + result);
			}
			if (result == null || result.equals("")) {
				result = "Cp no resp";
			}
			response.append(result);
			System.out.println("result freeflow =" + result);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			result = "MalformedURL Exception";
		} catch (ConnectException e) {
			e.printStackTrace();
			result = "Not able to Connect";
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			result = "Read TimeOut";
		} catch (Exception e) {
			e.printStackTrace();
			result = "Generic Exception";
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();

				}
			}

		}
		return 0;
	}

	public static String CallReURL( String urlString, int connectTimeout, int readTimeout ) {
		String inputResponse = "";
		try {
			URL url = new URL(urlString);
			URLConnection urlConn = url.openConnection();
			urlConn.setConnectTimeout(connectTimeout * 1000);
			urlConn.setReadTimeout(readTimeout * 1000);
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String inputLine;
			while ((inputLine = inputReader.readLine()) != null)
				inputResponse = inputResponse + inputLine;
			inputReader.close();
			// urlConn.close();
		} catch (Exception exp) {
			// inputResponse = exp.getMessage();
		}
		return inputResponse;
	}

	public String CallURL(String urlString) {
		String inputResponse = "";
		try {
			URL url = new URL(urlString);
			URLConnection urlConn = url.openConnection();
			urlConn.setConnectTimeout(30 * 1000);
			urlConn.setReadTimeout(30 * 1000);
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String inputLine;
			while ((inputLine = inputReader.readLine()) != null)
				inputResponse = inputResponse + inputLine;
			inputReader.close();
		} catch (Exception exp) {
			inputResponse = exp.getMessage();
		}
		return inputResponse;
	}

	public static String CallURL(String urlString, int i) {
		String inputResponse = "";
		System.out.println("xmlData  urlString =" + urlString);
		try {
			URL url = new URL(urlString);
			URLConnection urlConn = url.openConnection();
			urlConn.setConnectTimeout(30 * 1000);
			urlConn.setReadTimeout(30 * 1000);
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
			String inputLine;
			while ((inputLine = inputReader.readLine()) != null)
				inputResponse = inputResponse + inputLine;
			System.out.println("xmlData = " + inputResponse);
			inputReader.close();
		} catch (Exception exp) {
			inputResponse = exp.getMessage();
		}
		return inputResponse;
	}

	public String callUrl(String urlString, int connectTimeout, int readTimeout) {
		String result = "";
		HttpURLConnection urlConnection = null;
		BufferedReader in = null;
		try {
			URL url = new URL(urlString);
			logger.error(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(connectTimeout * 1000);
			urlConnection.setReadTimeout(readTimeout * 1000);
			in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				result = result + inputLine;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			result = "MalformedURL Exception";
		} catch (ConnectException e) {
			e.printStackTrace();
			result = "Not able to Connect";
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
			result = "Read TimeOut";
		} catch (Exception e) {
			e.printStackTrace();
			result = "Generic Exception";
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] data, String delimiter) {
		StringBuilder r = new StringBuilder(data.length * 2);
		for (byte b : data) {
			r.append(hexCode[(b >> 4) & 0xF]);
			r.append(hexCode[(b & 0xF)]);
			r.append(delimiter);
		}
		return r.toString();
	}

	public static String bytesToHex(byte[] data) {
		return bytesToHex(data, " ");
	}

	public int appendArray(byte des[], byte src[], int index) {
		int len = src.length;
		for (int l = 0; l < len; l++) {
			des[index] = src[l];
			index++;
		}
		return index;
	}

	public byte[] appendArray(byte[] source1, byte[] source2) {
		int len = source1.length + source2.length;
		byte[] dest = new byte[len];
		int i = 0;
		for (i = 0; i < source1.length; i++) {
			dest[i] = source1[i];
		}
		for (int j = 0; j < source2.length; j++) {
			dest[i++] = source2[j];
		}
		return dest;
	}

	public byte[] getCodedString(String data) {
		try {
			// data = data.replaceAll("%","");
			// byte t[] = DatatypeConverter.parseHexBinary ( data );
			// byte t[] = parseHexBinary ( data );

			// return getCodedString ( t ) ;

			// String message = URLDecoder.decode ( data , "UTF-8");
			return utf8toutf16(data);

		} catch (Exception exp) {
			// exp.printStackTrace();
		}
		return (byte[]) null;
	}

	public byte[] getCodedString(byte t[]) {
		try {
			String temp = byteArrayToHex(t);
			// String temp = new String( t , "UTF-8" );

			return utf8toutf16(temp);

		} catch (Exception exp) {
			// exp.printStackTrace();
		}
		return (byte[]) null;
	}

	public String dec2hex4(char textString) {
		char[] hexequiv = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		return "" + hexequiv[(textString >> 12) & 0xF] + hexequiv[(textString >> 8) & 0xF]
				+ hexequiv[(textString >> 4) & 0xF] + hexequiv[textString & 0xF];
	}

	public String putSpaces(String data, int digit, String spacer) {
		if (data == null)
			return data;
		StringBuilder str = new StringBuilder();
		boolean spaceOn = false;
		int counter = 1;
		for (int i = 0; i < data.length(); i++) {
			str.append(data.charAt(i));
			if (spaceOn) {
				str.append(spacer);
				spaceOn = false;
			}
			counter++;
			if (counter == digit) {
				spaceOn = true;
				counter = 0;
			}
		}
		return str.toString();
	}

	public byte[] parseHexBinary(String str) throws Exception {
		byte[] a = new BigInteger(str, 16).toByteArray();
		if (a.length != str.length() / 2) {
			a = Arrays.copyOfRange(a, 1, a.length);
		}
		return a;
	}

	public byte getHexCodePointToByte(char a, char b) {
		int c = hex2int(a) * 16 + hex2int(b);

		return ((byte) c);
	}

	public int getNumberOfTrailingByte(byte b) {

		int result = 0;
		if ((b & 0x80) == 0) {
			return 0;
		} else if ((b & 0xE0) == 0xC0) {
			return 1;
		} else if ((b & 0xF0) == 0xE0) {
			return 2;
		} else if ((b & 0xF8) == 0xF0) {
			return 3;
		} else if ((b & 0xFC) == 0xF8) {
			return 4;
		} else if ((b & 0xFE) == 0xFC) {
			return 5;
		}
		return result;
	}

	public int getUtf8byteToUnicode(byte b[], int i, int trailbytecount) {
		int counter = 0;
		int result = 0;
		switch (trailbytecount) {
		case 0:
			result = (int) b[i];
			break;
		case 1:

			result = ((int) (b[i] & 0x1F)) << 6;
			result = result | ((int) (b[i + 1] & 0x3F));
			break;
		case 2:
			result = ((b[i] & 0xF)) << 6;
			result = (result | (b[i + 1] & 0x3F)) << 6;
			result = (result | (b[i + 2] & 0x3F));
			break;
		case 3:
			result = (b[i] & 0x7) << 6;
			result = (result | (b[i + 1] & 0x3F)) << 6;
			result = (result | (b[i + 2] & 0x3F)) << 6;
			result = (result | (b[i + 3] & 0x3F));
			break;
		case 4:
			result = ((b[i] & 0x3)) << 6;
			result = (result | (b[i + 1] & 0x3F)) << 6;
			result = (result | (b[i + 2] & 0x3F)) << 6;
			result = (result | (b[i + 3] & 0x3F)) << 6;
			result = (result | (b[i + 4] & 0x3F));
			break;
		case 5:
			result = ((b[i] & 0x1)) << 6;
			result = (result | (b[i + 1] & 0x3F)) << 6;
			result = (result | (b[i + 2] & 0x3F)) << 6;
			result = (result | (b[i + 3] & 0x3F)) << 6;
			result = (result | (b[i + 4] & 0x3F)) << 6;
			result = (result | (b[i + 5] & 0x3F));
			break;
		}
		return result;
	}

	public byte[] unicodeToUTF16(int data) {

		if (data < 0xffff) {
			byte[] b = new byte[2];
			b[0] = (byte) (data >> 8);
			b[1] = (byte) (data & 0xFF);
			return b;
		} else if (data >= 0x10000 && data <= 0x10FFFF) {
			byte[] c = new byte[4];
			data = data - 65536;
			int highbit = data >> 10;
			int lowbit = data & 0x3FF;
			highbit = highbit + 0xD800;
			lowbit = lowbit + 0xDC00;
			c[0] = (byte) (highbit >> 8);
			c[1] = (byte) (highbit & 0xFF);
			c[2] = (byte) (lowbit >> 8);
			c[3] = (byte) (lowbit & 0xFF);
			return c;
		}
		return null;
	}

	public byte[] utf8toutf16(String str) throws Exception {

		str = str.replace("%", "");
		byte[] a = new byte[str.length() / 2];
		int length = 0;
		byte[][] result = new byte[str.length() / 2][];
		int index = 0, resultIndex = 0;
		for (int i = 0; i < str.length(); i = i + 2) {

			a[i / 2] = getHexCodePointToByte(str.charAt(i), str.charAt(i + 1));
			int trailbyteCount = getNumberOfTrailingByte(a[i / 2]);

			index = i / 2;
			for (int k = 0; k < trailbyteCount; k++) {
				i = i + 2;
				a[i / 2] = getHexCodePointToByte(str.charAt(i), str.charAt(i + 1));
			}
			int unicode = getUtf8byteToUnicode(a, index, trailbyteCount);
			byte[] data = unicodeToUTF16(unicode);
			result[resultIndex++] = data;
		}

		byte[] array = new byte[resultIndex * result[0].length];
		int k = 0;
		for (int i = 0; i < resultIndex; i++) {
			for (int j = 0; j < result[0].length; j++) {
				array[k++] = result[i][j];
			}
		}

		return array;

	}

	/*
	 * public byte[] utf8toutf16( byte b[] ) throws Exception {
	 * 
	 * String message = new String ( b , "UTF-8" ); //String message =
	 * URLDecoder.decode( msg ,"UTF-8" ); // byte b[] = message.getBytes();
	 * //System.out.println ( " Message is " + message ); byte result[][] = new
	 * byte[message.length() ] [] ; for ( int i=0 ; i< message.length() ; i++ ){
	 * byte b1 = (byte) message.charAt(i); // System.out.println (
	 * String.format("%02X ", b1)); String hex= int2hex( (int) message.charAt(i));
	 * byte output[] = new BigInteger ( hex , 16 ).toByteArray(); if ( output.length
	 * == 1 ){ byte al[] = new byte[2]; al[0]=0x00; al[1]=output[0]; result[i] = al;
	 * }else result[i] = output; // System.out.println ( int2hex( (int )
	 * message.charAt(i) ) + "  " + b1 ); }
	 * 
	 * int counter = 0; for ( int k = 0 ; k < result.length ; k++ ){
	 * 
	 * for ( int l =0; l< result[k].length ; l++ ){
	 * 
	 * counter ++; } // System.out.println ( " HEX VALUE " + byteArrayToHex (
	 * result[k] ) ); // System.out.println ( );
	 * 
	 * } byte b2[] = new byte[counter]; int c=0; for ( int k = 0 ; k < result.length
	 * ; k++ ){
	 * 
	 * for ( int l =0; l< result[k].length ; l++ ){
	 * 
	 * b2[c++] = result [k][l]; // System.out.println ( b2[c - 1]); }
	 * 
	 * } //System.out.println ( "utf-16 bytes length on network " + b2.length "  ,"
	 * + b2 ); return b2;
	 * 
	 * }
	 */
	public String int2hex(int dec) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		String hex = "";
		while (dec != 0) {
			int rem = dec % 16;
			hex = hexDigits[rem] + hex;
			dec = dec / 16;
		}
		return hex;

	}

	public int hex2int(char hex) {

		switch (hex) {
		case '0':
			return 0;
		case '1':
			return 1;
		case '2':
			return 2;
		case '3':
			return 3;
		case '4':
			return 4;
		case '5':
			return 5;
		case '6':
			return 6;
		case '7':
			return 7;
		case '8':
			return 8;
		case '9':
			return 9;
		case 'a':
		case 'A':
			return 10;
		case 'b':
		case 'B':
			return 11;
		case 'c':
		case 'C':
			return 12;
		case 'd':
		case 'D':
			return 13;
		case 'e':
		case 'E':
			return 14;
		case 'f':
		case 'F':
			return 15;
		}
		return -1;
	}

	public String byteArrayToHex(byte[] a) {
		StringBuilder sb = new StringBuilder(a.length * 2);
		for (byte b : a)
			sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}

	public String sort(String data, String newSeqNumber) {
		StringBuilder stringBuilder = new StringBuilder();
		StringBuilder token = new StringBuilder();
		token.append("-").append(newSeqNumber).append("-");
		int index = data.indexOf(token.toString());

		if (data.trim().length() == 0) {
			stringBuilder.append(token);

		} else {

			if (index != -1) {
				stringBuilder.append("-").append(newSeqNumber).append(data.substring(0, index))
						.append(data.substring(token.length() + index - 1, data.length()));
			} else {
				stringBuilder.append("-").append(newSeqNumber).append(data);
			}
		}
		return stringBuilder.toString();
	}

	public byte[] byteArraysCopy(byte[] a, byte[] b) {
		if (a == null)
			a = "".getBytes();
		if (b == null)
			b = "".getBytes();
		int aLen = a.length;
		int bLen = b.length;
		byte[] c = new byte[aLen + bLen];

		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	static String getSeed() {

		if (seed >= 10000)
			seed = 0;
		seed++;

		return String.format("%04d", seed);
	}

	public static String getSessionId(String Msisdn) {
		long date = System.currentTimeMillis();
		String sid = Long.toString(date);
		return sid + Msisdn.substring(7, 10) + "" + getSeed();
	}

	public int copyArray(byte[] source, int startIndex, int lastIndex, List<Byte> result) {
		for (int i = startIndex; i < lastIndex; i++) {
			result.add(source[i]);
			startIndex++;
		}
		return startIndex;
	}

	public byte[] getBytes(List<Byte> result) {
		byte b[] = new byte[result.size()];
		for (int index = 0; index < result.size(); index++) {
			b[index] = result.get(index);
		}
		return b;
	}

	public byte[] replace(byte[] source, byte[] replace, byte[] replaceWith) {
		if (source.length < 1 || replace.length < 1)
			return source;
		List<Byte> result = new ArrayList<Byte>();
		int startIndex = 0;
		int lastIndex = 0;

		for (int index = 0, counter = 0; index < source.length; index++) {
			counter = counter % replace.length;
			if (source[index] == replace[counter]) {
				counter++;
			}
			if (counter == replace.length) {
				lastIndex = index - counter + 1;
				copyArray(source, startIndex, lastIndex, result);
				copyArray(replaceWith, 0, replaceWith.length, result);
				startIndex = lastIndex + counter;
				counter = 0;
			}
		}
		if (startIndex < source.length) {
			copyArray(source, startIndex, source.length, result);
		}
		return getBytes(result);
	}

	public boolean isDate(String str, String format) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat(format);
			formatter.parse(str);
			return true;
		} catch (Exception exp) {
		}
		return false;
	}

}
