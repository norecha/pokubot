package aok.coc.util;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class TestTroopParser {
	
	private final String[] imageNames = new String[] {"troop_1420776935220.png"};
	
	private final String imageLocation = "/parser_images/";

	@Test
	public void testTroopParser() throws IOException {
		for (String imageName : imageNames) {
			BufferedImage image = ImageIO.read(TestImageParser.class.getResourceAsStream(imageLocation + imageName));
			
		}
	}
}
