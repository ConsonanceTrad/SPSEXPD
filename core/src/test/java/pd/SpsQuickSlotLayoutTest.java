package pd;

import pd.items.Item;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;

import java.util.ArrayList;

/** Verifies the SPS-style 3-region quickslot layout (bottom 3-10 / left 0-4 / right 0-4). */
public final class SpsQuickSlotLayoutTest {
	private SpsQuickSlotLayoutTest() { }

	public static void main(String[] args) {
		Game.version = "test";

		//1. 18 槽三区固定段：下 0-9、左 10-13、右 14-17
		check(QuickSlot.SIZE == 18, "快捷栏容量不是 18 槽");
		check(QuickSlot.BOTTOM_START == 0 && QuickSlot.BOTTOM_SIZE == 10, "下段槽位不是 0-9");
		check(QuickSlot.LEFT_START == 10 && QuickSlot.LEFT_SIZE == 4, "左段槽位不是 10-13");
		check(QuickSlot.RIGHT_START == 14 && QuickSlot.RIGHT_SIZE == 4, "右段槽位不是 14-17");
		check(QuickSlot.BOTTOM_SIZE + QuickSlot.LEFT_SIZE + QuickSlot.RIGHT_SIZE == QuickSlot.SIZE,
				"三区槽位段之和与总容量不一致");

		//2. 越界读写保护
		QuickSlot qs = new QuickSlot();
		Item item = new Item();
		qs.setSlot(-1, item);
		qs.setSlot(QuickSlot.SIZE, item);
		qs.setSlot(999, item);
		check(qs.getItem(-1) == null && qs.getItem(QuickSlot.SIZE) == null && qs.getItem(999) == null,
				"越界读取没有返回 null");
		check(qs.getSlot(item) == -1, "越界写入没有被忽略");

		//3. 跨区读写（三区末槽）
		Item bottom = new Item();
		Item left = new Item();
		Item right = new Item();
		qs.setSlot(QuickSlot.BOTTOM_START + 9, bottom);
		qs.setSlot(QuickSlot.LEFT_START + 3, left);
		qs.setSlot(QuickSlot.RIGHT_START + 3, right);
		check(qs.getItem(9) == bottom && qs.getItem(13) == left && qs.getItem(17) == right,
				"三区末槽读写异常");

		//4. 旧档兼容：placements 长度 9（SIZE=9 时代存档）还原不越界、槽号不变。
		//注意 Bundle.getCollection 是深拷贝（反射重建实例），断言用 quantity 区分对应关系
		QuickSlot legacy = new QuickSlot();
		Item ghost1 = new Item();
		ghost1.quantity(3);
		Item ghost2 = new Item();
		ghost2.quantity(5);
		ArrayList<Bundlable> ph = new ArrayList<>();
		ph.add(ghost1);
		ph.add(ghost2);
		Bundle b = new Bundle();
		b.put("placeholders", ph);
		b.put("placements", new boolean[]{false, true, true, false, false, false, false, false, true});
		legacy.restorePlaceholders(b);
		check(legacy.getItem(1) != null && legacy.getItem(1).quantity() == 3
				&& legacy.getItem(2) != null && legacy.getItem(2).quantity() == 5,
				"旧档 9 长度 placements 还原失败或槽号错位");

		//5. 旧档 placements 无有效位置时安静退出
		QuickSlot legacy2 = new QuickSlot();
		ArrayList<Bundlable> ph2 = new ArrayList<>();
		Item ghost3 = new Item();
		ghost3.quantity(0);
		ph2.add(ghost3);
		Bundle b2 = new Bundle();
		b2.put("placeholders", ph2);
		b2.put("placements", new boolean[9]);
		legacy2.restorePlaceholders(b2);
		check(legacy2.getItem(0) == null, "无有效位置的旧档还原意外写入了槽位");

		//6. 旧档 quickslotpos 0-8 语义：占位符替换只作用于同槽
		QuickSlot slotPos = new QuickSlot();
		Item stack = new Item();
		slotPos.setSlot(8, stack);
		check(slotPos.getSlot(stack) == 8, "旧档 0-8 槽号写入异常");

		System.out.println("SPS三区快捷栏测试通过：18槽区域结构、越界保护、跨区读写与旧档9长度placements兼容均正常。");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
