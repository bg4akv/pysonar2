package org.yinwang.pysonar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import sun.net.www.protocol.file.FileURLConnection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * unsorted utility class
 */
public class $ {

	public static String baseFileName(String filename)
	{
		return new File(filename).getName();
	}

	public static String hashFileName(String filename)
	{
		return Integer.toString(filename.hashCode());
	}

	public static boolean same(Object o1, Object o2)
	{
		if (o1 == null) {
			return o2 == null;
		} else {
			return o1.equals(o2);
		}
	}

	public static String getTempFile(String file)
	{
		String tmpDir = getTempDir();
		return makePathString(tmpDir, file);
	}

	public static String getTempDir()
	{
		String systemTemp = getSystemTempDir();
		return makePathString(systemTemp, "pysonar2-" + Analyzer.self.sid);
	}

	public static String getSystemTempDir()
	{
		String tmp = System.getProperty("java.io.tmpdir");
		String sep = System.getProperty("file.separator");
		if (tmp.endsWith(sep)) {
			return tmp;
		}
		return tmp + sep;
	}

	/**
	 * Returns the parent qname of {@code qname} -- everything up to the
	 * last dot (exclusive), or if there are no dots, the empty string.
	 */
	public static String getQnameParent(String qname)
	{
		if (qname == null || qname.isEmpty()) {
			return "";
		}
		int index = qname.lastIndexOf(".");
		if (index == -1) {
			return "";
		}
		return qname.substring(0, index);
	}

	public static String moduleQname(String file)
	{
		File f = new File(file);

		if (f.getName().endsWith("__init__.py")) {
			file = f.getParent();
		} else if (file.endsWith(Globals.FILE_SUFFIX)) {
			file = file.substring(0, file.length() - Globals.FILE_SUFFIX.length());
		}

		// remove Windows like '\\' and 'C:'
		file = file.replaceAll("^\\\\", "");
		file = file.replaceAll("^[a-zA-Z]:", "");

		return file.replace(".", "%20").replace('/', '.').replace('\\', '.');
	}

	/**
	 * Given an absolute {@code path} to a file (not a directory),
	 * returns the module name for the file.  If the file is an __init__.py,
	 * returns the last component of the file's parent directory, else
	 * returns the filename without path or extension.
	 */
	public static String moduleName(String path)
	{
		File f = new File(path);
		String name = f.getName();
		if (name.equals("__init__.py")) {
			return f.getParentFile().getName();
		} else if (name.endsWith(Globals.FILE_SUFFIX)) {
			return name.substring(0, name.length() - Globals.FILE_SUFFIX.length());
		} else {
			return name;
		}
	}

	public static String arrayToString(Collection<String> strings)
	{
		StringBuffer sb = new StringBuffer();
		for (String s : strings) {
			sb.append(s).append("\n");
		}
		return sb.toString();
	}

	public static String arrayToSortedStringSet(Collection<String> strings)
	{
		Set<String> sorter = new TreeSet<>();
		sorter.addAll(strings);
		return arrayToString(sorter);
	}

	public static void writeFile(String path, String contents)
	{
		PrintWriter out = null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(path)));
			out.print(contents);
			out.flush();
		} catch (Exception e) {
			$.die("Failed to write: " + path);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static String readFile(String path)
	{
		// Don't use line-oriented file read -- need to retain CRLF if present
		// so the style-run and link offsets are correct.
		byte[] content = getBytesFromFile(path);
		if (content == null) {
			return null;
		} else {
			return new String(content, Charset.forName("UTF-8"));
		}
	}

	public static byte[] getBytesFromFile(String filename)
	{
		try {
			return FileUtils.readFileToByteArray(new File(filename));
		} catch (Exception e) {
			return null;
		}
	}

	static boolean isReadableFile(String path)
	{
		File f = new File(path);
		return f.canRead() && f.isFile();
	}

	public static String readWhole(InputStream is) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		byte[] bytes = new byte[8192];

		int nRead;
		while ((nRead = is.read(bytes, 0, 8192)) > 0) {
			sb.append(new String(bytes, 0, nRead));
		}
		return sb.toString();
	}

	public static void copyResourcesRecursively(URL originUrl, File destination) throws Exception
	{
		URLConnection urlConnection = originUrl.openConnection();
		if (urlConnection instanceof JarURLConnection) {
			copyJarResourcesRecursively(destination, (JarURLConnection) urlConnection);
		} else if (urlConnection instanceof FileURLConnection) {
			FileUtils.copyDirectory(new File(originUrl.getPath()), destination);
		} else {
			die("Unsupported URL type: " + urlConnection);
		}
	}

	public static void copyJarResourcesRecursively(File destination, JarURLConnection jarConnection)
	{
		JarFile jarFile;
		try {
			jarFile = jarConnection.getJarFile();
		} catch (Exception e) {
			$.die("Failed to get jar file)");
			return;
		}

		Enumeration<JarEntry> em = jarFile.entries();
		while (em.hasMoreElements()) {
			JarEntry entry = em.nextElement();
			if (entry.getName().startsWith(jarConnection.getEntryName())) {
				String fileName = StringUtils.removeStart(entry.getName(), jarConnection.getEntryName());
				if (!fileName.equals("/")) { // exclude the directory
					InputStream entryInputStream = null;
					try {
						entryInputStream = jarFile.getInputStream(entry);
						FileUtils.copyInputStreamToFile(entryInputStream, new File(destination, fileName));
					} catch (Exception e) {
						die("Failed to copy resource: " + fileName, e);
					} finally {
						if (entryInputStream != null) {
							try {
								entryInputStream.close();
							} catch (Exception e) {
							}
						}
					}
				}
			}
		}
	}

	public static String readResource(String resource)
	{
		File file = new File(resource);
		if (!file.exists()) {
			System.out.println("file not exists: " + resource);
			return "";
		}
		System.out.println("file exists: " + resource);
		InputStream s = null;
		try {
			s = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		return readWholeStream(s);
	}

	/**
	 * get unique hash according to file content and filename
	 */

	public static String getFileHash(String path)
	{
		byte[] bytes = getBytesFromFile(path);
		return $.getContentHash(path.getBytes()) + "." + getContentHash(bytes);
	}

	public static String getContentHash(byte[] fileContents)
	{
		MessageDigest algorithm;

		try {
			algorithm = MessageDigest.getInstance("SHA-1");
		} catch (Exception e) {
			$.die("Failed to get SHA, shouldn't happen");
			return "";
		}

		algorithm.reset();
		algorithm.update(fileContents);
		byte messageDigest[] = algorithm.digest();
		StringBuilder sb = new StringBuilder();
		for (byte aMessageDigest : messageDigest) {
			sb.append(String.format("%02x", 0xFF & aMessageDigest));
		}
		return sb.toString();
	}

	static public String escapeQname(String s)
	{
		return s.replaceAll("[.&@%-]", "_");
	}

	public static String escapeWindowsPath(String path)
	{
		return path.replace("\\", "\\\\");
	}

	public static Collection<String> toStringCollection(Collection<Integer> collection)
	{
		List<String> ret = new ArrayList<>();
		for (Integer x : collection) {
			ret.add(x.toString());
		}
		return ret;
	}

	static public String joinWithSep(Collection<String> ls, String sep, String start,
			String end)
	{
		StringBuilder sb = new StringBuilder();
		if (start != null && ls.size() > 1) {
			sb.append(start);
		}
		int i = 0;
		for (String s : ls) {
			if (i > 0) {
				sb.append(sep);
			}
			sb.append(s);
			i++;
		}
		if (end != null && ls.size() > 1) {
			sb.append(end);
		}
		return sb.toString();
	}

	public static void msg(String m)
	{
		if (Analyzer.self != null && !Analyzer.self.hasOption("quiet")) {
			System.out.println(m);
		}
	}

	public static void msg_(String m)
	{
		if (Analyzer.self != null && !Analyzer.self.hasOption("quiet")) {
			System.out.print(m);
		}
	}

	public static void testmsg(String m)
	{
		System.out.println(m);
	}

	public static void die(String msg)
	{
		die(msg, null);
	}

	public static void die(String msg, Exception e)
	{
		System.err.println(msg);

		if (e != null) {
			System.err.println("Exception: " + e + "\n");
		}

		Thread.dumpStack();
		System.exit(2);
	}

	public static String readWholeFile(String filename)
	{
		try {
			return new Scanner(new File(filename)).useDelimiter("PYSONAR2END").next();
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static String readWholeStream(InputStream in)
	{
		return new Scanner(in).useDelimiter("\\Z").next();
	}

	public static String percent(long num, long total)
	{
		if (total == 0) {
			return "100%";
		} else {
			int pct = (int) (num * 100 / total);
			return String.format("%1$3d", pct) + "%";
		}
	}

	public static String formatTime(long millis)
	{
		long sec = millis / 1000;
		long min = sec / 60;
		sec = sec % 60;
		long hr = min / 60;
		min = min % 60;

		return hr + ":" + min + ":" + sec;
	}

	/**
	 * format number with fixed width
	 */
	public static String formatNumber(Object n, int length)
	{
		if (length == 0) {
			length = 1;
		}

		if (n instanceof Integer) {
			return String.format("%1$" + length + "d", (int) n);
		} else if (n instanceof Long) {
			return String.format("%1$" + length + "d", (long) n);
		} else {
			return String.format("%1$" + length + "s", n.toString());
		}
	}

	public static boolean deleteDirectory(String directory)
	{
		return deleteDirectory(new File(directory));
	}

	public static boolean deleteDirectory(File directory)
	{
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File f : files) {
					if (f.isDirectory()) {
						deleteDirectory(f);
					} else {
						f.delete();
					}
				}
			}
		}
		return directory.delete();
	}

	public static String newSessionId()
	{
		return UUID.randomUUID().toString();
	}

	public static File makePath(String... files)
	{
		File ret = new File(files[0]);

		for (int i = 1; i < files.length; i++) {
			ret = new File(ret, files[i]);
		}

		return ret;
	}

	public static String makePathString(String... files)
	{
		return unifyPath(makePath(files).getPath());
	}

	public static String unifyPath(String filename)
	{
		return unifyPath(new File(filename));
	}

	public static String unifyPath(File file)
	{
		try {
			return file.getCanonicalPath();
		} catch (Exception e) {
			die("Failed to get canonical path");
			return "";
		}
	}

	public static String relPath(String path1, String path2)
	{
		String a = unifyPath(path1);
		String b = unifyPath(path2);

		String[] as = a.split("[/\\\\]");
		String[] bs = b.split("[/\\\\]");

		int i;
		for (i = 0; i < Math.min(as.length, bs.length); i++) {
			if (!as[i].equals(bs[i])) {
				break;
			}
		}

		int ups = as.length - i - 1;

		File res = null;

		for (int x = 0; x < ups; x++) {
			res = new File(res, "..");
		}

		for (int y = i; y < bs.length; y++) {
			res = new File(res, bs[y]);
		}

		if (res == null) {
			return null;
		} else {
			return res.getPath();
		}
	}

	public static String projRelPath(String file)
	{
		if (file.startsWith(Analyzer.self.projectDir)) {
			return file.substring(Analyzer.self.projectDir.length() + 1);
		} else {
			return file;
		}
	}

	public static String projAbsPath(String file)
	{
		if (file.startsWith("/") || file.startsWith(Analyzer.self.projectDir)) {
			return file;
		} else {
			return makePathString(Analyzer.self.projectDir, file);
		}
	}

	public static File joinPath(File dir, String file)
	{
		return joinPath(dir.getAbsolutePath(), file);
	}

	public static File joinPath(String dir, String file)
	{
		File file1 = new File(dir);
		File file2 = new File(file1, file);
		return file2;
	}

	public static String banner(String msg)
	{
		return "---------------- " + msg + " ----------------";
	}

	public static String printMem(long bytes)
	{
		double dbytes = bytes;
		DecimalFormat df = new DecimalFormat("#.##");

		if (dbytes < 1024) {
			return df.format(bytes);
		} else if (dbytes < 1024 * 1024) {
			return df.format(dbytes / 1024);
		} else if (dbytes < 1024 * 1024 * 1024) {
			return df.format(dbytes / 1024 / 1024) + "M";
		} else if (dbytes < 1024 * 1024 * 1024 * 1024L) {
			return df.format(dbytes / 1024 / 1024 / 1024) + "G";
		} else {
			return "Too big to show you";
		}
	}

	public static String getGCStats()
	{
		long totalGC = 0;
		long gcTime = 0;

		for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
			long count = gc.getCollectionCount();

			if (count >= 0) {
				totalGC += count;
			}

			long time = gc.getCollectionTime();

			if (time >= 0) {
				gcTime += time;
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(banner("memory stats"));
		sb.append("\n- total collections: " + totalGC);
		sb.append("\n- total collection time: " + formatTime(gcTime));

		Runtime runtime = Runtime.getRuntime();
		sb.append("\n- total memory: " + $.printMem(runtime.totalMemory()));

		return sb.toString();
	}

	public static List<List<Binding>> correlateBindings(List<Binding> bindings)
	{
		Map<Integer, List<Binding>> bdHash = new HashMap<>();
		for (Binding b : bindings) {
			int hash = b.hashCode();
			if (!bdHash.containsKey(hash)) {
				bdHash.put(hash, new ArrayList<>());
			}
			List<Binding> bs = bdHash.get(hash);
			bs.add(b);
		}
		return new ArrayList<>(bdHash.values());
	}

	public static boolean deleteFile(String file)
	{
		return new File(file).delete();
	}

	public static void sleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		} catch (InterruptedException e)
		{
		}
	}

}