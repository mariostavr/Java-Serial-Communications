import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
public class echoPacket {
ArrayList<Long> responseTime = new ArrayList<Long>();
ArrayList<Long> throughputBPS = new ArrayList<Long>();
ArrayList<Long> srttAr = new ArrayList<Long>(), varAr = new ArrayList<Long>();
ArrayList<Long> rtoAr = new ArrayList<Long>();
ArrayList<String> messages = new ArrayList<String>();
int ack=0, nack=0, counter = 10, packets = 0;
long srtt=0, var=0, totalTime = 0;
public void run(String pInfo, int sPort, int cPort, InetAddress hostAdr, int mode, int run) throws IOException {
DatagramSocket s = new DatagramSocket();
byte[] txbuffer = pInfo.getBytes();
DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length,hostAdr,sPort);
DatagramSocket r = new DatagramSocket(cPort);
r.setSoTimeout(4000);
byte[] rxbuffer = new byte[2048];
DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
if (run == 2)
r.setSoTimeout(1000);
if (mode == 1) {
long strTime = System.currentTimeMillis();
while (System.currentTimeMillis() < (strTime+300000)) {
try {
long startTime = System.currentTimeMillis();
s.send(p);
r.receive(q);
ack++;
long response = System.currentTimeMillis() - startTime;
totalTime += response;
packets++;
srtt = (long)((0.9*srtt) + ((1-0.9)*response));//------------------- a = 0.9
var = (long)((0.25*var) + Math.abs(((1-0.25)*(srtt-response))));//-- b = 0.25
long rto = srtt + 4*var;//----------------------------------------------- c = 4
responseTime.add(response);
srttAr.add(srtt);
varAr.add(var);
rtoAr.add(rto);

String message = new String(rxbuffer,0,q.getLength());
System.out.println(message);
if (totalTime >= 8000) {
throughputBPS.add((long)(packets*8*32)/8);
totalTime = 0;
packets = 0;
}
}catch (Exception x) {
System.out.println(x);
nack++;
}
}
}else if(mode == 2){
String code = pInfo + "00";
while (counter < 100) {
txbuffer = code.getBytes();
p = new DatagramPacket(txbuffer,txbuffer.length,hostAdr,sPort);
try {
s.send(p);
r.receive(q);
String message = new String(rxbuffer,0,q.getLength());
System.out.println(message + " " + code);
messages.add(message);
messages.add(code);
ack++;
}catch (Exception x) {
System.out.println("Packet didn't received!");
nack++;
}
code = pInfo + String.valueOf(counter);
counter++;
}
}
r.close();
s.close();
float pACK = (float)ack / (ack+nack);
System.out.println("Success rate: " + (float)100*pACK + "%");
//----------------------------------------ADDING_TO_FILE-----------------------------------------------
FileOutputStream echoPacket = new FileOutputStream("Echo Packets.text");
PrintStream prt = new PrintStream(echoPacket);
for (int i=0; i<responseTime.size(); i++)
prt.println("Packet "+(i+1)+"\t\tResponse Time="+responseTime.get(i)+"\tSRTT="
+srttAr.get(i)+"\tVar="+varAr.get(i)+"\t\tRTO="+rtoAr.get(i));
for (int i=0; i<throughputBPS.size(); i++)
prt.println((i+1) + ". Throughput (BPS)="+throughputBPS.get(i));
for (int i=0; i<messages.size(); i+=2)

prt.println(messages.get(i) + "\tCode: " + messages.get(i+1));
prt.println("\nProbability of Occurrence: "+(float)100*pACK+"%");
prt.close();
System.out.println("\nFiles Created");
}
}