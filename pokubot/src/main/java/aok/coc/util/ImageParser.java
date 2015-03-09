package aok.coc.util;

import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.sikuli.core.search.RegionMatch;
import org.sikuli.core.search.algorithm.TemplateMatcher;

import aok.coc.exception.BotBadBaseException;
import aok.coc.exception.BotException;
import aok.coc.util.coords.Area;

public class ImageParser {
	private static final int		VAR						= 5;
	private static final int		ATTACK_GROUP_UNIT_DIFF	= 72;

	private static final Logger		logger					= Logger.getLogger(ImageParser.class.getName());

	// use 3 points to detect number
	// offsets are relative to top left of the digit

	/**
	 * types:
	 * 1: gold
	 * 2: elixir
	 * 3: dark elixir
	 * 4: attack groups
	 */
	private static final int[][]	OFFSET_ZERO				= new int[][] { { 6, 4 }, { 7, 7 }, { 10, 13 } };
	private static final int[][]	COLOR_ZERO				= new int[][] { { 0x989579, 0x39382E, 0x272720 },
															{ 0x978A96, 0x393439, 0x272427 },
															{ 0x909090, 0x363636, 0x262626 },
															{ 0x979797, 0x393939, 0x262626 }
															};

	private static final int[][]	OFFSET_ONE				= new int[][] { { 1, 1 }, { 1, 12 }, { 4, 12 } };
	private static final int[][]	COLOR_ONE				= new int[][] { { 0x979478, 0x313127, 0xD7D4AC },
															{ 0x968895, 0x312D31, 0xD8C4D6 },
															{ 0x8F8F8F, 0x2F2F2F, 0xCDCDCD },
															{ 0x979797, 0x313131, 0xD7D7D7 }
															};

	private static final int[][]	OFFSET_TWO				= new int[][] { { 1, 7 }, { 3, 6 }, { 7, 7 } };
	private static final int[][]	COLOR_TWO				= new int[][] { { 0xA09E80, 0xD8D4AC, 0x979579 },
															{ 0xA0919F, 0xD8C4D6, 0x978A96 },
															{ 0x989898, 0xCDCDCD, 0x909090 },
															{ 0xA0A0A0, 0xD8D8D8, 0x979797 }
															};

	private static final int[][]	OFFSET_THREE			= new int[][] { { 2, 3 }, { 4, 8 }, { 5, 13 } };
	private static final int[][]	COLOR_THREE				= new int[][] { { 0x7F7D65, 0x070706, 0x37362C },
															{ 0x7F737E, 0x070607, 0x373236 },
															{ 0x797979, 0x070707, 0x343434 },
															{ 0x7F7F7F, 0x070707, 0x373737 }
															};

	private static final int[][]	OFFSET_FOUR				= new int[][] { { 2, 3 }, { 3, 1 }, { 1, 5 } };
	private static final int[][]	COLOR_FOUR				= new int[][] { { 0x282720, 0x080806, 0x403F33 },
															{ 0x282428, 0x080708, 0x403A40 },
															{ 0x262626, 0x070707, 0x3D3D3D },
															{ 0x282828, 0x080808, 0x404040 }
															};

	private static final int[][]	OFFSET_FIVE				= new int[][] { { 5, 4 }, { 4, 9 }, { 6, 12 } };
	private static final int[][]	COLOR_FIVE				= new int[][] { { 0x060604, 0x040403, 0xB7B492 },
															{ 0x060606, 0x040404, 0xB7A7B6 },
															{ 0x060606, 0x040404, 0xAFAFAF },
															{ 0x060606, 0x040404, 0xB7B7B7 },
															};

	private static final int[][]	OFFSET_SIX				= new int[][] { { 5, 4 }, { 5, 9 }, { 8, 5 } };
	private static final int[][]	COLOR_SIX				= new int[][] { { 0x070605, 0x040403, 0x181713 },
															{ 0x070707, 0x040404, 0x181618 },
															{ 0x060606, 0x030303, 0x161616 },
															{ 0x070707, 0x040404, 0x181818 }
															};

	private static final int[][]	OFFSET_SEVEN			= new int[][] { { 5, 11 }, { 4, 3 }, { 7, 7 } };
	private static final int[][]	COLOR_SEVEN				= new int[][] { { 0x5E5C4B, 0x87856C, 0x5D5C4B },
															{ 0x5F565E, 0x877B86, 0x5F565E },
															{ 0x5A5A5A, 0x818181, 0x5A5A5A },
															{ 0x5E5E5E, 0x878787, 0x5D5D5D }
															};

	private static final int[][]	OFFSET_EIGHT			= new int[][] { { 5, 3 }, { 5, 10 }, { 1, 6 } };
	private static final int[][]	COLOR_EIGHT				= new int[][] { { 0x27261F, 0x302F26, 0x26261F },
															{ 0x282427, 0x302C30, 0x262326 },
															{ 0x252525, 0x2D2D2D, 0x242424 },
															{ 0x282828, 0x303030, 0x262626 }
															};

	private static final int[][]	OFFSET_NINE				= new int[][] { { 5, 5 }, { 5, 9 }, { 8, 12 } };
	private static final int[][]	COLOR_NINE				= new int[][] { { 0x302F26, 0x050504, 0x272720 },
															{ 0x302C30, 0x050505, 0x282427 },
															{ 0x2E2E2E, 0x050505, 0x262626 },
															{ 0x303030, 0x050505, 0x272727 }
															};

	private static final int[][][]	offsets					= new int[10][][];
	private static final int[][][]	colors					= new int[10][][];

	private static final int[]		widths					= new int[] { 13, 6, 10, 10, 12, 10, 11, 10, 11, 11 };

	// boundaries of base according to Area.ENEMY_BASE
	private static final Point		ENEMY_BASE_LEFT			= new Point(13, 313);
	private static final Point		ENEMY_BASE_TOP			= new Point(401, 16);
	private static final Point		ENEMY_BASE_RIGHT		= new Point(779, 312);
	private static final Point		ENEMY_BASE_BOTTOM		= new Point(400, 597);
	private static final Polygon	ENEMY_BASE_POLY			= new Polygon();

	static {
		offsets[0] = OFFSET_ZERO;
		offsets[1] = OFFSET_ONE;
		offsets[2] = OFFSET_TWO;
		offsets[3] = OFFSET_THREE;
		offsets[4] = OFFSET_FOUR;
		offsets[5] = OFFSET_FIVE;
		offsets[6] = OFFSET_SIX;
		offsets[7] = OFFSET_SEVEN;
		offsets[8] = OFFSET_EIGHT;
		offsets[9] = OFFSET_NINE;

		colors[0] = COLOR_ZERO;
		colors[1] = COLOR_ONE;
		colors[2] = COLOR_TWO;
		colors[3] = COLOR_THREE;
		colors[4] = COLOR_FOUR;
		colors[5] = COLOR_FIVE;
		colors[6] = COLOR_SIX;
		colors[7] = COLOR_SEVEN;
		colors[8] = COLOR_EIGHT;
		colors[9] = COLOR_NINE;

		ENEMY_BASE_POLY.addPoint(ENEMY_BASE_LEFT.x, ENEMY_BASE_LEFT.y);
		ENEMY_BASE_POLY.addPoint(ENEMY_BASE_TOP.x, ENEMY_BASE_TOP.y);
		ENEMY_BASE_POLY.addPoint(ENEMY_BASE_RIGHT.x, ENEMY_BASE_RIGHT.y);
		ENEMY_BASE_POLY.addPoint(ENEMY_BASE_BOTTOM.x, ENEMY_BASE_BOTTOM.y);
	}

	static boolean hasDE(BufferedImage image) throws BotBadBaseException {
		int deCheck = image.getRGB(20, 0);
		
		// 0x80752B
		if (RobotUtils.compareColor(deCheck, new Color(128, 117, 43).getRGB(), 7)) {
			return true;
		} else if (RobotUtils.compareColor(deCheck, 0xffb1a841, 7)) {
			return false;
		} else {
			throw new BotBadBaseException("de: " + Integer.toHexString(deCheck));
		}
	}

	public static int parseGold(BufferedImage image) throws BotBadBaseException {
		return parseNumber(image, 0, 33, 0 + (hasDE(image) ? 0 : 1), image.getWidth() - 43);
	}

	public static int parseElixir(BufferedImage image) throws BotBadBaseException {
		return parseNumber(image, 1, 33, 29 + (hasDE(image) ? 0 : 1), image.getWidth() - 43);
	}

	public static int parseDarkElixir(BufferedImage image) throws BotBadBaseException {
		if (!hasDE(image)) {
			return 0;
		}
		return parseNumber(image, 2, 33, 57, image.getWidth() - 43);
	}

	public static int parseTrophy(BufferedImage image) throws BotBadBaseException {
		if (!hasDE(image)) {
			return parseNumber(image, 3, 33, 62, image.getWidth() - 43);
		} else {
			return parseNumber(image, 3, 33, 90, image.getWidth() - 43);
		}
	}

	public static int[] parseTroopCount() {
		BufferedImage image = RobotUtils.screenShot(Area.ATTACK_GROUP);
		int[] troopCount = parseTroopCount(image);
		logger.info("[Troop count: " + Arrays.toString(troopCount) + "]");
		return troopCount;
	}

	static int[] parseTroopCount(BufferedImage image) {
		int[] tmp = new int[11]; // max group size

		int xStart = 20;
		final int yStart = 11;

		int no;
		int curr = 0;
		while (true) {
			no = parseNumber(image, 3, xStart, yStart, ATTACK_GROUP_UNIT_DIFF - 10);
			if (no == 0) {
				break;
			}
			if (no >= 5) {
				tmp[curr] = no;
			} else {
				// ignore 1,2,3,4 because they are usually
				// cc or spells
				tmp[curr] = 0;
			}
			curr++;
			xStart += ATTACK_GROUP_UNIT_DIFF;
		}

		Integer barbKingSlot = parseBarbKingSlot(image);
		if (barbKingSlot != null) {
			tmp[barbKingSlot] = 1;

			// if BK was found after a 0 slot, new length should be adjusted according to BK
			// ie [110, 90, 0, BK] -> len = 4
			curr = Math.max(curr + 1, barbKingSlot + 1);
		}

		Integer archerQueenSlot = parseArcherQueenSlot(image);
		if (archerQueenSlot != null) {
			tmp[archerQueenSlot] = 1;

			// if AQ was found after a 0 slot, new length should be adjusted according to AQ
			// ie [110, 90, 0, AQ] -> len = 4
			curr = Math.max(curr + 1, archerQueenSlot + 1);
		}

		return Arrays.copyOf(tmp, curr);
	}

	static Integer parseBarbKingSlot(BufferedImage image) {
		Rectangle rectangle = findArea(image, ImageParser.class.getResource("/images/bk.png"));
		if (rectangle == null) {
			return null;
		}

		return rectangle.x / ATTACK_GROUP_UNIT_DIFF;
	}

	static Integer parseArcherQueenSlot(BufferedImage image) {
		Rectangle rectangle = findArea(image, ImageParser.class.getResource("/images/aq.png"));
		if (rectangle == null) {
			return null;
		}

		return rectangle.x / ATTACK_GROUP_UNIT_DIFF;
	}
	
	public static Point findTrainButton() {
		BufferedImage image = RobotUtils.screenShot(Area.BARRACKS_BUTTONS);
		Rectangle rectangle = findArea(image, ImageParser.class.getResource("/images/train.png"));
		if (rectangle == null) {
			return null;
		}
		
		Point ret = rectangle.getLocation();
		ret.x += Area.BARRACKS_BUTTONS.getX1();
		ret.y += Area.BARRACKS_BUTTONS.getY1();
		return ret;
	}

	static Rectangle findArea(BufferedImage input, URL url) {
		BufferedImage tar;
		try {
			tar = ImageIO.read(url);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to read url " + url, e);
			return null;
		}

		List<RegionMatch> doFindAll = TemplateMatcher.findMatchesByGrayscaleAtOriginalResolution(
			input, tar, 1, 0.9);

		if (doFindAll.isEmpty()) {
			return null;
		} else {
			return doFindAll.get(0).getBounds();
		}
	}

	static int parseNumber(BufferedImage image, int type, int xStart, int yStart, int maxSearchWidth) {

		String no = "";
		int curr = xStart;
		while (curr < xStart + maxSearchWidth) {
			Integer i = parseDigit(image, curr, yStart, type);
			if (i != null) {
				no += i;
				curr += widths[i] - 1;
			} else {
				curr++;
			}
		}
		if (!no.isEmpty()) {
			return Integer.parseInt(no);
		} else {
			return 0;
		}
	}

	private static Integer parseDigit(BufferedImage image, int xStart, int yStart, int type) {

		for (int i = 0; i < 10; i++) {
			boolean found = true;
			for (int j = 0; j < offsets[i].length; j++) {
				int actual = image.getRGB(xStart + offsets[i][j][0], yStart + offsets[i][j][1]);
				int expected = colors[i][type][j];
				if (!RobotUtils.compareColor(actual, expected, VAR)) {
					found = false;
					break;
				}
			}

			if (found) {
				return i;
			}
		}

		return null;
	}

	static int[] parseLoot(BufferedImage image) throws BotBadBaseException {
		int gold = parseGold(image);
		int elixir = parseElixir(image);
		int de = parseDarkElixir(image);
		logger.info(String.format("[gold: %d, elixir: %d, de: %d]",
			gold, elixir, de));

		return new int[] { gold, elixir, de };
	}

	public static int[] parseLoot() throws BotBadBaseException {
		BufferedImage image = RobotUtils.screenShot(Area.ENEMY_LOOT);

		return parseLoot(image);
	}

	public static boolean isCollectorFullBase() throws BotException {
		return isCollectorFullBase(RobotUtils.screenShot(Area.ENEMY_BASE));
	}

	@SuppressWarnings("resource")
	static boolean isCollectorFullBase(BufferedImage image) throws BotException {

		FileSystem fileSystem = null;
		Stream<Path> walk = null;
		try  {
			URI uri = ImageParser.class.getResource("/elixir_images").toURI();
			Path images;
			if (uri.getScheme().equals("jar")) {
				fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap());
				images = fileSystem.getPath("/elixir_images");
			} else {
				images = Paths.get(uri);
			}
			walk = Files.walk(images, 1);

			List<Rectangle> matchedElixirs = new ArrayList<>();
			int attackableElixirs = 0;
			for (Iterator<Path> it = walk.iterator(); it.hasNext();) {
				Path next = it.next();
				if (Files.isDirectory(next)) {
					continue;
				}
				BufferedImage tar = ImageIO.read(Files.newInputStream(next, StandardOpenOption.READ));
				List<RegionMatch> doFindAll = TemplateMatcher.findMatchesByGrayscaleAtOriginalResolution(
					image, tar, 7, 0.8);

				int c = 0;

				RECT_LOOP:
				for (RegionMatch i : doFindAll) {
					
					// if matched area is out of enemy poly
					if (!ENEMY_BASE_POLY.contains(i.x, i.y)) {
						continue;
					}
					
					// check if it's an existing match
					for (Rectangle r : matchedElixirs) {
						if (r.intersects(i.getBounds())) {
							break RECT_LOOP;
						}
					}
					c++;
					matchedElixirs.add(i.getBounds());
					if (next.getFileName().toString().startsWith("empty")) {
						attackableElixirs--;
					} else if (next.getFileName().toString().startsWith("full")) {
						attackableElixirs++;
					}
					logger.finest("\t" + i.getBounds() + " score: " + i.getScore());
				}
				if (c > 0) {
					logger.finest(String.format("\tfound %d elixirs matching %s\n", c, next.getFileName().toString()));
				}
			}
			
			boolean result = attackableElixirs >= 0;
			if (result == false) {
				logger.info("empty collectors");
			}
			return result;
		} catch (Exception e) {
			throw new BotException(e.getMessage(), e);
		} finally {
			if (fileSystem != null) {
				try {
					fileSystem.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			if (walk != null) {
				walk.close();
			}
		}
	}
}
