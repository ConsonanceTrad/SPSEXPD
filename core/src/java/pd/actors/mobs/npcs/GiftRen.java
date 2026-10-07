/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.npcs;

import pd.actors.hero.Hero;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.armor.specialarmor.RenBArmor;
import render.utils.math.Random;
import pd.messages.InlineText;

public class GiftRen extends GiftNpc {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GiftRen.class)
			.t("desc", "看起来REN似乎等待着有人送他一些来自这座地牢的礼物。")
			.t("name", "等待礼物的REN")
			.t("normal", "你好，请问有什么事情吗？我正在收集一些存在于这座地牢的物件，我想将它们珍藏在我的某个世界里……嚄，最好是很有特征的宝物！")
			.t("yell1", "有什么问题吗？")
			.t("yell2", "我喜欢来自地牢的礼物！")
			.t("yell3", "我有些喜欢你了。")
			.t("yell4", "看起来这座地牢还有不少好东西！")
			.t("thank1", "成色不错！非常开门。")
			.t("thank2", "嚄，谢谢……")
			.t("thank3", "哦哟……谢谢！")
			.t("reward1", "这是别的世界的钱币，交换给你！")
			.t("reward2", "我在附近捡到的闪闪发光的东西，给。")
			.t("reward3", "我收集了很多这些，给你一点！")
			.t("reward4", "回给你这个！")
			.t("reward5", "嚄……谢谢你一直以来的礼物。穿上这件衣服之后，能够“防止”你被其他世界的REN袭击。");
	}



	{
		properties.add(Property.ELF);
	}
	@Override public Visual visual() { return Visual.REN; }
	@Override public boolean acceptsGift(Item item) {
		return named(item, "MiniMoai", "HugeShuriken", "ActiveMrDestructo", "Mobile", "ToyGun",
				"TrickSand", "MirrorDoll", "WindBottle", "HandLight", "CurseBox", "HolyWater",
				"PrayerWheel", "Triangolo", "Flute", "Wardrum", "Trumpet", "Harp", "Club",
				"RunicBlade", "Rapier", "Lance", "AresSword", "CromCruachAxe", "JupitersWraith",
				"LokisFlail", "NeptunusTrident", "WandOfFlock", "WandOfFlow", "WandOfTCloud",
				"StoneArmor", "CeramicsArmor", "ProtectiveclothingArmor", "MachineArmor",
				"StyrofoamArmor", "Strawberry", "Cherry", "Nut", "PerfectFood", "BlueMilk",
				"DeathCap", "Earthstar", "JackOLantern", "PixieParasol", "GoldenJelly",
				"GreenSpore", "RingOfKnowledge", "ScrollOfRegrowth");
	}
	@Override protected GiftResult reward(Hero hero) {
		if (friendship() == 100) return result("reward5", new RenBArmor());
		if (friendship() % 20 == 0) return result("reward" + Random.IntRange(1, 4),
				Generator.random(Generator.Category.NORNSTONE));
		return result("thank" + Random.IntRange(1, 3));
	}
}
