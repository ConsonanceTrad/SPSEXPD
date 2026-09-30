package pd.items.summon;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.effects.Speck;
import pd.sprites.CharSprite;
import pd.sprites.FairySprite;
import pd.sprites.ItemSpriteSheet;
import pd.sprites.SugarplumFairySprite;
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
