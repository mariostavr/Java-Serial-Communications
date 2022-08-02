import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import ithakimodem.Modem;
public class echoPacket {
public void run(String Code) throws IOException, FileNotFoundException {
Modem modem = new Modem();
modem.setSpeed(1000);
modem.setTimeout(8000);
modem.open("ithaki");
int k;
long strTime = 0;
String response = "";
ArrayList<Long> rTime = new ArrayList<Long>();
long startTime = System.currentTimeMillis();
while (System.currentTimeMillis() < startTime + 300000) {
modem.write(Code.getBytes());
for (;;) {
try {
k = modem.read();
response += (char)k;
if (k == -1) {
System.out.println("Connection Closed");
break;
}
if (response.indexOf("\r\n\n\n") != -1) { // WELCOME_MSG_RECEIVED / START_OF_PACKETS
strTime = System.currentTimeMillis();
response = "";
}
if (response.indexOf("PSTOP") != -1) {
rTime.add(System.currentTimeMillis() - strTime);
System.out.print((char)k);
System.out.println("|| Packet Arrived");
response = "";
strTime = System.currentTimeMillis();
break;
}
System.out.print((char)k);
}catch (Exception x) {break;}
}
}
modem.close();
//-------------------------------------------------------------------------------------ADDING_TO_FILE
FileOutputStream echoPacket = new FileOutputStream("Echo Packets.text");
PrintStream prt = new PrintStream(echoPacket);
prt.println("Echo Packets");
for (int i=0; i<rTime.size(); i++)
prt.println("Packet " + (i+1) + " Response Time: " + rTime.get(i) + "ms");
prt.close();
System.out.println("\n\nFile Created");
}
}