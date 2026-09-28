package com.shatteredpixel.shatteredpixeldungeon.items.summon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ExMobileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobileSprite;
import com.watabou.utils.Random;

public class Mobile extends SpsSummonItem {

	private static boolean activate;

	{
		image = ItemSpriteSheet.MOBILE;
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
