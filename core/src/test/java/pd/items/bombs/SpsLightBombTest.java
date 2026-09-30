package pd.items.bombs;

import pd.actors.Char;
import pd.actors.mobs.AdultDragonViolet;
import pd.actors.mobs.DM300;
import pd.actors.mobs.Goo;
import pd.actors.mobs.MonsterBox;
import pd.actors.mobs.Rat;
import pd.actors.mobs.pets.BlueDragon;

/** Headless checks for the SPS-PD 0.9.8 LightBomb creature-family mapping. */
public final class SpsLightBombTest {
	private SpsLightBombTest() { }

	public static void main(String[] args) {
		checkHeavy(new DM300(), "机械");
		checkHeavy(new MonsterBox(), "未知物体");
		checkHeavy(new Goo(), "酸性元素");
		check(!LightBomb.dealsHeavyDamageTo(new pd.actors.mobs.IceBug()),
				"旧版冰足虫只有野兽阵营，不应进入高伤害分支");
		checkHeavy(new AdultDragonViolet(), "敌对龙");
		checkHeavy(new BlueDragon(), "龙宠");

		TaggedChar fiery = new TaggedChar(Char.Property.FIERY);
		TaggedChar electric = new TaggedChar(Char.Property.ELECTRIC);
		TaggedChar undead = new TaggedChar(Char.Property.UNDEAD);
		TaggedChar unknown = new TaggedChar(Char.Property.UNKNOW);
		TaggedChar mech = new TaggedChar(Char.Property.MECH);
		TaggedChar element = new TaggedChar(Char.Property.ELEMENT);
		checkHeavy(fiery, "火元素");
		checkHeavy(electric, "电元素");
		checkHeavy(undead, "亡灵");
		checkHeavy(unknown, "未知");
		checkHeavy(mech, "机械");
		checkHeavy(element, "旧版元素");
		check(!LightBomb.dealsHeavyDamageTo(new Rat()), "普通生物被错误归入高伤害分支");
		check(!LightBomb.dealsHeavyDamageTo(new TaggedChar(Char.Property.PLANT)), "植物被错误归入高伤害分支");

		System.out.println("SPS圣光炸弹测试通过：旧版未知、机械、元素、龙、亡灵及首领属性映射均正常。");
	}

	private static void checkHeavy(Char target, String family) {
		check(LightBomb.dealsHeavyDamageTo(target), family + "未进入圣光炸弹高伤害分支");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private static final class TaggedChar extends Char {
		TaggedChar(Property property) { properties.add(property); }
		@Override public int damageRoll() { return 0; }
		@Override public int attackSkill(Char target) { return 0; }
		@Override public int drRoll() { return 0; }
	}
}
