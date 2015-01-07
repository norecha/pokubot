package aok.coc.util;

import java.awt.AWTException; //for errors
import java.awt.Color; //to handle colors
import java.awt.Desktop; //to open files & links
import java.awt.Dimension; //variable type that screen size is returned in
import java.awt.MouseInfo; //this lets us retrieve the mouse coordinates
import java.awt.Point;
import java.awt.Rectangle; //to create rectangle used for screenshot
import java.awt.Robot; //to control mouse & keyboard
import java.awt.Toolkit; //used to get screen size
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent; //contains constants for mouse buttons etc
import java.awt.image.BufferedImage; //to take screenshot
import java.io.BufferedReader; //to read from console
import java.io.BufferedWriter;
import java.io.File; //to save image
import java.io.FileReader;
//user generated imports
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader; //to read from console
import java.io.Writer;
import java.net.URI; //to manipulate URLS
import java.text.DateFormat; //formatting the data
import java.text.SimpleDateFormat; //formatting the date
import java.util.Date; //getting the date
import java.util.Random; //to create random numbers

import javax.imageio.ImageIO; //to craft image from coordinates
import javax.swing.JOptionPane; //for message box
import javax.swing.JPasswordField; //for input  messages etc 

import com.sun.jna.platform.win32.WinDef.HWND;
//for key presses

// Important things: http://docs.oracle.com/javase/1.3/docs/api/java/awt/Robot.html

@SuppressWarnings("unused")
public class RobotUtils {

	private static final String	WORKING_DIR			= System.getProperty("user.dir");
	private static final int	SCREEN_WIDTH		= Toolkit.getDefaultToolkit().getScreenSize().width;
	private static final int	SCREEN_HEIGHT		= Toolkit.getDefaultToolkit().getScreenSize().height;
	private static final String	SYSTEM_OS			= System.getProperty("os.name");
	private static final String	USER_NAME			= System.getProperty("user.name");
	private static final String	USER_HOME_DIR		= System.getProperty("user.home");

	private static Robot		r;
	private static Integer		offsetX				= null;
	private static Integer		offsetY				= null;

	public static Random		random				= new Random();

	static {
		try {
			r = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	// user32
	public static final int		WM_COMMAND			= 0x111;
	public static final int		WM_LBUTTONDOWN		= 0x201;
	public static final int		WM_LBUTTONUP		= 0x202;
	public static final int		WM_LBUTTONDBLCLK	= 0x203;
	public static final int		WM_RBUTTONDOWN		= 0x204;
	public static final int		WM_RBUTTONUP		= 0x205;
	public static final int		WM_RBUTTONDBLCLK	= 0x206;
	public static final int		WM_KEYDOWN			= 0x100;
	public static final int		WM_KEYUP			= 0x101;
	public static final int		WM_MOUSEWHEEL		= 0x20A;

	private static HWND			handler				= null;

	public static void setup(Rectangle rect) {
		offsetX = rect.x;
		offsetY = rect.y;
	}

	public static void setupWin32(HWND handler, Rectangle rect) {
		RobotUtils.handler = handler;
		offsetX = rect.x;
		offsetY = rect.y;
	}

	public static void zoomUp(int notch) throws InterruptedException {
		System.out.println("Zooming out...");
		for (int i = 0; i < notch; i++) {
			User32.INSTANCE.SendMessage(handler, WM_KEYDOWN, 0x28, 0X1500001);
			Thread.sleep(350);
		}
		User32.INSTANCE.SendMessage(handler, WM_KEYDOWN, 0X11, 0X11d0001);
	}

	private static void keyType(int... keyCodes) {
		keyType(keyCodes, 0, keyCodes.length);
	}

	private static void keyType(int[] keyCodes, int offset, int length) {
		if (length == 0) {
			return;
		}
		r.keyPress(keyCodes[offset]);
		keyType(keyCodes, offset + 1, length - 1);
		r.keyRelease(keyCodes[offset]);
	}

	private static void cKeyDown(int i) {
		r.keyPress(i);
	}

	private static void cKeyUp(int i) {
		r.keyRelease(i);
	}

	private static void cKeyPress(int i) {
		cKeyDown(i);
		cKeyUp(i);
	}

	private static void sleep(int s) {
		r.delay(s);
	}

	public static void mouseMove(int x, int y) {
		r.mouseMove(x + offsetX, y + offsetY);
	}

	public static void leftClick(Clickable clickable, int sleepInMs) throws InterruptedException {
		leftClickWin32(clickable.getX(), clickable.getY());
		Thread.sleep(sleepInMs + random.nextInt(sleepInMs));
		//		mouseClick("left", clickable.getX() + offsetX, clickable.getY() + offsetY);
	}

	public static void leftClick(int x, int y) {
		leftClickWin32(x, y);
		//		mouseClick("left", x + offsetX, y + offsetY);
	}

	private static void leftClickWin32(int x, int y) {
		int lParam = makeParam(x, y);
		User32.INSTANCE.SendMessage(handler, WM_LBUTTONDOWN, 0x00000001, lParam);
		User32.INSTANCE.SendMessage(handler, WM_LBUTTONUP, 0x00000000, lParam);
	}
	
	private static int makeParam(int low, int high) {
		// to work for negative numbers
		return (high << 16) | ((low << 16) >>> 16);
	}

	public static void sleepTillClickableIsActive(Clickable clickable) throws InterruptedException {
		while (true) {
			if (isClickableActive(clickable)) {
				return;
			}
			Thread.sleep(random.nextInt(250) + 750);
		}
	}

	private static void mouseClick(String button, int x, int y) {
		Point prev = MouseInfo.getPointerInfo().getLocation();
		r.mouseMove(x, y);
		if (button.equals("left")) {
			r.mousePress(InputEvent.BUTTON1_MASK); //press the left mouse button
			r.mouseRelease(InputEvent.BUTTON1_MASK); //release the left mouse button
		} else {
			if (button.equals("right")) {
				r.mousePress(InputEvent.BUTTON3_MASK); //press the right mouse button
				r.mouseRelease(InputEvent.BUTTON3_MASK); //release the right mouse button
			}
		}
		r.mouseMove(prev.x, prev.y);
	}

	private static void mouseClick(String button) {
		int X = MouseInfo.getPointerInfo().getLocation().x; //get the X coordinate of the mouse
		int Y = MouseInfo.getPointerInfo().getLocation().y; //get the Y coordinate of the mouse
		mouseClick(button, X, Y);
	}

	private static void mouseDown(String button, int x, int y) {
		r.mouseMove(x, y);
		if (button.equals("left")) {
			r.mousePress(InputEvent.BUTTON1_MASK); //press the left mouse button
		} else {
			if (button.equals("right")) {
				r.mousePress(InputEvent.BUTTON3_MASK); //press the right mouse button
			}
		}
	}

	private static void mouseDown(String button) {
		int X = MouseInfo.getPointerInfo().getLocation().x; //get the X coordinate of the mouse
		int Y = MouseInfo.getPointerInfo().getLocation().y; //get the Y coordinate of the mouse
		mouseDown(button, X, Y);
	}

	private static void mouseUp(String button) {
		if (button.equals("left")) {
			r.mouseRelease(InputEvent.BUTTON1_MASK); //Release the left mouse button
		} else {
			if (button.equals("right")) {
				r.mouseRelease(InputEvent.BUTTON3_MASK); //Release the right mouse button
			}
		}
	}

	private static void mouseClickDrag(String button, int x1, int y1, int x2, int y2) {
		mouseDown(button, x1, y1);
		sleep(50);
		mouseMove(x2, y2);
		sleep(50);
		mouseUp(button);
	}

	private static void mouseScrollUp() {
		r.mouseWheel(-100);
	}

	private static void mouseScrollDown() {
		r.mouseWheel(100);
	}

	private static void msgBox(String Text, String Title) {
		JOptionPane.showMessageDialog(null, Text, Title, JOptionPane.PLAIN_MESSAGE); //Show message box
	}

	public static void msgBox(String Text) {
		msgBox(Text, "");
	}

	private static void print(String Text) {
		System.out.println(Text);
	}

	private static void print(int Text) {
		System.out.println(Text);
	}

	private static void print(double Text) {
		System.out.println(Text);
	}

	private static void print(char Text) {
		System.out.println(Text);
	}
	
	public static int[] parseLoot() {
		BufferedImage image = RobotUtils.screenShot(20, 98, 141, 270);
		// String fileName = "post_zort_" + System.currentTimeMillis();
		// File save_path = new File(fileName + "_" + 0 + ".png");
		// ImageIO.write(image, "png", save_path);

		int gold = DigitParser.parseGold(image);
		int elixir = DigitParser.parseElixir(image);
		int de = DigitParser.parseDarkElixir(image);
		System.out.printf("[gold: %d, elixir: %d, de: %d]\n",
			gold, elixir, de);
		
		return new int[]{gold, elixir, de};
	}

	public static BufferedImage screenShot(int x1, int y1, int x2, int y2) {
		return r.createScreenCapture(new Rectangle(x1 + offsetX, y1 + offsetY, x2 - x1, y2 - y1));
	}

	public static boolean screenShot(String fileName, int x1, int y1, int x2, int y2) {
		try {
			if (!(fileName.toLowerCase().endsWith(".png"))) {
				fileName = fileName + ".png";
			}
			BufferedImage img = r.createScreenCapture(new Rectangle(x1 + offsetX, y1 + offsetY, x2 - x1, y2 - y1));
			File save_path = new File(fileName);
			ImageIO.write(img, "png", save_path);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private static boolean screenShot(String fileName) {
		try {
			Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			screenShot(fileName, 0, 0, screen.width, screen.height);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private static boolean open(String fileName) {
		if (fileName.toLowerCase().startsWith("http://") || fileName.toLowerCase().startsWith("https://")) {
			try {
				Desktop d = Desktop.getDesktop();
				URI path = new URI(fileName);
				d.browse(path);
				return true;
			} catch (Throwable t) {
				return false;
			}
		} else {
			try {
				Desktop d = Desktop.getDesktop();
				File file = new File(fileName);
				d.open(file);
				return true;
			} catch (Throwable t) {
				return false;
			}
		}
	}

	private static String dateTimeGet() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

	private static String exec(String cmd) {
		//for windows cmd = "cmd /c command"
		//for linux cmd = "command"
		try {
			String result = "";
			String line;
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader bri = new BufferedReader
									(new InputStreamReader(p.getInputStream()));
			BufferedReader bre = new BufferedReader
									(new InputStreamReader(p.getErrorStream()));
			while ((line = bri.readLine()) != null) {
				result = result + line + "\n";
			}
			bri.close();
			while ((line = bre.readLine()) != null) {
				result = result + line + "\n";
			}
			bre.close();
			p.waitFor();
			return result;
		} catch (Exception err) {
			return null;
		}

	}

	private static String inputBox(String Text, String Title) {
		String result = JOptionPane.showInputDialog(null, Text, Title, 1);
		return result;
	}

	private static String inputBox(String Text) {
		String result = JOptionPane.showInputDialog(null, Text, "", 1);
		return result;
	}

	private static String inputList(String Text, String Title, String[] Choices, String Default) {
		String choice = (String) JOptionPane.showInputDialog(null, Text, Title, JOptionPane.QUESTION_MESSAGE, null, Choices, Default);
		return choice;
	}

	private static String inputList(String Text, String Title, String[] Choices) {
		String choice = inputList(Text, Title, Choices, Choices[0]);
		return choice;
	}

	private static String inputPassword(String Text, String Title, String Okay) {
		JPasswordField passwordField = new JPasswordField();
		Object[] obj = { Text + "\n\n", passwordField };
		Object[] stringArray = { Okay };
		if (JOptionPane.showOptionDialog(null, obj, Title,
			JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, stringArray, obj) == JOptionPane.YES_OPTION) {
			return new String(passwordField.getPassword());
		} else {
			return null;
		}
	}

	private static String inputPassword(String Text, String Title) {
		return inputPassword(Text, Title, "Submit");
	}

	private static String inputPassword(String Text) {
		return inputPassword(Text, "Password Input");
	}

	private static String toStr(Object x) {
		try {
			return String.valueOf(x);
		} catch (Exception e) {
			return null;
		}
	}

	private static String toStr(char[] x) {
		try {
			String result = "";
			for (Character i : x) {
				result += i.toString();
			}
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	private static int toInt(String x) {
		try {
			double y = Double.parseDouble(x); //convert to double in case of decimals
			int z = (int) y; //convert to int
			return z; //return 
		} catch (Exception e) {
			return 0;
		}
	}

	private static int toInt(double x) {
		try {
			int y = (int) x; //convert to int
			return y; //return
		} catch (Exception e) {
			return 0;
		}
	}

	private static int toInt(char x) {
		try {
			int y = x - 48; //convert to int
			return y; //return
		} catch (Exception e) {
			return 0;
		}
	}

	private static double toDouble(int x) {
		double newDub = x * 1.0;
		return newDub;
	}

	private static double toDouble(String x) {
		double y = Double.parseDouble(x); //convert to double in case of decimals
		return y;
	}

	private static char toChar(String x) {
		try {
			return x.charAt(0);
		} catch (Exception e) {
			return '0';
		}
	}

	private static int intGetRandom(int min, int max) {
		Random rand = new Random();
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	private static int[] cursorGetPos() {
		int X = MouseInfo.getPointerInfo().getLocation().x; //get the X coordinate of the mouse
		int Y = MouseInfo.getPointerInfo().getLocation().y; //get the Y coordinate of the mouse
		int[] coords = { X, Y };
		return coords;
	}

	public static Color pixelGetColor(int x, int y) {
		Color pixel = r.getPixelColor(x + offsetX, y + offsetY);
		return pixel;
	}

	public static boolean isClickableActive(Clickable clickable) {
		if (clickable.getColor() == null) {
			throw new IllegalArgumentException(clickable.name());
		}

		return pixelGetColor(clickable.getX(), clickable.getY()).equals(clickable.getColor());
	}

	private static int[] pixelSearch(Color c, int x1, int y1, int x2, int y2, int speed) {
		try {
			//create robot to capture the screen
			BufferedImage screen = r.createScreenCapture(new Rectangle(x1, y1, x2 + 1, y2 + 1)); //read the screen into a buffered image (+1's to avoid index out of range)

			int cVal = c.getRGB(); //get value of color to compare to pixels

			//speed 1 - xMod = 1 yMod = 1
			//10201 - 100%
			//speed 2 - xMod = 2 yMod = 1
			//5050 - 50%
			//speed 3 - xMod = 2 yMod = 2
			//2500 - 25%
			//speed 4 - xMod = 3 yMod = 2
			//1650 - 16%
			//speed 5 - xMod = 3 yMod = 3
			//1089 - 10%

			int xMod;
			int yMod;
			switch (speed) {
			case 1:
				xMod = 1;
				yMod = 1;
				break;
			case 2:
				xMod = 2;
				yMod = 1;
				break;
			case 3:
				xMod = 2;
				yMod = 2;
				break;
			case 4:
				xMod = 3;
				yMod = 2;
				break;
			case 5:
				xMod = 3;
				yMod = 3;
				break;
			default:
				xMod = 1;
				yMod = 1;
				break;
			}

			int[] preXArray = new int[x2 - x1 + 1]; //create an array to hold all X values in image
			int iterator = 0;
			while (iterator <= x2) {
				preXArray[iterator] = x1 + iterator;
				iterator++;
			}
			int[] preYArray = new int[y2 - y1 + 1]; //create an array to hold all Y values in image
			iterator = 0;
			while (iterator <= y2) {
				preYArray[iterator] = y1 + iterator;
				iterator++;
			}

			int[] xArray = new int[(preXArray.length / xMod)];
			int step = 0;
			for (int i = 0; i < preXArray.length; i += xMod) {
				try {
					xArray[step] = preXArray[i];
				} catch (Exception e) {
				}
				step++;
			}

			int[] yArray = new int[(preYArray.length / yMod)];
			step = 0;
			for (int i = 0; i < preYArray.length; i += yMod) {
				try {
					yArray[step] = preYArray[i];
				} catch (Exception e) {
				}
				step++;
			}

			for (int yVal : yArray) {
				for (int xVal : xArray) {
					int color = screen.getRGB(xVal, yVal); //get the color of pixel at coords (xVal, yVal)	
					if (color == cVal) { //if we find the color
						int[] cPos = { xVal, yVal };
						return cPos;
					}
				}
			}

			int[] returnVal = { -1, -1 };
			return returnVal;
		} catch (Exception e) {
			int[] returnVal = { -3, -3 };
			return returnVal;
		}
	}

	private static int[] pixelSearch(Color c, int x1, int y1, int x2, int y2) {
		return pixelSearch(c, x1, y1, x2, y2, 1);
	}

	private static int[] pixelSearch(Color c) {
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		return pixelSearch(c, 0, 0, screen.width, screen.height);
	}

	private static void clipboardPut(String s) {
		StringSelection stringSelection = new StringSelection(s);
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		clpbrd.setContents(stringSelection, null);
	}

	private static String fileRead(String fPath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fPath));
			StringBuilder data = new StringBuilder();

			String line = br.readLine();
			while (line != null) {
				data.append(line);
				data.append('\n');
				line = br.readLine();
			}

			String fileData = data.toString();
			br.close();
			return fileData;
		} catch (Exception e) {
			return "null";
		}
	}

	private static boolean fileWrite(String fPath, String data) {
		BufferedWriter bufferedWriter = null;
		try {
			File myFile = new File(fPath);
			if (!myFile.exists()) {
				myFile.createNewFile();
			}
			Writer writer = new FileWriter(myFile);
			bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(data);
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.close();
				}
			} catch (Exception ex) {
			}
		}
	}

	private static String[] fileList(String dir) {
		try {
			File[] fileList = new File(dir).listFiles(); //java code to get a list of files
			int fileNum = fileList.length; //store length of file list
			String[] files = new String[fileNum];

			for (int i = 0; i < fileNum; i++) {
				files[i] = fileList[i].toString();
			}
			return files;
		} catch (Exception e) {
			String[] files = { "null" };
			return files;
		}
	}

	private static String clipboardGet() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		String result;
		try {
			result = (String) clipboard.getData(DataFlavor.stringFlavor);
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}