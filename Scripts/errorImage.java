import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import ithakimodem.Modem;
public class errorImage {
public void run(String Code) throws IOException, FileNotFoundException{
Modem modem = new Modem();
modem.setSpeed(8000);
modem.setTimeout(8000);
modem.open("ithaki");
int k;
String response = "";
ArrayList<Byte> eImage = new ArrayList<Byte>();
modem.write((Code).getBytes());
for (;;) {
try {
k = modem.read();
response += (char)k;
if (k == -1) {
System.out.println("Connection Closed");
break;
}
if (response.indexOf("\r\n\n\n") !=- 1) { // WELCOME_MSG_RECEIVED / LOADING_BYTES_OF_IMAGE
System.out.println("Loading Bytes of Image...");
while (response.indexOf("255217") == -1) { // EndDelimiterOfImage (255, 217 = ffd8, ffd9)
k = modem.read();
response += k;
eImage.add((byte)k);
}
break;
}
System.out.print((char)k);
}catch (Exception x) {break;}
}
modem.close();
//-------------------------------------------------------------------------------------IMAGE_CONVERSION
byte[] eImg = new byte[eImage.size()];
for (int i=0; i<eImage.size(); i++)
eImg[i] = eImage.get(i);
BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(eImg));
ImageIO.write(bufferedImage,"jpg",new File("C:\\Users\\Marios\\Java\\DIKTIA\\Images\\Error Image_E2.jpg"));
System.out.println("\nImage Created");
}
}