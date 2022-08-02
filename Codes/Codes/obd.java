import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;
public class carFaultDiag_II {
int ack=0, nack=0;
public void run(String pInfo, int sPort, int cPort, InetAddress hostAdr) throws IOException {
Scanner scn = new Scanner(System.in);
System.out.print("\nChoose request for OBD-II:\n1. Engine Run Time\n"
+ "2. Intake Air Temperature\n3. Throttle Position \n"
+ "4. Engine RPM\n5. Vehicle Speed\n6. Coolant Temperature\n"
+ "ENTER 'exit' to stop. \n\nENTER REQUEST'S NAME: ");
String request = scn.nextLine();
for(;;) {
float formula=0;
String code = "";
if (request.equals("Engine Run Time"))
code = pInfo + "OBD=01 1F";
else if (request.equals("Intake Air Temperature"))
code = pInfo + "OBD=01 0F";
else if (request.equals("Throttle Position"))
code = pInfo + "OBD=01 11";
else if (request.equals("Engine RPM"))
code = pInfo + "OBD=01 0C";
else if (request.equals("Vehicle Speed"))
code = pInfo + "OBD=01 0D";
else if (request.equals("Coolant Temperature"))
code = pInfo + "OBD=01 05";
else if (request.equals("exit"))
break;
FileOutputStream file = new FileOutputStream("OBD_II "+request+".text");
ArrayList<Float> formulaValue = new ArrayList<Float>();
DatagramSocket s = new DatagramSocket();
byte[] txbuffer = code.getBytes();
DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length,hostAdr,sPort);
DatagramSocket r = new DatagramSocket(cPort);
r.setSoTimeout(3000);

byte[] rxbuffer = new byte[5000];
DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
long startTime = System.currentTimeMillis();
while (System.currentTimeMillis() < startTime+40000) {
try {
s.send(p);
r.receive(q);
ack++;
String message = new String(rxbuffer,0,q.getLength());
System.out.println(message);
if ((request.equals("Engine Run Time")) || (request.equals("Engine RPM"))) {
String XX = "", YY = "";
XX += (char)rxbuffer[6];
XX += (char)rxbuffer[7];
YY += (char)rxbuffer[9];
YY += (char)rxbuffer[10];
if (request.equals("Engine Run Time"))
formula = (float)256*Integer.parseInt(XX,16)+Integer.parseInt(YY,16);
else
formula = (float)(((Integer.parseInt(XX,16)*256)+Integer.parseInt(YY,16))/4);
}else {
String XX = "";
XX += (char)rxbuffer[6];
XX += (char)rxbuffer[7];
if (request.equals("Intake Air Temperature") || (request.equals("Coolant Temperature")))
formula = (float)Integer.parseInt(XX,16) - 40;
else if (request.equals("Throttle Position"))
formula = (float)Integer.parseInt(XX,16)*100/255;
else if (request.equals("Vehicle Speed"))
formula = (float)Integer.parseInt(XX,16);
}
formulaValue.add(formula);
}catch (Exception x) {
System.out.println(x);
nack++;
}
}
r.close();
s.close();
float pACK = (float)ack / (ack+nack);
System.out.println("Success rate: " + (float)100*pACK + "%");
//----------------------------------------ADDING_TO_FILE-----------------------------------------------

PrintStream prt = new PrintStream(file);
prt.println(request);
for (int i=0; i<formulaValue.size(); i++)
prt.println((i+1) + ". " + formulaValue.get(i));
prt.println("\nProbability of Occurrence: "+(float)100*pACK+"%");
prt.close();
System.out.println("\nFile Created");
System.out.print("\nChoose next request for OBD-II:\n1. Engine Run Time\n"
+ "2. Intake Air Temperature\n3. Throttle Position \n"
+ "4. Engine RPM\n5. Vehicle Speed\n6. Coolant temperature \n\nENTER REQUEST'S NAME: ");
request = scn.nextLine();
}
scn.close();
System.out.println("Procces Finished!");
}
}