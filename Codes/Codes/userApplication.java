import java.io.IOException;
import java.net.InetAddress;
import java.util.Scanner;
import javax.sound.sampled.LineUnavailableException;
public class userApplication {
public static void main(String[] param) throws IOException, LineUnavailableException {
Scanner scn = new Scanner(System.in);
String packetInfo;
int nP = 999; // Number Of Packets
int serverPort = 38000, clientPort = 48000, mode, run=0;
byte[] hostIP = { (byte)155, (byte)207, (byte)18, (byte)208 };
InetAddress hostAddress = InetAddress.getByAddress(hostIP);
//--------------------------------------SCREEN_MESSAGES / SELECT OPTION---------------------------------
System.out.print("OPTIONS:\n1. Echo Packet\n2. Image\n3. Audio (DPCM)"
+ "\n4. Audio (AQDPCM)\n5. Ithaki Copter (TCP)\n6. Ithaki Copter (UDP)"
+ "\n7. Car Fault Diagnostics (UDP)\n\nENTER NUMBER: ");
int option = scn.nextInt();
//----------------------------------------------OPTION_RUN---------------------------------------------
switch(option) {
case 1: //----------------------------------------------------------------------ECHO_PACKET
packetInfo = "EXXXX";
System.out.print("\n1. Response Time - Throughputs\n2. Temperatures\n\nENTER NUMBER: ");
mode = scn.nextInt();
if(mode == 2) {
packetInfo = packetInfo + "T";
}else {
System.out.print("\n1. With Delay\n2. Without Delay\n\nENTER NUMBER: ");
run = scn.nextInt();
if (run == 2)
packetInfo = "E0000";
}
new echoPacket().run(packetInfo, serverPort, clientPort, hostAddress, mode, run);
break;

case 2: //----------------------------------------------------------------------------IMAGE
packetInfo = "MXXXX";
System.out.print("\nWhich Camera do you want to use?\n1. Camera 1 \n2. Camera 2"
+ "\n\nENTER NUMBER: ");
int cam = scn.nextInt();
if (cam == 2)
packetInfo = packetInfo+"CAM=PTZ";
new image().run(packetInfo, serverPort, clientPort, hostAddress, cam);
break;
case 3: //-----------------------------------------------------------------------AUDIO(DPCM)
packetInfo = "AXXXX";
System.out.print("\n1. Frequencies\n2. Audio\n\nENTER NUMBER: ");
mode = scn.nextInt();
if (mode == 1)
packetInfo = packetInfo + "T" + nP;
else
packetInfo = packetInfo + "F" + nP;
new audioDPCM().run(packetInfo, serverPort, clientPort, hostAddress, nP);
break;
case 4: //----------------------------------------------------------------------AUDIO(AQDPCM)
packetInfo = "AXXXXAQF"+nP;
new audioAQDPCM().run(packetInfo, serverPort, clientPort, hostAddress, nP);
break;
case 5: //----------------------------------------------------------------ITHAKI_COPTER_TCP
int portNumber = 38048;
String request = "AUTO FLIGHTLEVEL=150 LMOTOR=150 RMOTOR=150 PILOT \r\n";
new ithakiCopter_I().run(portNumber, hostAddress, request);
break;
case 6: //----------------------------------------------------------------ITHAKI_COPTER_UDP
packetInfo = "QXXXX";
serverPort = 38078;
clientPort = 48078;
new ithakiCopter_II().run(packetInfo, serverPort, clientPort, hostAddress);
break;
case 7: //--------------------------------------------------------CAR_FAULT_DIAGNOSTICS_UDP
packetInfo = "VXXXX";
new carFaultDiag_II().run(packetInfo, serverPort, clientPort, hostAddress);
break;
}
scn.close();
}
}