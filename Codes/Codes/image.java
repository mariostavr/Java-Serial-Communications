import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import javax.imageio.ImageIO;
public class image {
ArrayList<Byte> img = new ArrayList<Byte>();
int nack = 0, ack = 0;
public void run(String pInfo, int sPort, int cPort, InetAddress hostAdr, int cam) throws IOException {
DatagramSocket s = new DatagramSocket();
byte[] txbuffer = pInfo.getBytes();
DatagramPacket p = new DatagramPacket(txbuffer,txbuffer.length,hostAdr,sPort);
s.send(p);
DatagramSocket r = new DatagramSocket(cPort);
r.setSoTimeout(5000);
byte[] rxbuffer = new byte[2048];
DatagramPacket q = new DatagramPacket(rxbuffer,rxbuffer.length);
System.out.println("\nLoading bytes of image...");
for(;;) {
try {
r.receive(q);
ack++;
for(int i=0; i<q.getLength(); i++)
img.add(rxbuffer[i]);
if (q.getLength() != 128)
break;
}catch (Exception x) {
System.out.println(x);
nack++;
}
}
r.close();
s.close();
float pACK = (float)ack / (ack+nack);
System.out.println("Success rate: " + (float)100*pACK + "%");
//----------------------------------------------IMAGE_CONVERSION-----------------------------------------------
byte[] image = new byte[img.size()];
for (int i=0; i<img.size(); i++)
image[i] = img.get(i);
BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(image));
if (cam == 1)

ImageIO.write(bufferedImage,"jpeg",new File("C:\\Users\\Marios\\Java\\Networks_II\\Image_1.jpg"));
else
ImageIO.write(bufferedImage,"jpeg",new File("C:\\Users\\Marios\\Java\\Networks_II\\Image_2.jpg"));
System.out.println("\nImage Created");
}
}