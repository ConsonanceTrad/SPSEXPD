package pd.items.summon;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.npcs.DirectableAlly;
import pd.items.Item;
import pd.mechanics.Ballistica;
import pd.sprites.ItemSpriteSheet;
import pd.sprites.MrDestructo2dot0Sprite;
import pd.sprites.MrDestructoSprite;
import render.utils.Random;

public class ActiveMrDestructo extends SpsSummonItem {

	private static boolean activate;

	{
		image = ItemSpriteSheet.ACTIVE_MR_DESTRUCTO;
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
					? new MrDestructo2dot0() : new MrDestructo());
		}
		activate = false;
	}

	public static class MrDestructo extends DirectableAlly {
		private Ballistica beam;

		{
			spriteClass = MrDestructoSprite.class;
			HP = HT = 100;
			defenseSkill = 3;
			EXP = 0;
			properties.add(Property.INORGANIC);
			properties.add(Property.IMMOVABLE);
		}

		@Override
		protected boolean getCloser(int target) {
			return false;
		}

		@Override
		protected boolean canAttack(Char enemy) {
			beam = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
			return beam.subPath(1, beam.dist).contains(enemy.pos);
		}

		@Override
		public int attackSkill(Char target) {
			return 20 + Dungeon.legacyDepth();
		}

		protected int minimumDamage() {
			return Dungeon.legacyDepth();
		}

		protected int maximumDamage() {
			return Dungeon.legacyDepth() + 12;
		}

		protected int armorBreak() {
			return 30;
		}

		@Override
		protected boolean doAttack(Char enemy) {
			beam = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.attack(enemy.pos);
				return false;
			}
			beamAttack();
			Invisibility.dispel(this);
			spend(attackDelay());
			return true;
		}

		@Override
		public void onAttackComplete() {
			beamAttack();
			Invisibility.dispel(this);
			spend(attackDelay());
			next();
		}

		private void beamAttack() {
			if (enemy == null) return;
			if (beam == null) beam = new Ballistica(pos, enemy.pos, Ballistica.STOP_SOLID);
			for (int cell : beam.subPath(1, beam.dist)) {
				Char target = Actor.findChar(cell);
				if (target != null && target != this && hit(this, target, true)) {
					target.damage(Random.NormalIntRange(minimumDamage(), maximumDamage()), this);
					Buff.affect(target, ArmorBreak.class, 3f).level(armorBreak());
					damage(Random.NormalIntRange(5, 10), this);
					if (!isAlive()) break;
				}
			}
		}

		@Override
		public int damageRoll() {
			return Random.NormalIntRange(minimumDamage(), maximumDamage());
		}
	}

	public static class MrDestructo2dot0 extends MrDestructo {
		{
			spriteClass = MrDestructo2dot0Sprite.class;
			HP = HT = 200;
			defenseSkill = 35;
		}

		@Override
		public int attackSkill(Char target) {
			return 30 + Dungeon.legacyDepth();
		}

		@Override
		protected int minimumDamage() {
			return Dungeon.legacyDepth() + 20;
		}

		@Override
		protected int maximumDamage() {
			return Dungeon.legacyDepth() + 32;
		}

		@Override
		protected int armorBreak() {
			return 50;
		}
	}
}
