package aok.coc.util;

import aok.coc.exception.BotBadBaseException;
import aok.coc.exception.BotException;
import aok.coc.util.coords.Area;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.sikuli.core.search.RegionMatch;
import org.sikuli.core.search.algorithm.TemplateMatcher;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ImageParser {
	private static final int		ATTACK_GROUP_UNIT_DIFF	= 72;

	private static final Logger		logger					= Logger.getLogger(ImageParser.class.getName());

	private static final BufferedImage[] 	digitLoot = new BufferedImage[10];
	private static final BufferedImage[] 	digitTroop = new BufferedImage[10];
	private static final BufferedImage[] 	digitTroopBig = new BufferedImage[10];

	// boundaries of base according to Area.ENEMY_BASE
	private static final Point		ENEMY_BASE_LEFT			= new Point(13, 313);
	private static final Point		ENEMY_BASE_TOP			= new Point(401, 16);
	private static final Point		ENEMY_BASE_RIGHT		= new Point(779, 312);
	private static final Point		ENEMY_BASE_BOTTOM		= new Point(400, 597);
	private static final Polygon	ENEMY_BASE_POLY			= new Polygon();

	static {
		ENEMY_BASE_POLY.addPoint(ENEMY_BASE_LEFT.x, ENEMY_BASE_LEFT.y);
		ENEMY_BASE_POLY.addPoint(ENEMY_BASE_TOP.x, ENEMY_BASE_TOP.y);
		ENEMY_BASE_POLY.addPoint(ENEMY_BASE_RIGHT.x, ENEMY_BASE_RIGHT.y);
		ENEMY_BASE_POLY.addPoint(ENEMY_BASE_BOTTOM.x, ENEMY_BASE_BOTTOM.y);

        for (int i = 0; i < digitLoot.length; i++) {
            digitLoot[i] = fromResource("/digits/l" + i + ".png");
        }

        for (int i = 0; i < digitTroop.length; i++) {
			digitTroop[i] = fromResource("/digits/t" + i + ".png");
        }

        for (int i = 0; i < digitTroopBig.length; i++) {
			digitTroopBig[i] = fromResource("/digits/tb" + i + ".png");
        }
    }

	static boolean hasDE(BufferedImage image) throws BotBadBaseException {
		int deCheck = image.getRGB(20, 0);

		if (RobotUtils.compareColor(deCheck, new Color(179, 172, 103).getRGB(), 7)) {
			return true;
		} else {
			return false;
		}
	}

	public static int parseGold(BufferedImage image) throws BotBadBaseException {
		return parseGoldFromBinary(imageToBinary(image));
	}

	static int parseGoldFromBinary(BufferedImage binary) throws BotBadBaseException {
		return parseNumberFromBinary(binary, 33, 0, digitLoot, binary.getWidth() - 33, 12);
	}

	public static int parseElixir(BufferedImage image) throws BotBadBaseException {
		return parseElixirFromBinary(imageToBinary(image));
	}

	static int parseElixirFromBinary(BufferedImage binary) throws BotBadBaseException {
		return parseNumberFromBinary(binary, 33, 29, digitLoot, binary.getWidth() - 33, 12);
	}

	public static int parseDarkElixir(BufferedImage image) throws BotBadBaseException {
		return parseDarkElixirFromBinary(imageToBinary(image));
	}

	static int parseDarkElixirFromBinary(BufferedImage binary) throws BotBadBaseException {
		return parseNumberFromBinary(binary, 33, 57, digitLoot, binary.getWidth() - 33, 12);
	}

	public static int[] parseTroopCount() {
		BufferedImage image = RobotUtils.screenShot(Area.ATTACK_GROUP);
		int[] troopCount = parseTroopCount(image);
		logger.info("[Troop count: " + Arrays.toString(troopCount) + "]");
		return troopCount;
	}

	static int[] parseTroopCount(BufferedImage image) {
		BufferedImage binary = imageToBinary(image);

		if (ConfigUtils.instance().isDebug()) {
			String name = "troop_" + System.currentTimeMillis();
			try {
				RobotUtils.saveImage(image, "debug", name + "_colored.png");
				RobotUtils.saveImage(binary, "debug", name + "_binary.png");
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Unable to save image", e);
			}
		}

		int[] tmp = new int[11]; // max group size

		int xStart = 13;
		final int yStart = 3;

		int no;
		int curr = 0;

		// first slot is always pre-selected.
		boolean selected = true;
		while (true) {
			try {
				if (selected) {
					no = parseNumberFromBinary(binary, xStart, yStart, digitTroopBig, ATTACK_GROUP_UNIT_DIFF * 5 / 6, 20);
					selected = false;
				} else {
					no = parseNumberFromBinary(binary, xStart, yStart, digitTroop, ATTACK_GROUP_UNIT_DIFF * 5 / 6, 20);
				}
			} catch (BotBadBaseException e) {
				break;
			}

			if (no >= 6) {
				tmp[curr] = no;
			} else {
				// ignore 1,2,3,4,5 because they are usually
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
		Rectangle rectangle = findArea(image, "/images/bk.png");
		if (rectangle == null) {
			return null;
		}

		return rectangle.x / ATTACK_GROUP_UNIT_DIFF;
	}

	static Integer parseArcherQueenSlot(BufferedImage image) {
		Rectangle rectangle = findArea(image, "/images/aq.png");
		if (rectangle == null) {
			return null;
		}

		return rectangle.x / ATTACK_GROUP_UNIT_DIFF;
	}

	static BufferedImage fromResource(String url) {
		try {
			return ImageIO.read(ImageParser.class.getResource(url));
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Unable to read url " + url, e);
			throw new RuntimeException(e);
		}
	}

	static Rectangle findArea(BufferedImage input, String resourcePath) {
		BufferedImage tar = fromResource(resourcePath);

		List<RegionMatch> doFindAll = TemplateMatcher.findMatchesByGrayscaleAtOriginalResolution(
			input, tar, 1, 0.9);

		if (doFindAll.isEmpty()) {
			return null;
		} else {
			return doFindAll.get(0).getBounds();
		}
	}

	static BufferedImage imageToBinary(BufferedImage colored) {

		// to greyscale
		BufferedImage greyscale = new BufferedImage(colored.getWidth(), colored.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = greyscale.getGraphics();
		g.drawImage(colored, 0, 0, null);
		g.dispose();

		// to binary
		BufferedImage binary = new BufferedImage(greyscale.getWidth(), greyscale.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int i = 0; i < greyscale.getWidth(); i++) {
			for (int j = 0; j < greyscale.getHeight(); j++) {
				int rgb = greyscale.getRGB(i, j);
				if ((rgb & 0xFF) > 0xF0) {
					binary.setRGB(i, j, 0xFFFFFF);
				} else {
					binary.setRGB(i, j, 0);
				}
			}
		}
		return binary;
	}

	static int parseNumberFromBinary(BufferedImage binary, int xStart, int yStart, BufferedImage digit[],
									 int width, int height) throws BotBadBaseException {

        List<Rectangle> rois = new ArrayList<>();
        rois.add(new Rectangle(xStart, yStart, width, height));

		// RegionMatch, digitLoot pair
        List<Pair<RegionMatch, Integer>> matches = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
            List<RegionMatch> digitMatches =
				TemplateMatcher.findMatchesByGrayscaleAtOriginalResolutionWithROIs(binary, digit[i], 10, 0.60, rois);

			final int finalI = i;
			digitMatches.forEach(rm -> matches.add(new MutablePair<>(rm, finalI)));

            for (RegionMatch match : digitMatches) {
				logger.finest("digitLoot:" + i + " " + match.getScore() + " " + match.getLocation());
			}
		}

		if (matches.isEmpty()) {
			throw new BotBadBaseException(String.format("Could not parse any number at %d %d", xStart, yStart));
		}

		// filter out bad matches
		// sort by score
        matches.sort((o1, o2) -> Double.compare(o2.getLeft().getScore(), o1.getLeft().getScore()));
		List<Range<Integer>> ranges = new ArrayList<>();
		int bestY = matches.get(0).getLeft().y;
		for (Iterator<Pair<RegionMatch, Integer>> it = matches.iterator(); it.hasNext(); ) {
			Pair<RegionMatch, Integer> match = it.next();
			Range<Integer> range = Range.between(match.getLeft().x, match.getLeft().x + digit[match.getRight()].getWidth());

			boolean removed = false;

			// if has far Y value discard
			if (Math.abs(match.getKey().getY() - bestY) > 1) {
				it.remove();
				removed = true;
				continue;
			}
			for (Range integerRange : ranges) {
				if (integerRange.isOverlappedBy(range)) {
					it.remove();
					removed = true;
					break;
				}
			}

			if (!removed) {
				ranges.add(range);
			}
		}

		StringBuilder no = new StringBuilder();
		matches.sort((o1, o2) -> Integer.compare(o1.getLeft().x, o2.getLeft().x));
		matches.forEach(match -> no.append(match.getRight()));

		if (no.length() == 0) {
			return 0;
		} else {
			logger.finest(String.format("found %s  at %d %d", no, xStart, yStart));
			return Integer.parseInt(no.toString());
		}

	}

	static int[] parseLoot(BufferedImage image) throws BotBadBaseException {
		BufferedImage binary = imageToBinary(image);
		boolean hasDE = hasDE(image);

		int gold = parseGoldFromBinary(binary);
		int elixir = parseElixirFromBinary(binary);
		int de = hasDE ? parseDarkElixirFromBinary(binary) : 0;
		logger.info(String.format("[gold: %d, elixir: %d, de: %d]",
			gold, elixir, de));

		if (ConfigUtils.instance().isDebug()) {
			String name = "loot_" + System.currentTimeMillis();
			try {
				RobotUtils.saveImage(image, "debug", name + "_colored.png");
				RobotUtils.saveImage(binary, "debug", name + "_binary.png");
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Unable to save image", e);
			}
		}

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
				if (Files.isDirectory(next) || Files.isHidden(next) || !next.toString().endsWith(".png")) {
					continue;
				}
				BufferedImage tar = ImageIO.read(Files.newInputStream(next, StandardOpenOption.READ));

				if (tar == null) {
					continue;
				}

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
