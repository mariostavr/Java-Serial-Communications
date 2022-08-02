import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import ithakimodem.Modem;
public class gps {
public void run(String Code) throws IOException, FileNotFoundException{
Modem modem = new Modem();
modem.setSpeed(8000);
modem.setTimeout(8000);
modem.open("ithaki");
int k, j = 0;
boolean start = false;
String response = "";
String[] gpsPacket = new String[90], time = new String[5];
String[] latitude = new String[5], latiT = new String[5];
String[] longitude = new String[5], longiT = new String[5];
String[] info = new String[20], newCode = new String[5];
ArrayList<Byte> gpsImage = new ArrayList<Byte>();
modem.write((Code).getBytes());
for (;;) {
try {
k = modem.read();
response += (char)k;
if (k == -1) {
System.out.println("Connection Closed");
break;
}
if (response.indexOf("START ITHAKI GPS TRACKING\r\n") != -1) { // RECEIVING_PACKETS
for (int i=0; i<gpsPacket.length; i++) {
response = "";
while (k != 13) { // EndOfGpsPacket (13 = \r)
k = modem.read();
System.out.print((char)k);
response += (char)k;
}
gpsPacket[i] = response;
k = modem.read();
}
start = true;
}
if (start) { // CREATING_NEWCODE
for (int i=0; i<gpsPacket.length; i+=11) {
info = gpsPacket[i].split("[.,]");
if (j<5) {
time[j] = info[1];
latitude[j] = info[3];
latiT[j] = Integer.toString((int)Math.round((Double.parseDouble(info[4])*0.006)));
longitude[j] = Integer.toString(Integer.parseInt(info[6]));
longiT[j] = Integer.toString((int)Math.round((Double.parseDouble(info[7])*0.006)));
newCode[j] = (longitude[j] + longiT[j] + latitude[j] + latiT[j]);
j++;
ΔΙΚΤΥΑ ΥΠΟΛΟΓΙΣΤΩΝ Ι ΜΑΡΙΟΣ ΣΤΑΥΡΟΥ ΑΕΜ: 9533
}
}
Code = ("PXXXXT="+newCode[0]+"T="+newCode[1]+"T="+newCode[2]+"T="+newCode[3]+"T="+newCode[4]+"\r");
modem.write(Code.getBytes());
start = false;
}
if (response.indexOf("STOP ITHAKI GPS TRACKING\r\n") != -1) { // LOADING_BYTES_OF_IMAGE
System.out.println("\n\nLoading Bytes of Image...");
while (response.indexOf("255217") == -1) { // EndDelimiterOfImage (255, 217 = ffd8, ffd9)
k = modem.read();
response += k;
gpsImage.add((byte)k);
}
break;
}
System.out.print((char)k);
}catch (Exception x) {break;}
}
modem.close();
//-----------------------------------------------------------------------------------ADDING_TO_FILE
FileOutputStream gpsPackets = new FileOutputStream("Gps Packets.text");
PrintStream prt = new PrintStream(gpsPackets);
for (int i=0; i<time.length; i++)
prt.println("Trace " + (i+1) + " time: " + time[i]);
prt.close();
System.out.println("\nFile Created");
//------------------------------------------------------------------------------------IMAGE_CONVERSION
byte[] gpsImg = new byte[gpsImage.size()];
for (int i=0; i<gpsImage.size(); i++)
gpsImg[i] = gpsImage.get(i);
BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(gpsImg));
ImageIO.write(bufferedImage,"jpg",new File("C:\\Users\\Marios\\Java\\DIKTIA\\Images\\Gps Image_M1.jpg"));
System.out.println("\nImage Created");
}
}