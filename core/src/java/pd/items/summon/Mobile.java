package pd.items.summon;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.sprites.ExMobileSprite;
import pd.sprites.MobileSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Mobile extends SpsSummonItem {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(Mobile.class)
			.t("name", "遥控卫星")
			.t("ac_active", "使用")
			.t("desc", "用于呼叫一个小型闪电卫星。")
			.t("mobilesatellite.name", "蓝色卫星")
			.t("mobilesatellite.desc", "这个东西貌似出现在外星飞船中，速度很快但寿命有限。")
			.t("exmobilesatellite.name", "红色卫星")
			.t("exmobilesatellite.desc", "由领袖改制的蓝色卫星，有更强的耐久、更高的射速和伤害。");
	}


	private static boolean activate;

	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
	}

	@Override
	public void execute(Hero hero, String action) {
		activate = AC_ACTIVE.equals(action);
		if (activate) beginActivation(hero);
		else super.execute(hero, action);
	}

	@Override
	protected void onThrow(int cell) {
		if (!activate || Dungeon.level.pit[cell]) {
			super.onThrow(cell);
		} else {
			summonOrRecover(cell, Dungeon.hero.subClass == HeroSubClass.LEADER
					? new EXMobileSatellite() : new MobileSatellite());
		}
		activate = false;
	}

	public static class MobileSatellite extends DirectableAlly {
		{
			spriteClass = MobileSprite.class;
			HP = HT = 100;
			defenseSkill = 0;
			baseSpeed = 2f;
			EXP = 0;
			properties.add(Property.INORGANIC);
		}

		@Override
		protected boolean act() {
			damage(1, this);
			return isAlive() && super.act();
		}

		@Override
		protected boolean canAttack(Char enemy) {
			return Dungeon.level.distance(pos, enemy.pos) <= 6;
		}

		@Override
		public float attackDelay() {
			return 0.33f;
		}

		@Override
		public int attackSkill(Char target) {
			return 30 + Dungeon.legacyDepth();
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(HP / 8 + 5, HP / 2 + 10);
		}
	}

	public static class EXMobileSatellite extends MobileSatellite {
		{
			spriteClass = ExMobileSprite.class;
			HP = HT = 300;
			defenseSkill = 35;
		}

		@Override
		public float attackDelay() {
			return 0.25f;
		}

		@Override
		public int attackSkill(Char target) {
			return 60 + Dungeon.legacyDepth();
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(HP / 6 + 10, HP / 2 + 20);
		}
	}
}
