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
public class audioAQDPCM {
FileOutputStream audioFile, audioAQDPCM;
int nack=0, ack=0, m, b, bytes, nib1, nib2, dif1, dif2, sample1, sample2;
ArrayList<Integer> ms = new ArrayList<Integer>();
ArrayList<Integer> betas = new ArrayList<Integer>();
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
m = (rxbuffer[1] << 8 | (rxbuffer[0] & 0xFF));
b = (rxbuffer[3] << 8 | (rxbuffer[2] & 0xFF));
ms.add(m);
betas.add(b);
for (int j=4; j<q.getLength(); j++) {
bytes = rxbuffer[j];
nib1 = (bytes & 0xF0) >> 4;
nib2 = (bytes & 0x0F);
dif1 = nib1-8;
dif2 = nib2-8;

sample1 = (dif1*b) + m; // [Quantization Step (b)]
sample2 = (dif2*b) + m;
audioBuffer.add((byte)(sample1 & 0xFF));
audioBuffer.add((byte)(sample1 >> 8));
audioBuffer.add((byte)(sample2 & 0xFF));
audioBuffer.add((byte)(sample2 >> 8));
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
float pACK = (float)ack / (ack+nack);
System.out.println("...Process Finished!");
System.out.println("Success rate: " + (float)100*pACK + "%");
//---------------------------------CONVERT_TO_BYTE_ARRAY--------------------------
byte[] audio = new byte[audioBuffer.size()];
for(int k=0; k<audioBuffer.size(); k++)
audio[k] = audioBuffer.get(k);
//-----------------------------------PLAYING_AUDIO---------------------------------
System.out.println("Playing audio...");
AudioFormat lnrPCM = new AudioFormat(8000,16,1,true,false);
SourceDataLine lnrOut = AudioSystem.getSourceDataLine(lnrPCM);
lnrOut.open(lnrPCM, audio.length);
lnrOut.start();
lnrOut.write(audio,0,audio.length);
lnrOut.stop();
lnrOut.close();
System.out.println("...Player stopped!");
//----------------------------------------ADDING_TO_FILE-----------------------------------------------
audioFile = new FileOutputStream("Audio (AQDPCM).wav");
audioAQDPCM = new FileOutputStream("Audio (AQDPCM).text");
PrintStream prt = new PrintStream(audioAQDPCM);
prt.println("Audio - AQDPCM");
for (int i=0; i<samples.size(); i++)
prt.println("Sample"+(i+1) +"="+samples.get(i)+"\tDifference"+(i+1)+"="+differences.get(i));
for (int i=0; i<ms.size(); i++)
prt.println("\tMean"+(i+1) +"="+ms.get(i)+"\tBeta"+(i+1)+"="+betas.get(i));
prt.println("\nProbability of Occurrence: "+(float)100*pACK+"%");
prt.close();
ByteArrayInputStream inStream = new ByteArrayInputStream(audio);

AudioInputStream audioInStream = new AudioInputStream(inStream, lnrPCM, audio.length);
AudioSystem.write(audioInStream, AudioFileFormat.Type.WAVE, audioFile);
System.out.println("\nFiles Created!");
}
}