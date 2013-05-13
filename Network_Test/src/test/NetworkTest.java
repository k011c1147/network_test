package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import javazoom.spi.mpeg.sampled.file.tag.IcyInputStream;
import javazoom.spi.mpeg.sampled.file.tag.IcyTag;
import javazoom.spi.mpeg.sampled.file.tag.MP3Tag;

public class NetworkTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		new NetworkTest();
	}
	
	public NetworkTest(){
		Socket socket = new Socket();
		boolean isRun = true;
		try {
			socket.connect(new InetSocketAddress("qb.dyndns.tv", 8000));
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bw.write("GET " + "/" + " HTTP/1.0\r\n");
			bw.write("Accept: */*\r\n");
			bw.write("Icy-Metadata:1\r\n");
			bw.write("\r\n");
			bw.flush();
			
			IcyInputStream iis = new IcyInputStream(new BufferedInputStream(socket.getInputStream()));
			
			Socket server = new Socket();
			server.connect(new InetSocketAddress("192.168.1.100", 8000+1));
			
			BufferedWriter bw_srv = new BufferedWriter(new OutputStreamWriter(server.getOutputStream()));
			bw_srv.write("shinchanwigulu\r\n");
			bw_srv.flush();
			
			BufferedReader br_srv = new BufferedReader(new InputStreamReader(server.getInputStream()));
			while(true){
				String buff = br_srv.readLine();
				if(buff == null){
					iis.close();
					return;
				}
				if(buff.indexOf("OK") != -1){
					break;
				}
			}
			
			bw_srv.write("icy-name:Test\r\n");
			bw_srv.write("icy-genre:Test\r\n");
			bw_srv.write("icy-");
			bw_srv.write("icy-br:128\r\n");
			bw_srv.write("\r\n");
			bw_srv.flush();
			
			BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
			/*
			//byte[] buff = new byte[1024];
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String buff;
			while(isRun && (buff = br.readLine()) != null){
				System.out.println(buff);
			}
			
			br.close();
			*/
			
			byte[] buff = new byte[1024*100];
			int readByte = -1;
			String streamTitle = null;
			int i=0;
			while(isRun && (readByte = iis.read(buff, 0, buff.length)) != -1){
				i++;
				System.out.println(Integer.toString(i));
				if(i >= 100)
					isRun = false;
				try{
					IcyTag tag = (IcyTag)iis.getTag("StreamTitle");
					String titleBuff = new String(tag.getValueAsString().getBytes("ISO-8859-1"), "sjis");
					if(!titleBuff.equals(streamTitle)){
						streamTitle = titleBuff;
						System.out.println(streamTitle);
						Socket srv = new Socket();
						srv.connect(new InetSocketAddress("192.168.1.100", 8000));
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(srv.getOutputStream()));
						writer.write("GET /admin.cgi?pass=shinchanwigulu&mode=updinfo&song=" + streamTitle);
						writer.flush();
						try{
							writer.close();
							srv.close();}
						catch(IOException eio){
							
						}
					}
					MP3Tag mp3tags[] = iis.getTags();
					IcyTag tags[];
					tags = Arrays.asList(mp3tags).toArray(new IcyTag[mp3tags.length]);
					if(((String)iis.getTag("content-type").getValue()).equals("audio/aacp"))
						break;
				}catch(NullPointerException en){
					
				}finally{
					try{
						bos.write(buff);
						bos.flush();
					}catch(IOException eio2){
						
					}
				}
			}
			bos.close();
			bw_srv.close();
			server.close();
			iis.close();
			bw.close();
			socket.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}finally{
		}
		
	}

}
