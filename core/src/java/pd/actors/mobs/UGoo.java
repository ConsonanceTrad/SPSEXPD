/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.blobs.SlowGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.effectblobs.ElectriShock;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Roots;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.items.equipment.weapon.enchantments.EnchantmentDark;
import pd.items.equipment.weapon.melee.special.Handcannon;
import pd.levels.BossRushLevel;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.UGooSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The four-element lord goo and its original elemental projections. */
public class UGoo extends BossRushBoss {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(UGoo.class)
			.t("name", "领主黏咕")
			.t("desc", "虚空中的一只黏咕投影。它可比这个世界的任何一只黏咕都要强。")
			.t("$earthgoo.name", "黏土黏咕")
			.t("$firegoo.name", "火焰黏咕")
			.t("$icegoo.name", "冰霜黏咕")
			.t("$shockgoo.name", "雷云黏咕");
	}




	{
		spriteClass = UGooSprite.class;
		baseSpeed = 0.25f;
		loot = new Handcannon();
		lootChance = 0.5f;
		properties.add(Property.ACIDIC);
		properties.add(Property.ELEMENT);
		properties.add(Property.UNKNOW);
		resistances.add(EnchantmentDark.class);
		immunities.add(EnchantmentDark.class);
	}

	@Override
	protected void onPhaseChanged(int phase) {
		if (!hasLivingMinions()) {
			spawnMinion(new EarthGoo());
			spawnMinion(new FireGoo());
			spawnMinion(new ShockGoo());
			spawnMinion(new IceGoo());
		}
	}

	protected void spawnMinion(Mob minion) {
		if (!(Dungeon.level instanceof BossRushLevel)) return;
		minion.pos = ((BossRushLevel) Dungeon.level).safeSpawnCell(pos);
		GameScene.add(minion);
	}

	protected boolean hasLivingMinions() {
		for (Mob mob : Dungeon.level.mobs()) if (mob instanceof ElementGoo && mob.isAlive()) return true;
		return false;
	}

	@Override
	public void damage(int damage, Object source) {
		if (hasLivingMinions()) damage = 0;
		super.damage(damage, source);
	}

	@Override
	public float speed() {
		return breaks == 3 ? 3f * super.speed() : super.speed();
	}

	@Override
	protected Class<? extends BossRushBoss> nextBoss() {
		return UTengu.class;
	}

	@Override
	public void die(Object cause) {
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (mob instanceof ElementGoo || mob instanceof Eye) mob.die(cause);
		}
		super.die(cause);
	}

	public abstract static class ElementGoo extends Mob {
		{
			HP = HT = 10;
			EXP = 0;
			defenseSkill = 5;
			baseSpeed = 0.75f;
			state = WANDERING;
			properties.add(Property.BOSS_MINION);
			properties.add(Property.ELEMENT);
			properties.add(Property.UNKNOW);
			properties.add(Property.MINIBOSS);
			resistances.add(ToxicGas.class);
			resistances.add(EnchantmentDark.class);
			immunities.add(Amok.class);
			immunities.add(Sleep.class);
			immunities.add(Terror.class);
			immunities.add(Vertigo.class);
		}
		@Override public int attackSkill(Char target) { return 10; }
		@Override public int damageRoll() { return Random.NormalIntRange(0, 1); }
		@Override public int drRoll() { return 2; }
	}

	public static class EarthGoo extends ElementGoo {
		{ spriteClass = UGooSprite.EarthSpawnSprite.class; immunities.add(Poison.class); immunities.add(ToxicGas.class); }
		@Override public int drRoll() { return 0; }
		@Override public int attackProc(Char enemy, int damage) {
			if (Random.Int(5) == 0) Buff.affect(enemy, Ooze.class).set(8f);
			if (Random.Int(5) == 0) Buff.prolong(enemy, Roots.class, 2f);
			return damage;
		}
	}

	public static class FireGoo extends ElementGoo {
		{ spriteClass = UGooSprite.FireSpawnSprite.class; properties.add(Property.FIERY); immunities.add(Burning.class); immunities.add(ScrollOfPsionicBlast.class); immunities.add(ToxicGas.class); }
		@Override protected boolean act() {
			for (int offset : PathFinder.NEIGHBOURS9) {
				int cell = pos + offset;
				if (Dungeon.level.insideMap(cell)) GameScene.add(Blob.seed(cell, 2, Fire.class));
			}
			return super.act();
		}
		@Override protected boolean canAttack(Char enemy) {
			if (buff(Silent.class) != null) return Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
			return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}
	}

	public static class IceGoo extends ElementGoo {
		{ spriteClass = UGooSprite.IceSpawnSprite.class; state = FLEEING; properties.add(Property.ICY); immunities.add(Poison.class); immunities.add(ToxicGas.class); immunities.add(SlowGas.class); }
		@Override protected boolean act() {
			for (int offset : PathFinder.NEIGHBOURS9) {
				int cell = pos + offset;
				if (Dungeon.level.insideMap(cell)) GameScene.add(Blob.seed(cell, 2, SlowGas.class));
			}
			return super.act();
		}
	}

	public static class ShockGoo extends ElementGoo {
		{ spriteClass = UGooSprite.ShockSpawnSprite.class; properties.add(Property.ELECTRIC); immunities.add(Burning.class); immunities.add(ScrollOfPsionicBlast.class); immunities.add(ElectriShock.class); }
		@Override public void damage(int damage, Object source) {
			GameScene.add(Blob.seed(pos, 5, ElectriShock.class));
			super.damage(damage, source);
		}
		@Override protected boolean canAttack(Char enemy) {
			if (buff(Silent.class) != null) return Dungeon.level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy);
			return new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}
	}
}
