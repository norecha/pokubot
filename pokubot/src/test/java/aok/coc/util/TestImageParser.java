package aok.coc.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Test;

import aok.coc.exception.BotBadBaseException;
import aok.coc.exception.BotException;

public class TestImageParser {
	
	static {
		try (InputStream inputStream = TestImageParser.class.getResourceAsStream("/logging.properties")) {
			LogManager.getLogManager().readConfiguration(inputStream);
		} catch (final IOException e) {
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}
	}

	private final String[]	imageNames	= new String[] { "zort_1420312983010_0.png", "zort_1420312988100_0.png", "zort_1420312993150_0.png", "zort_1420312998219_0.png", "zort_1420313003290_0.png", "zort_1420313008343_0.png", "zort_1420313013415_0.png", "zort_1420313018469_0.png", "zort_1420313023540_0.png", "zort_1420313028608_0.png", "zort_1420313033674_0.png", "zort_1420313038742_0.png", "zort_1420313043808_0.png", "zort_1420313048879_0.png", "zort_1420313053961_0.png", "zort_1420313059039_0.png", "zort_1420313064108_0.png", "zort_1420313069163_0.png", 
			"zort_1420313074233_0.png", "zort_1420313079309_0.png", "zort_1420317535502_0.png",
			"issue11.png", "issue11_2.png", "issue11_3.png"};
	private final int[] expectedGold = new int[] {
		30806, 382230, 30999, 32849, 72961, 63132, 283217,10_264,122_359,222_267,50_994,
		42_007,4_725,52_528,24_624,45_767,11_988,14_400,25_000,80_923, 137_145,
		144_795, 95_868, 77_037
	};
	
	private final int[] expectedElixir = new int[] {
			20_877, 361_799, 27_061, 31_577, 141_291, 11_216,
			325_808, 76_351, 165_735, 248_571, 17_096, 28_785, 45_000,
			44_299, 31_362, 43_800, 35_281, 94_392, 15_000, 67_962, 74_325,
			49_608, 49_650, 127_782
	};
	
	private final int[] expectedDarkElixir = new int[] {
			0, 629, 0, 0, 352, 401, 608, 0, 0, 762, 0, 0, 0, 2,
			100, 0, 168, 0, 0, 0, 1026, 106, 520, 1_145
	};
	
	private final int[] expectedTrophy= new int[] {
			14, 13, 20, 10, 14, 30, 17, 20, 13, 16, 22, 17, 22, 21, 18, 29, 34, 13, 14, 28, 15,
			18, 30, 31
	};
	
	private final String[] troopImageNames = new String[] {"troop_1420776935220.png", "troop_1420820135855.png",
		"troop_1420820165432.png", "troop_1420820170086.png", "troop_1420820177795.png", "troop_1420820189647.png",
		"troop_1420820193767.png", "troop_1420820198106.png", "troop_1420820765409.png"
	};
	
	private final int[][] expectedTroops = new int[][] {
		new int[]{145,55,1},
		new int[]{153,46,1},
		new int[]{152,46,1},
		new int[]{151,46,1},
		new int[]{150,46,1},
		new int[]{149,46,1},
		new int[]{148,46,1},
		new int[]{147,46,1},
		new int[]{142,46,1},
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
		System.out.println("Gold Success Rate: " + (1 - (float)fail / imageNames.length));

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
		System.out.println("DE Success Rate: " + (1 - (float)fail / imageNames.length));

	}

	@Test
	public void testTrophyParser() throws IOException, BotBadBaseException {
		
		int fail = 0;
		for (int i = 0; i < imageNames.length; i++) {
			String s = imageNames[i];
			BufferedImage image = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + s));
			int trophy = ImageParser.parseTrophy(image);
			
			try {
				Assert.assertEquals(expectedTrophy[i], trophy);
			} catch (AssertionError e) {
				fail++;
				System.err.println(e.getMessage());
			}
		}
		System.out.println("Tropy Success Rate: " + (1 - (float)fail / imageNames.length));
	}

	@Test
	public void testTroopParser() throws IOException {
		
		int fail = 0;
		for (int i = 0; i < troopImageNames.length; i++) {
			String s = troopImageNames[i];
			BufferedImage image = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + s));
			int[] troops = ImageParser.parseTroopCount(image);
			
			try {
				Assert.assertArrayEquals(expectedTroops[i], troops);
			} catch (AssertionError e) {
				fail++;
				System.err.println(e.getMessage());
			}
		}
		System.out.println("Troop Success Rate: " + (1 - (float)fail / troopImageNames.length));
	}
	
	@Test
	public void testBKParser() throws IOException {
		BufferedImage image = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + "troop_1420928729522.png"));
		int parseBarbKingSlot = ImageParser.parseBarbKingSlot(image);
		
		Assert.assertEquals(3, parseBarbKingSlot);

		BufferedImage image2 = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + "troop_1420820765409.png"));
		int parseBarbKingSlot2 = ImageParser.parseBarbKingSlot(image2);
		
		Assert.assertEquals(2, parseBarbKingSlot2);
	}
	
	@Test
	public void testBaseParser() throws IOException, BotException {
		// processing: attack_1421204450459.png 1-9
		File baseDir = new File(TestImageParser.class.getResource("/full_base_images").getFile());

		int fail = 0;
		for (File f : baseDir.listFiles()) {
			String name = f.getName();
//			if (!name.equals("attack_1422589140788_no_9.png")) {
//				continue;
//			}
			@SuppressWarnings("unused")
			Integer thLevel;
			try {
				thLevel = Integer.parseInt(name.substring(name.lastIndexOf('_') + 1, name.lastIndexOf('.')));
			} catch (NumberFormatException e) {
				thLevel = null;
			}
			int secondIndexOf_ = name.indexOf('_', name.indexOf('_') + 1);
			String bool = name.substring(secondIndexOf_ + 1, name.lastIndexOf('_'));
			boolean expected = bool.equals("yes");
			BufferedImage src = ImageIO.read(f);
			boolean isAttackable = ImageParser.isCollectorFullBase(src);
			try {
				Assert.assertEquals(name, expected, isAttackable);
			} catch (AssertionError e) {
				fail++;
				System.err.println(e.getMessage());
			}
		}
		System.out.println("Base detecter Success Rate: " + (1 - (float)fail / baseDir.listFiles().length));
	}
	
	@Test
	public void testIssue11() throws IOException, BotBadBaseException {
		try {
			BufferedImage image = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + "issue11_3.png"));
			int[] loot = ImageParser.parseLoot(image);
			System.out.println(Arrays.toString(loot));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
