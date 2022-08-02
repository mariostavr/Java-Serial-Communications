import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.IOException;
public class userApplication {
public static void main(String[] param) throws IOException , FileNotFoundException {
System.out.print("OPTIONS\n\n1.Echo Packet\n2.Image\n"
+ "3.Error Image\n4.GPS\n5.ARQ \n\nEnter Option: ");
String code;
Scanner scn = new Scanner(System.in);
int option = scn.nextInt();
scn.close();
//--------------------------------------ECHO_PACKET------------------------------------
if (option == 1) {
code = "EXXXX\r";
new echoPacket().run(code);
}
//-----------------------------------------IMAGE---------------------------------------
if (option == 2) {
code = "MXXXXFIX\r";
new image().run(code);
}
//--------------------------------------ERROR_IMAGE------------------------------------
if (option == 3) {
code = "GXXXXFIX\r";
new errorImage().run(code);
}
//------------------------------------------GPS----------------------------------------
if (option == 4) {
code = "PXXXXR=1003090\r";
new gps().run(code);
}
//------------------------------------------ARQ----------------------------------------
if (option == 5) {
code = "QXXXX\r";
new arq().run(code);
}
}
}