import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ithakimodem.Modem;
public class arq {
public void run(String Code) throws IOException, FileNotFoundException{
Modem modem = new Modem();
modem.setSpeed(1000);
modem.setTimeout(8000);
modem.open("ithaki");
int k, rNack = 0;
double ack = 0, nack = 0;
long strTime = 0, stpTime = 0;
String response = "";
ArrayList<Long> rTime = new ArrayList<Long>();
ArrayList<Double> pDis = new ArrayList<Double>();
ArrayList<Byte> digits = new ArrayList<Byte>();
List<Integer> list = new ArrayList<Integer>();
long startTime = System.currentTimeMillis();
while (System.currentTimeMillis() < startTime + 300000) {
String fcs = "";
modem.write(Code.getBytes());
for (;;) {
try {
k = modem.read();
response += (char)k;
if (k == -1) {
System.out.println("Connection Closed");
break;
}
//---------------------------------------------------------------------WELCOME_MSG_RECEIVED / START_OF_PACKETS
if (response.indexOf("\r\n\n\n") != -1) {
strTime = System.currentTimeMillis();
response = "";
}
//-------------------------------------------------------------------------------RECEIVING_<XXXX..XX> / FCS
if (k == 60) { // Decimal of Character "<" (=60)
while(k != 62){ // Decimal of Character ">" (=62)
System.out.print((char)k);
k = modem.read();
digits.add((byte)k);
}
System.out.print((char)k);
k = modem.read();
for(int i=0; i<3; i++) { // RECEIVING_FCS
System.out.print((char)k);
k = modem.read();
fcs += (char)k;
}
ΔΙΚΤΥΑ ΥΠΟΛΟΓΙΣΤΩΝ Ι ΜΑΡΙΟΣ ΣΤΑΥΡΟΥ ΑΕΜ: 9533
}
if (response.indexOf("PSTOP") != -1){
stpTime = System.currentTimeMillis();
System.out.println((char)k);
response = "";
break;
}
System.out.print((char)k);
}catch (Exception x) {break;}
}
int num = 0; // CALCULATING XOR
for (int i=0; i<digits.size()-1; i++)
num ^= digits.get(i);
digits.clear();
if (num == Integer.parseInt(fcs)) { // CHECKING_PACKET
rTime.add(stpTime-strTime);
System.out.println("Positive Acknowledgement || " + fcs + " = " + num);
ack++;
rNack = 0;
Code = "QXXXX\r";
strTime = System.currentTimeMillis();
}
else {
rNack++;
list.add(rNack);
nack++;
System.out.println("Negative Acknowledgement || " + fcs + " != " + num);
Code = "RXXXX\r";
}
}
modem.close();
//---------------------------------------------------------------------------------CALCULATING/ADDING_TO_FILE
double pA = ack / (ack + nack), pN = nack / (ack + nack);
double ber = (float) (1 - Math.pow(pA, 1.0/128.0)); // 128 = 16(bytes) * 8(bits)
for(int n=1; n<=Collections.max(list); n++) // Max_Repeated_Nack_Same_Packet
pDis.add((1 - pN) * Math.pow(pN, n-1));
FileOutputStream arqPacket = new FileOutputStream("Arq Packets.text");
PrintStream prt = new PrintStream(arqPacket);
prt.println("Packets");
for (int i=0; i<rTime.size(); i++)
prt.println("Packet " + (i+1) + " Response Time: " + rTime.get(i) + "ms");
prt.println("\nACK Probability = " + pA + "\tAck = " + (int)ack);
prt.println("NACK Probalitity = " + pN + " \tNack = " + (int)nack);
prt.println("Bit Error Rate = " + ber);
prt.println("\nProbability Distribution (Xmax = " + + Collections.max(list) + ")");
for (int i=0; i<pDis.size(); i++)
prt.println("P(X= " + (i+1) + ") = " + pDis.get(i));
prt.close();
System.out.println("\nFile Created");
}
}