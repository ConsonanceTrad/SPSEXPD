package pd.items;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.mobs.SpsSewerMobs;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.quest.AdventureJournal;
import pd.items.sellitem.DevUpPlan;
import pd.scenes.MemorySaveScene;
import pd.sprites.ItemSpriteSheet;
import watabou.utils.Bundle;

/** Headless checks for the two legacy item-driven memory-save routes. */
public final class SpsMemoryItemsTest {

	public static void main(String[] args) {
		try {
			testSaveItems();
			testRewardRoutes();
			System.out.println("SPS记忆道具测试通过：动作、场景、消耗规则、NPC奖励与鼠王奖励均正常。");
		} finally {
			Dungeon.hero = null;
		}
	}

	private static void testSaveItems() {
		Hero hero = new Hero();
		PuddingCup pudding = new PuddingCup();
		SaveYourLife device = new SaveYourLife();
		check(pudding.actions(hero).contains("SAVE"), "布丁杯缺少记忆动作");
		check(device.actions(hero).contains("SAVE"), "紧急离线装置缺少离线动作");
		check(PuddingCup.saveScene() == MemorySaveScene.class
				&& SaveYourLife.saveScene() == MemorySaveScene.class,
				"记忆道具没有进入记忆槽场景");
		hero.belongings.backpack.items.add(pudding);
		pudding.consumeForSave(hero);
		check(!hero.belongings.backpack.items.contains(pudding), "布丁杯记忆后没有被消耗");
		check(device.image == ItemSpriteSheet.SAVE_YOUR_LIFE && device.isIdentified()
				&& !device.isUpgradable() && device.unique,
				"紧急离线装置图标或基础属性错误");
	}

	private static void testRewardRoutes() {
		Hero hero = new Hero();
		Dungeon.hero = hero;
		TownNpc author = new TownNpc().configure(TownNpc.Spec.HMDZL001);
		check(author.SupercreateLoot() instanceof DevUpPlan,
				"未救出奥蒂卢克时作者NPC没有返回更新计划");
		DevUpPlan plan = (DevUpPlan)author.SupercreateLoot();
		plan.quantity(3);
		check(plan.value() == 1500 && plan.isIdentified() && !plan.isUpgradable(),
				"更新计划售价、鉴定或升级属性错误");

		AdventureJournal journal = new AdventureJournal();
		Bundle saved = new Bundle();
		saved.put("completed", 1 << 7);
		journal.restoreFromBundle(saved);
		hero.belongings.backpack.items.add(journal);
		check(author.SupercreateLoot() instanceof SaveYourLife,
				"救出奥蒂卢克后作者NPC没有返回紧急离线装置");

		pd.actors.mobs.RatBoss ratBoss =
				new pd.actors.mobs.RatBoss();
		check(ratBoss.SupercreateLoot() instanceof SaveYourLife,
				"鼠王的特殊奖励不是紧急离线装置");
		check(ratBoss.properties().contains(Char.Property.BEAST)
				&& ratBoss.properties().contains(Char.Property.BOSS),
				"鼠王缺少野兽或首领属性");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsMemoryItemsTest() { }
}
