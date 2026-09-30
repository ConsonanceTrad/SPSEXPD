package pd.levels.features;

import pd.levels.ChaosLevel;
import pd.levels.DeadEndLevel;
import pd.levels.NewRoomLevel;
import render.noosa.Game;

import java.nio.ByteBuffer;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Regression checks for legacy fixed-map and main-route sign messages. */
public final class SpsSignTest {

	public static void main(String[] args) throws Exception {
		Game.version = "test";
		check("dead_end".equals(Sign.messageKey(new DeadEndLevel(), 14, 0)), "死路告示牌文本错误");
		check("chaos".equals(Sign.messageKey(new ChaosLevel(), 14, 0)), "混沌层告示牌误用了主线层号");
		check("new_room_0".equals(Sign.messageKey(new NewRoomLevel(), 14, 0)), "标准样板房告示牌文本错误");
		check("new_room_1".equals(Sign.messageKey(new NewRoomLevel(), 14, 1)), "森林样板房告示牌文本错误");
		check("new_room_0".equals(Sign.messageKey(new NewRoomLevel(), 14, 99)), "损坏的样板房类型没有安全回退");
		check("tip_22".equals(Sign.messageKey(null, 22, 0)), "主线告示牌没有使用实际层号");
		check(Sign.messageKey(null, 26, 0) == null, "终局层不应生成普通提示文本");

		for (String lang : new String[]{"en", "zh", "zh-hant", "ru"}) {
			Path file = Path.of("messages", "levels", lang, "levels.properties");
			String text = strictUtf8(file);
			for (String key : new String[]{"chaos", "new_room_0", "new_room_1"}) {
				String prefix = "levels.features.sign." + key + "=";
				int start = text.indexOf(prefix);
				check(start >= 0, file + "缺少" + key);
				int valueStart = start + prefix.length();
				int end = text.indexOf('\n', valueStart);
				if (end < 0) end = text.length();
				check(!text.substring(valueStart, end).trim().isEmpty(), file + "的" + key + "为空");
			}
			check(text.indexOf('\uFFFD') < 0, file + "含UTF-8替换字符");
		}

		System.out.println("SPS告示牌测试通过：主线、死路、混沌层、样板房分流及英简繁俄UTF-8文本均正常。");
	}

	private static String strictUtf8(Path file) throws Exception {
		return StandardCharsets.UTF_8.newDecoder()
				.onMalformedInput(CodingErrorAction.REPORT)
				.onUnmappableCharacter(CodingErrorAction.REPORT)
				.decode(ByteBuffer.wrap(Files.readAllBytes(file))).toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsSignTest() {
	}
}
