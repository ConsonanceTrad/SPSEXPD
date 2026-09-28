package com.shatteredpixel.shatteredpixeldungeon.items.summon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FairySprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SugarplumFairySprite;
import com.watabou.utils.Random;

public class FairyCard extends SpsSummonItem {

	private static boolean activate;

	{
		image = ItemSpriteSheet.FAIRY_CARD;
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
					? new SugarplumFairy() : new Fairy());
		}
		activate = false;
	}

	public static class Fairy extends DirectableAlly {
		{
			spriteClass = FairySprite.class;
			HP = HT = 200;
			defenseSkill = 0;
			EXP = 0;
		}

		@Override
		protected boolean act() {
			damage(1, this);
			if (!isAlive()) return true;
			if (Dungeon.level.adjacent(pos, Dungeon.hero.pos)) healHero();
			return super.act();
		}

		protected void healHero() {
			Dungeon.hero.HP = Math.min(Dungeon.hero.HT, Dungeon.hero.HP + 5);
			Dungeon.hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
			Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, "5");
		}

		@Override
		protected Char chooseEnemy() {
			return this instanceof SugarplumFairy ? super.chooseEnemy() : null;
		}

		@Override
		public int damageRoll() {
			return 0;
		}

		@Override
		public int attackSkill(Char target) {
			return 0;
		}
	}

	public static class SugarplumFairy extends Fairy {
		{
			spriteClass = SugarplumFairySprite.class;
			HP = HT = 400;
			defenseSkill = 20;
		}

		@Override
		protected void healHero() {
			Dungeon.hero.HP = Math.min(Dungeon.hero.HT * 2, Dungeon.hero.HP + 5);
			Dungeon.hero.sprite.emitter().start(Speck.factory(Speck.HEALING), 0.4f, 1);
			Dungeon.hero.sprite.showStatus(CharSprite.POSITIVE, "5");
		}

		@Override
		protected boolean canAttack(Char enemy) {
			return Dungeon.level.distance(pos, enemy.pos) <= 4;
		}

		@Override
		public int attackSkill(Char target) {
			return 60 + Dungeon.legacyDepth();
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(Dungeon.legacyDepth() + 12, Dungeon.legacyDepth() + 25);
		}
	}
}
