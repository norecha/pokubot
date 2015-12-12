package aok.coc.util;

import aok.coc.exception.BotBadBaseException;
import aok.coc.exception.BotException;
import org.junit.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class TestImageParser {
	
	static {
		try (InputStream inputStream = TestImageParser.class.getResourceAsStream("/logging.properties")) {
			LogManager.getLogManager().readConfiguration(inputStream);
		} catch (final IOException e) {
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}

	private final String[]	imageNames	= new String[]{
			"loot_1435779233477.png",
			"loot_1435779259826.png",
	};

	private final int[] expectedGold = new int[] {
			86_756, 25_254
	};
	
	private final int[] expectedElixir = new int[] {
			61_349, 31_071
	};
	
	private final int[] expectedDarkElixir = new int[] {
			1_484, 291
	};
	
	private final String[] troopImageNames = new String[] {
			"troop_1435978003287_colored.png",
			"troop_1435807094346_colored.png",
			"troop_1435809657799_colored.png",
			"troop_1435809945313_colored.png",
			"troop_1435811131637_colored.png",
			"troop_1435841575319_colored.png",
			"troop_1435841832834_colored.png",
			"troop_1435842591508_colored.png",
			"troop_1435842662877_colored.png",
			"troop_1435926197976_colored.png",
			"troop_1435926297125_colored.png",
			"troop_1435980125998_colored.png",
			"troop_1435979943461_colored.png",
			"troop_1435979995403_colored.png",
			"troop_1435980060511_colored.png",
	};
	
	private final int[][] expectedTroops = new int[][] {
		new int[]{111,89,0,1},
		new int[]{106,103,0,1},
		new int[]{96,80,0,1},
		new int[]{92,77,0,1},
		new int[]{112,101,0,1},
		new int[]{118,102,0,1,1},
		new int[]{114,102,0,1,1},
		new int[]{110,100,0,1,1},
		new int[]{108,98,0,1,1},
		new int[]{0,8,16,1,1},
		new int[]{0,8,18,1,1},
		new int[]{90,90,0,0},
		new int[]{109,111,0,0},
		new int[]{110,110,0,0},
		new int[]{100,100,0,0},
	};
	
	private final String imageLocation = "/parser_images/";

	@Test
	public void testGoldParser() throws IOException, BotBadBaseException {
		int fail = 0;
		for (int i = 0; i < imageNames.length; i++) {
			String s = imageNames[i];
			BufferedImage image = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + s));
			int gold = ImageParser.parseGold(image);
			
			try {
				Assert.assertEquals(expectedGold[i], gold);
			} catch (AssertionError e) {
				fail++;
				System.err.println(e.getMessage());
			}
		}
		System.out.println("Gold Success Rate: " + (1 - (float) fail / imageNames.length));
	}

	@Test
	public void testElixirParser() throws IOException, BotBadBaseException {
		
		int fail = 0;
		for (int i = 0; i < imageNames.length; i++) {
			String s = imageNames[i];
			BufferedImage image = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + s));
			int elixir = ImageParser.parseElixir(image);
			
			try {
				Assert.assertEquals(expectedElixir[i], elixir);
			} catch (AssertionError e) {
				fail++;
				System.err.println(e.getMessage());
			}
		}
		System.out.println("Elixir Success Rate: " + (1 - (float)fail / imageNames.length));

	}

	@Test
	public void testDarkElixirParser() throws IOException, BotBadBaseException {
		
		int fail = 0;
		for (int i = 0; i < imageNames.length; i++) {
			String s = imageNames[i];
			BufferedImage image = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + s));
			int de = ImageParser.parseDarkElixir(image);
			
			try {
				Assert.assertEquals(expectedDarkElixir[i], de);
			} catch (AssertionError e) {
				fail++;
				System.err.println(e.getMessage());
			}
		}
		System.out.println("DE Success Rate: " + (1 - (float) fail / imageNames.length));

	}

	@Test
	public void testTroopParser() throws IOException, BotBadBaseException {
		
		int fail = 0;
		for (int i = 0; i < troopImageNames.length; i++) {
			String s = troopImageNames[i];
			BufferedImage image = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + s));
			int[] troops = ImageParser.parseTroopCount(image);
			
			try {
				Assert.assertArrayEquals(expectedTroops[i], troops);
			} catch (AssertionError e) {
				fail++;
				System.err.println(e.getMessage() + " " + Arrays.toString(expectedTroops[i]) +
						" vs " + Arrays.toString(troops));
			}
		}
		System.out.println("Troop Success Rate: " + (1 - (float)fail / troopImageNames.length));
	}
	
	@Test
	public void testBKParser() throws IOException {
		BufferedImage image = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + "troop_1435926297125_colored.png"));
		int parseBarbKingSlot = ImageParser.parseBarbKingSlot(image);
		
		Assert.assertEquals(3, parseBarbKingSlot);

		BufferedImage image2 = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + "troop_1435841575319_colored.png"));
		int parseBarbKingSlot2 = ImageParser.parseBarbKingSlot(image2);
		
		Assert.assertEquals(3, parseBarbKingSlot2);
	}

	@Test
	@Ignore
	public void testBaseParser() throws IOException, BotException {
		// processing: attack_1421204450459.png 1-9
		File baseDir = new File(TestImageParser.class.getResource("/full_base_images").getFile());

		int fail = 0;
		for (File f : baseDir.listFiles()) {
			System.out.println("processing " + f.getName());
			BufferedImage src = ImageIO.read(f);
			boolean isFullActual = ImageParser.isCollectorFullBase(src);
			boolean isFullExpected = f.getName().startsWith("true");

			try {
				Assert.assertEquals(isFullExpected, isFullActual);
			} catch (AssertionError e) {
				fail++;
				System.out.println(e.getMessage() + " for " + f.getName());
			}
		}
		System.out.println("Full Collector Base Success Rate: " + (1 - (float)fail / baseDir.listFiles().length));
	}
}
