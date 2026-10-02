/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Item;
import pd.items.equipment.bombs.DungeonBomb;
import pd.messages.InlineText;

public class GiftCoconut extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftCoconut.class)
			.t("desc", "一只正在测试各种东西的橙色猫。")
			.t("name", "实地实验的椰子")
			.t("normal", "你好啊，有什么事吗？")
			.t("yell1", "想吃蛋糕啊。")
			.t("yell2", "还有啥黑科技呢？")
			.t("yell3", "我的小机器人去哪了？")
			.t("yell4", "实验感觉如何，我这儿炸弹管够。")
			.t("thank1", "谢了。")
			.t("reward1", "新鲜的炸弹在此。")
			.t("reward2", "这是新的命中药水，尝一下吧。")
			.t("reward3", "这是新的闪避凝胶，擦一下吧。")
			.t("reward4", "这是新的法强发蜡，涂一下吧。")
			.t("reward5", "这是杠铃，这是火堆，来，举重吧。");
	}

	{ properties.add(Property.MECH); properties.add(Property.BEAST); }
	@Override public Visual visual() { return Visual.COCONUT; }
	@Override public boolean acceptsGift(Item item) {
		return named(item, "NutCake", "AlienBag", "CocoCatEgg", "GoldAmmo", "PotionOfMixing",
				"TestArmor", "TestWeapon", "WandOfTest", "TestCloak");
	}
	@Override protected GiftResult reward(Hero hero) {
		switch (friendship()) {
			case 30: hero.improveAttackSkill(1); return result("reward2");
			case 50: hero.improveDefenseSkill(1); return result("reward3");
			case 70: hero.improveMagicSkill(1); return result("reward4");
			case 100: hero.STR++; return result("reward5");
			default:
				return friendship() % 40 == 0 ? result("reward1", new DungeonBomb()) : result("thank1");
		}
	}
}
