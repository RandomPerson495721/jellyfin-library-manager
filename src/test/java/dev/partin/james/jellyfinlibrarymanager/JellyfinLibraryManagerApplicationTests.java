package dev.partin.james.jellyfinlibrarymanager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class JellyfinLibraryManagerApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void quickRegexTest() {
		int TFF = 0;
		int Progressive = 0;
		String testString = "[Parsed_idet_0 @ 0x600002684b00] Multi frame detection: TFF:     5 BFF:     0 Progressive:   376 Undetermined:   120";
		String regex = "TFF: (.*?) BFF";
		String regex2 = "Progressive: (.*?) Undetermined";
		Pattern pattern = Pattern.compile(regex);
		Pattern pattern2 = Pattern.compile(regex2);
		Matcher TFFmatcher = pattern.matcher(testString);
		Matcher ProgressiveMatcher = pattern2.matcher(testString);
		if (TFFmatcher.find()) {
			TFF = Integer.parseInt(TFFmatcher.group(1).trim());
		}
		if (ProgressiveMatcher.find()) {
			Progressive = Integer.parseInt(ProgressiveMatcher.group(1).trim());
		}
		assert TFF == 5;
		assert Progressive == 376;
	}

	@Test
	void stringParseTest() {
		String testString = "[Parsed_metadata_1 @ 0x600003b74210] lavfi.cropdetect.w=1712";
		int width = Integer.parseInt(testString.substring(testString.indexOf("=") + 1));
		assert width == 1712;
	}

}
