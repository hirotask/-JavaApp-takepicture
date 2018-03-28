package jp.erudosan.eCam;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.github.sarxos.webcam.Webcam;

public class Main {

	Date time = new Date();

	Webcam cam = null;
	boolean result = false;

	AudioInputStream ais = null;

	public static Main instance = new Main();

	public void takePicture() throws IOException, InterruptedException {
		cam = Webcam.getDefault();

		if (cam != null) {
			System.out.println("WebCam: " + cam.getName());
			System.out.println("FPS: " + cam.getFPS());
			cam.open();

			BufferedImage img = cam.getImage();
			try {
				File newdir =  new File("pictures");
				File newdir2 = new File("audio");
				newdir.mkdir();
				newdir2.mkdir();

				writeText();

				result = ImageIO.write(img, "png", new File("./pictures/webcam-picture-" + time.getTime() + ".png"));

				ais = AudioSystem.getAudioInputStream(new File("./audio/audio.wav"));
				AudioFormat format = ais.getFormat();
				DataLine.Info info = new DataLine.Info(Clip.class, format);
				Clip clip = (Clip) AudioSystem.getLine(info);
				clip.open(ais);
				clip.loop(0);
				clip.flush();
				while(clip.isActive()) {
					Thread.sleep(100);
				}

			} catch (NullPointerException e) {
				e.printStackTrace();
				result = false;
				System.out.println("ERROR: the file didn't export");
			}catch(UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
				e.printStackTrace();
			}finally {
				try {
					ais.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Camera was not found!");
		}
	}


	public void writeText() throws IOException{
		File file = new File("./audio/setting.txt");
		FileWriter write = new FileWriter(file);

		write.write("==========Settings==========\r\n"
				+"・audioフォルダに「audio.wav」という名前で音楽データを保存すればその音を写真撮影時に、再生することができます。\r\n");

		write.close();
	}
}
