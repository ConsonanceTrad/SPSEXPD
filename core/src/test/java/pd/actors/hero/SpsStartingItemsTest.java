/*
 * Special Surprise Pixel Dungeon, GPLv3 or later.
 *
 * 无图形验证「开局物品单一真源」（HeroClass.startingItems / baseStartingItems）：
 * - 每个职业 × 每个主题的清单都能实例化、无 null，且物品类型与声明的槽位相符；
 * - 预览（preview）条数与声明条数一致（详情页展示与实际发放同源）；
 * - 「留空」语义：主题 >= IMPLEMENTED_SKINS（如「传奇征途」），以及没有皮肤内容的职业
 *   （决斗者 / 修士 / 剑圣），清单必须与经典（skin 0）完全一致。
 */

package pd.actors.hero;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;

import pd.items.Item;
import pd.items.KindOfWeapon;
import pd.items.KindofMisc;
import pd.items.equipment.armor.Armor;
import render.noosa.Game;

public final class SpsStartingItemsTest {

	private static int checks = 0;

	private static void check(boolean cond, String what) {
		checks++;
		if (!cond) throw new AssertionError("FAIL: " + what);
	}

	public static void main(String[] args) {
		Game.version = "test";
		GdxNativesLoader.load();
		new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		pd.messages.Messages.setup(pd.messages.Languages.CHI_SMPL);

		try {
			testSlotsMatchItemTypes();
			testBlankThemesEqualClassic();

			System.out.println("SpsStartingItemsTest PASS (" + checks + " checks)");
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		System.out.flush();
		System.exit(0);
	}

	/** 清单条目能实例化，且物品类型与声明的槽位相符 */
	private static void testSlotsMatchItemTypes() {
		for (HeroClass cls : HeroClass.playableClasses()) {
			for (int skin = 0; skin < HeroClass.THEME_COUNT; skin++) {
				checkLoadout(cls.name() + "/" + skin, HeroClass.startingItems(cls, skin));
			}
		}
		checkLoadout("通用", HeroClass.baseStartingItems());
	}

	private static void checkLoadout(String where, HeroClass.Loadout lo) {
		check(lo != null, where + " 清单非 null");

		int n = 0;
		for (HeroClass.LoadoutItem it : lo.items) {
			n++;
			String at = where + " 第 " + n + " 条";
			check(it.factory != null, at + " 有工厂");
			check(it.quantity >= 1, at + " 数量合法");
			check(it.upgrade >= 0, at + " 升级数合法");

			Item item = it.factory.get();
			check(item != null, at + " 能实例化");
			if (item == null) continue;

			String name = item.getClass().getSimpleName();
			switch (it.slot) {
				case WEAPON:
				case SECOND_WEP:
					check(item instanceof KindOfWeapon, at + " 是武器类（实际 " + name + "）");
					break;
				case ARMOR:
					check(item instanceof Armor, at + " 是护甲类（实际 " + name + "）");
					break;
				case ARTIFACT:
				case ACCESSORY4:
				case ACCESSORY5:
				case MISC:
					check(item instanceof KindofMisc, at + " 可进饰品槽（实际 " + name + "）");
					break;
				default:
					break;
			}
		}

		//详情页展示与实际发放同源：preview 的条数应与声明一致
		check(lo.preview().size() == n, where + " preview 条数与声明一致");

		//preview 要带上数量（否则 UI 的数量角标不会显示）
		//注：有些物品的数量来自构造参数（如 new BlindFruit(3)），声明 quantity=1 只是「不额外覆盖」
		java.util.ArrayList<Item> pv = lo.preview();
		int idx = 0;
		for (HeroClass.LoadoutItem it : lo.items) {
			Item item = it.factory.get();
			if (item == null) continue;
			Item shown = pv.get(idx++);
			check(shown.quantity() >= 1, where + " preview 数量至少为 1（实际 " + shown.quantity() + "）");
			if (it.quantity > 1) {
				check(shown.quantity() == it.quantity,
						where + " preview 数量与声明一致（声明 " + it.quantity + "，实际 " + shown.quantity() + "）");
			}
		}
	}

	/** 留空主题 / 无皮肤内容职业的清单必须与经典完全一致 */
	private static void testBlankThemesEqualClassic() {
		for (HeroClass cls : HeroClass.playableClasses()) {
			String classic = signature(HeroClass.startingItems(cls, 0));
			for (int skin = 0; skin < HeroClass.THEME_COUNT; skin++) {
				boolean blank = skin >= HeroClass.IMPLEMENTED_SKINS || !cls.supportsSkins();
				if (!blank) continue;
				check(signature(HeroClass.startingItems(cls, skin)).equals(classic),
						cls.name() + "/" + skin + " 无内容，应与经典清单一致");
			}
		}
	}

	private static String signature(HeroClass.Loadout lo) {
		StringBuilder sb = new StringBuilder();
		for (HeroClass.LoadoutItem it : lo.items) {
			Item item = it.factory.get();
			sb.append(it.slot).append(':')
					.append(item == null ? "null" : item.getClass().getName())
					.append("x").append(it.quantity).append('+').append(it.upgrade).append('|');
		}
		return sb.toString();
	}
}
