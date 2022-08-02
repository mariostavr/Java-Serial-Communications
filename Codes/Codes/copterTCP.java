import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
public class ithakiCopter_I {
ArrayList<String> messages= new ArrayList<String>();
long startTime = System.currentTimeMillis();
public void run(int portNum, InetAddress hostAdr, String request) throws IOException {
Socket s = new Socket(hostAdr, portNum);
PrintStream out = new PrintStream(s.getOutputStream());
BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
out.println(request);
while(System.currentTimeMillis() < startTime+60100) {
in.skip(427);//--------------------------length of the message
try {
String line = in.readLine();
while (line != null) {
System.out.println(line);
messages.add(line);
line = in.readLine();
}
run(portNum, hostAdr, "AUTO FLIGHTLEVEL=000 LMOTOR=000 RMOTOR=000 PILOT \r\n");
}catch(Exception x) {
System.out.println(x);
}
}
s.close();
out.close();
in.close();
//----------------------------------------ADDING_TO_FILE-----------------------------------------------
FileOutputStream ithakiCopterFile = new FileOutputStream("Ithaki Copter.text");
PrintStream prt = new PrintStream(ithakiCopterFile);
prt.println("Ithaki Copter");
for (int i=0; i<messages.size(); i++)
prt.println(messages.get(i));
prt.close();
}
}