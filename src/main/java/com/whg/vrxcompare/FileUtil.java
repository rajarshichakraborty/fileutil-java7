package com.whg.vrxcompare;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	private static final String IMAGE_FILE_REGEX = "^[\\d]*[_](([a-zA-Z]{1}[\\d]{1,})(\\[[\\d]*\\]){0,1})(\\.(?i)(jpg|png|jpeg|gif|bmp))$";
	private static final String DATE_FORMAT = "MM/dd/yyyy";

	public enum FileType {
		IMAGE, TEXT, ANY
	}

	private String filePath;

	/**
	 * A {@code FileVisitor} that finds all files that match the specified
	 * pattern.
	 */
	public static class Finder extends SimpleFileVisitor<Path> {

		private final PathMatcher matcher;
		private int numMatches = 0;
		private List<FileMeta> filemetalist = new ArrayList<FileMeta>();

		public List<FileMeta> getFilemetalist() {
			return filemetalist;
		}

		Finder(String pattern) {
			matcher = FileSystems.getDefault().getPathMatcher("regex:" + pattern);
		}

		void find(Path file) {
			Path name = file.getFileName();
			if (name != null && matcher.matches(name)) {
				try {
					BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
					FileTime createTime = attributes.creationTime();
					FileTime modTime = attributes.lastModifiedTime();

					/*try {
						Metadata md = ImageMetadataReader.readMetadata(Files
								.newInputStream(file));
						for (Directory directory : md.getDirectories()) {
							for (Tag tag : directory.getTags()) {
								System.out.println(tag);
							}
						}
					} catch (ImageProcessingException e) {
						e.printStackTrace();
					}*/
					FileMeta fileMeta = new FileMeta();
					fileMeta.setCreatedDate(formatDate(createTime.toString()));
					fileMeta.setModifiedDate(formatDate(modTime.toString()));
					fileMeta.setFilePath(file.getParent().toString());
					fileMeta.setFilename(name.toString());
					filemetalist.add(fileMeta);

				} catch (IOException e) {
					e.printStackTrace();
				}

				numMatches++;
			}
		}

		void done() {
			System.out.println("Matched Files: " + numMatches);
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			find(file);
			return CONTINUE;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir,
				BasicFileAttributes attrs) {
			find(dir);
			return CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) {
			System.err.println(exc);
			return CONTINUE;
		}
	}

	public FileUtil(String path) {
		this.filePath = path;
	}

	public List<FileMeta> findFiles(FileType filetype) {
		Path startingDir = Paths.get(this.filePath);
		String pattern;
		switch (filetype) {
			case IMAGE:
				pattern = IMAGE_FILE_REGEX;
				break;
			default:
				pattern = "*";
		}

		Finder finder = new Finder(pattern);
		try {
			Files.walkFileTree(startingDir, finder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		finder.done();
		return finder.getFilemetalist();
	}
	
	public List<String> readFile() {
		try {
			return Files.readAllLines(Paths.get(this.filePath), Charset.defaultCharset());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void writeFile(String content) {
		try {
			Files.write( Paths.get(this.filePath), content.getBytes(), StandardOpenOption.CREATE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String formatDate(String date) {
		try {
			return new SimpleDateFormat(DATE_FORMAT).format(new SimpleDateFormat("yyyy-MM-dd").parse(date.split("T")[0]));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		List<FileMeta> filelistmeta = new FileUtil("C:/Rajarshi/MyLab/VRXDAMCOMPARE/LDrive").findFiles(FileType.IMAGE);
		if(filelistmeta!=null && filelistmeta.size() > 0) {
			for (FileMeta fileMeta : filelistmeta) {
				System.out.println(fileMeta.toString());
			}
		}
	}
}
