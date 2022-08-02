import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
public class audioDPCM {
int nack=0, ack=0, bytes, nib1, nib2, dif1, dif2, sample1, sample2=0;
ArrayList<Integer> differences = new ArrayList<Integer>();
ArrayList<Integer> samples = new ArrayList<Integer>();
ArrayList<Byte> audioBuffer = new ArrayList<Byte>();
public void run(String pInfo, int sPort, int cPort, InetAddress hostAdr, int num) throws LineUnavailableException, IOException {
DatagramSocket s = new DatagramSocket();
byte[] txbuffer = pInfo.getBytes();
DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length,hostAdr,sPort);
s.send(p);
DatagramSocket r = new DatagramSocket(cPort);
r.setSoTimeout(3000);
byte[] rxbuffer = new byte[2048];
DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
//--------------------------------------PROCESS---------------------------------------
System.out.println("\nLoading/Decoding bytes of audio...");
for(int i=0; i<num; i++) {
try {
r.receive(q);
ack++;
for (int j=0; j<q.getLength(); j++) {
bytes = rxbuffer[j];
nib1 = (bytes & 0xF0) >> 4;
nib2 = (bytes & 0x0F);
dif1 = nib1-8;
dif2 = nib2-8;
sample1 = sample2 + (dif1*1); // [Quantization Step (b) = 1, MUST BE =>1]
sample2 = sample1 + (dif2*1);
audioBuffer.add((byte)sample1);
audioBuffer.add((byte)sample2);
differences.add(dif1);
differences.add(dif2);
samples.add(sample1);

samples.add(sample2);
}
}catch (Exception x) {
System.out.println(x);
nack++;
}
}
s.close();
r.close();
float pACK =(float)ack / (ack+nack);
System.out.println("...Process Finished!");
System.out.println("Success rate: " + (float)100*pACK + "%");
//---------------------------------CONVERT_TO_BYTE_ARRAY--------------------------
byte[] audio = new byte[audioBuffer.size()];
for(int k=0; k<audioBuffer.size(); k++)
audio[k] = audioBuffer.get(k);
//-----------------------------------PLAYING_AUDIO---------------------------------
System.out.println("Playing audio...");
AudioFormat lnrPCM = new AudioFormat(8000,8,1,true,false);
SourceDataLine lnrOut = AudioSystem.getSourceDataLine(lnrPCM);
lnrOut.open(lnrPCM, 32000);
lnrOut.start();
lnrOut.write(audio,0,audio.length);
lnrOut.stop();
lnrOut.close();
System.out.println("...Player stopped!");
//----------------------------------------ADDING_TO_FILE-----------------------------------------------
FileOutputStream audioFile = new FileOutputStream("Audio (DPCM).wav");
FileOutputStream audioDPCM = new FileOutputStream("Audio (DPCM).text");
PrintStream prt = new PrintStream(audioDPCM);
prt.println("Audio - DPCM");
for (int i=0; i<samples.size(); i++)
prt.println("Sample"+(i+1)+"="+samples.get(i)+"\tDifference"+(i+1)+"="+differences.get(i));
prt.println("\nProbability of Occurrence: "+(float)100*pACK+"%");
prt.close();
ByteArrayInputStream inStream = new ByteArrayInputStream(audio);
AudioInputStream audioInStream = new AudioInputStream(inStream, lnrPCM, audio.length);
AudioSystem.write(audioInStream, AudioFileFormat.Type.WAVE, audioFile);
System.out.println("\nFiles Created");
}
}