package kr.kro.probono.bluetooth;

import android.bluetooth.BluetoothSocket;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;


/**
 * BlueModule. YJ Lee, yoonjin67@gmail.com
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public abstract class BlueModule extends android.content.Context {

	public String getPackageName() {
		return "kr.kro.probono.bluetooth";
	}

	public static class ConnectedThread {
		public static Thread thr;
		public static void start() {
			thr.start();
		}
	}


	public static class BlueConf {



		public static class ConnectThread extends Thread {
			public static BluetoothSocket SOCK;
			public static InputStream IN_STREAM;
			public static OutputStream OUT_STREAM;
			public static int errCount, normalCount;
			/*ERROR COUNT */
			/* ERR*/public static final int ERROR = -1;
			/*NONE*/public static final int NONE = 0;
			public static final int ON_CONNECTING = 2;  /*Attempting to connect*/
			public static final int CONNECTED = 3;  /*Connected*/
			public static int status = NONE;
			public static ConnectedThread connectedThread;
			public static void __init__() {
				setStatus(NONE);
			}


			public static void setStatus(int state) {
				status = state;
			}


			public static void connected() {
				connectedThread = null;
				ConnectedThread.start();
				setStatus(CONNECTED);
			}

			public static void initTTS(byte[] buf) {
				String myString = new String(buf);
				TextToSpeech TTS = new TextToSpeech(null, status -> {
				});
				TTS.setPitch(120);
				TTS.setLanguage(Locale.US);
				TTS.setVoice(TTS.getDefaultVoice());
				TTS.speak(myString, TextToSpeech.QUEUE_FLUSH, null);
				normalCount++;
				normalCount %= 1024;
			}

			public static void __onRun__(EditText destName,BluetoothSocket sock) {
				SOCK=sock;
				try {
					OUT_STREAM = SOCK.getOutputStream();
					IN_STREAM = SOCK.getInputStream();
				} catch (IOException e) {
					System.out.println("(ERROR): No I/O Stream");
				}
				byte[] input_str = "String".getBytes(StandardCharsets.UTF_8);
				byte [] req="Request".getBytes(StandardCharsets.UTF_8);
				try {
					System.out.println( IN_STREAM.read(input_str) );

				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					OUT_STREAM.write(req);
				} catch (IOException e) {
					System.out.println("(ERROR): No I/O Stream");
				}
				byte[] QUIT_SIG = {'_', '_', 'Q', 'U', 'I', 'T', '_', '_'};
				try {
					int length = IN_STREAM.read(input_str);
					if (input_str == QUIT_SIG) {
						return;
					} else {
						/* TODO: TTS */
						initTTS(input_str);
						String mString = input_str.toString();
						CharSequence seq = mString;
						destName.setText(seq);
					}
				} catch (IOException e) {
					errCount++;
					errCount %= 1024;
				}
				if (((double) (errCount / normalCount)) > 0.3) {
					System.err.println("Too much error occurred");
				}
			}
		}
	}

}

