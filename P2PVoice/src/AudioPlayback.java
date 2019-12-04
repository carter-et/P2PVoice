import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;

public class AudioPlayback {
    private Socket socket;
    private InputStream in;
    private DataLine.Info info;
    private AudioFormat format;
    private SourceDataLine speakers;


    public AudioPlayback(Socket socket) {
        this.socket = socket;
        try {
            this.in = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        format = new AudioFormat(8000.0f, 16, 1, true, true);
        info = new DataLine.Info(SourceDataLine.class, format);

        try {
            speakers = (SourceDataLine) AudioSystem.getLine(info);
            speakers.open(format);
        } catch(LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playAudio() {
        int bRead;
        byte[] data = new byte[speakers.getBufferSize()/5];

        speakers.start();

        while(true) {
            try {
                bRead = in.read(data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            speakers.write(data, 0, data.length);
        }
    }

    private byte[] getAudio() throws Exception {
        //First read a 32 bit integer indicating
        //how many bytes to read for the message.
        byte[] msgLength = new byte[4];
        this.in.read(msgLength, 0, 4);
        int len = ByteBuffer.wrap(msgLength).getInt();

        //Next read len bytes of data and return it
        //as a string.
        byte[] msg = new byte[len];
        this.in.read(msg, 0, len);
        return msg;
    }

    public static void main(String[] args) throws Exception {


    }
}
