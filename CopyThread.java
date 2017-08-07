import java.util.*;
import java.io.*;
import java.net.*;

class CopyThread implements Runnable {

  InputStream   in;
  Thread        thread;
  OutputStream  out;
  byte[]        buffer = new byte[1000];

  CopyThread(InputStream in, OutputStream out) {
    this.in  = in;
    this.out = out;
    thread = new Thread(this,"copy");
  }

  static void of(InputStream in, OutputStream out) {
      new CopyThread(in,out).start();
  }

  void start() {
      thread.start();
  }

  public void run() {
    try {
        copy();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
 }

 void copy() throws IOException {
     while (true) {
         int realSize = in.read(buffer);
         if (realSize == -1) {
             out.close();
             break;
         }
         copyBytes(buffer, realSize);
     }
     in.close();
     out.close();
 }

 void copyBytes(byte bytes[], int realSize) throws IOException {
    out.write(bytes,0,realSize);
 }

}
