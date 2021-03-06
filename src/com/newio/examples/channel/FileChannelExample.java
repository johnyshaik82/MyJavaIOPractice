package com.newio.examples.channel;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;

public class FileChannelExample {
	public static void main(String[] args) {
		fromFileToConsole();

	}

	private static void fromFileToConsole() {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile("resources\\FileChannelExample.txt", "rw");
				FileOutputStream outStream = new FileOutputStream("resources\\FileChannelOut.txt");
				FileChannel fileChannel1 = randomAccessFile.getChannel();
				FileChannel fileChannel2 = outStream.getChannel()) {
			ByteBuffer byteBuffer = ByteBuffer.allocate(512);
			int bytesRead = fileChannel1.read(byteBuffer);
			while (bytesRead > 0) {
				System.out.println("\n\nNumber of Bytes Read : " + bytesRead + " File's current position : "
						+ fileChannel1.position() + "\n");
				byteBuffer.flip();
				int index = 0;
				while (byteBuffer.hasRemaining()) {
					fileChannel2.write(byteBuffer);
					System.out.println("ByteBuffer Iteration : " + index);
				}
				byteBuffer.clear();
				bytesRead = fileChannel1.read(byteBuffer);
			}
			System.out.println("Finished Reading from FileChannelExample.txt");
			printFileContentIO();
			printFileContentNIO();
			appendDataToFile(randomAccessFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Path path = Paths.get("resources\\FileChannelOut.txt");
		System.out.println("Deleted");
		try {
			Files.deleteIfExists(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void printFileContentIO() throws IOException {
		System.out.println("\n IO ::: Printing data from FileChannelOut.txt \n\n");
		try (FileReader fileReader = new FileReader("resources\\FileChannelOut.txt");
				BufferedReader bufferReader = new BufferedReader(fileReader)) {
			String textRead = bufferReader.readLine();
			while (textRead != null) {
				System.out.println(textRead);
				textRead = bufferReader.readLine();
			}
		}
	}

	private static void printFileContentNIO() throws IOException {
		System.out.println("\n NEW IO ::: Printing data from FileChannelOut.txt \n\n");
		Path path = Paths.get("resources\\FileChannelOut.txt");
		try (Stream<String> lines = Files.lines(path).onClose(() -> System.out.println("File Closed"))) {

			Iterator<String> linesItr = lines.iterator();
			while (linesItr.hasNext())
				System.out.println(linesItr.next());
		}
	}

	private static void appendDataToFile(RandomAccessFile randomAccessFile) throws IOException {
		String msg = "\nThis is new text appended. ";
		randomAccessFile.write(msg.getBytes());
	}
}