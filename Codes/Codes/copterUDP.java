import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
public class ithakiCopter_II {
ArrayList<String> messages= new ArrayList<String>();
int ack=0, nack=0;
long strTime = System.currentTimeMillis();
public void run(String pInfo, int sPort, int cPort, InetAddress hostAdr) throws IOException {
DatagramSocket s = new DatagramSocket();
byte[] txbuffer = pInfo.getBytes();
DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length,hostAdr,sPort);
DatagramSocket r = new DatagramSocket(cPort);
r.setSoTimeout(3000);
byte[] rxbuffer = new byte[2048];
DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
while (System.currentTimeMillis() < (strTime+65000)) {
try {
s.send(p);
r.receive(q);
String message = new String(rxbuffer,0,q.getLength());
System.out.println(message);
messages.add(message);
ack++;
}catch (Exception x) {
System.out.println(x);
nack++;
}
}
System.out.println("Procces Finished!");
float pACK = (float)ack / (ack+nack);
s.close();
r.close();
//----------------------------------------ADDING_TO_FILE-----------------------------------------------
FileOutputStream ithakiCopterFile = new FileOutputStream("Ithaki Copter (UDP).text");
PrintStream prt = new PrintStream(ithakiCopterFile);
prt.println("Ithaki Copter");
for (int i=0; i<messages.size(); i++)
prt.println(messages.get(i));
prt.println("\nProbability of Occurrence: "+(float)100*pACK+"%");
prt.close();
System.out.println("\nFiles Created");
}
}