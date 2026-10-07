package pd.windows;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.Dungeon;
import pd.actors.Actor;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.artifacts.CloakOfSheep;
import pd.items.equipment.artifacts.EndlessAmmoBag;
import pd.items.equipment.artifacts.GoddessRadiance;
import pd.items.equipment.artifacts.HandOfTheElder;
import pd.items.equipment.artifacts.HeartOfSatan;
import pd.items.equipment.artifacts.NaturalAxe;
import pd.items.equipment.artifacts.VoidHand;
import pd.items.equipment.weapon.melee.special.TrinityForce;
import pd.items.equipment.weapon.missiles.Thrower;
import pd.messages.Languages;
import pd.messages.Messages;
import render.noosa.Game;
import render.utils.math.Random;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/** Headless checks that the ported/original gear shows up in the in-game debugger item list. */
public final class SpsDebugItemsTest {

	/** SPSEXPD: 本轮前后移植/原创的神器。 */
	private static final Class<?>[] NEW_ARTIFACTS = {
			EndlessAmmoBag.class, NaturalAxe.class, VoidHand.class, HandOfTheElder.class,
			HeartOfSatan.class, GoddessRadiance.class, CloakOfSheep.class
	};

	/** SPSEXPD: 同批新增的原创武器。 */
	private static final Class<?>[] NEW_WEAPONS = { TrinityForce.class, Thrower.class };

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		HeadlessApplication app = new HeadlessApplication(new ApplicationAdapter() {
			@Override public void create() { }
		}, new HeadlessApplicationConfiguration());
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		Messages.setup(Languages.CHI_SMPL);
		Random.pushGenerator(0x5350534445425547L);
		try {
			Generator.fullReset();
			testDebuggerContainsNewItems();
			System.out.println("SPS调试器测试通过：7 件新神器与 2 件新武器均登记在调试器物品清单并归入「装备」组。");
		} finally {
			Random.popGenerator();
			Actor.clear();
			Dungeon.hero = null;
			Dungeon.level = null;
			app.exit();
		}
	}

	@SuppressWarnings("unchecked")
	private static void testDebuggerContainsNewItems() throws Exception {
		Method groups = WndDebugItems.class.getDeclaredMethod("groups");
		groups.setAccessible(true);
		LinkedHashMap<String, ArrayList<Class<? extends Item>>> map =
				(LinkedHashMap<String, ArrayList<Class<? extends Item>>>) groups.invoke(null);

		for (Class<?> type : NEW_ARTIFACTS) assertListed(map, type);
		for (Class<?> type : NEW_WEAPONS) assertListed(map, type);
	}

	private static void assertListed(LinkedHashMap<String, ArrayList<Class<? extends Item>>> map, Class<?> type) {
		String found = null;
		for (java.util.Map.Entry<String, ArrayList<Class<? extends Item>>> entry : map.entrySet()) {
			if (entry.getValue().contains(type)) {
				found = entry.getKey();
				break;
			}
		}
		check(found != null, "调试器物品清单里没有 " + type.getSimpleName());
		check("装备".equals(found),
				type.getSimpleName() + " 没有归入调试器「装备」组，实际：" + found);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsDebugItemsTest() { }
}
