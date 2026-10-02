/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Fire;
import pd.actors.blobs.ToxicGas;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Charm;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Frost;
import pd.actors.buffs.GlassShield;
import pd.actors.buffs.Poison;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.actors.hero.Hero;
import pd.effects.CellEmitter;
import pd.effects.particles.ElmoParticle;
import pd.items.Heap;
import pd.items.KindOfWeapon;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.armor.normalarmor.BaseArmor;
import pd.items.equipment.armor.normalarmor.RubberArmor;
import pd.items.equipment.armor.normalarmor.WoodenArmor;
import pd.items.specific.journalpages.Vault;
import pd.items.consum.scrolls.ScrollOfPsionicBlast;
import pd.items.equipment.wands.fusion.WandOfFlow;
import pd.items.equipment.weapon.melee.normalweapon.FightGloves;
import pd.items.equipment.weapon.melee.normalweapon.Knuckles;
import pd.items.equipment.weapon.melee.special.FireCracker;
import pd.items.equipment.weapon.missiles.MoneyPack;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.BeastYearSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.Calendar;
import pd.messages.InlineText;

/** The roaming year beast summoned by YearFood on depth 25. */
public class YearBeast extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(YearBeast.class)
			.t("name", "年兽")
			.t("disarm", "年兽解除了你的武装！")
			.t("desc", "传说中的生物，有着人类的智慧，虎的敏捷，熊的力量，凤的魅力。当然，这一切都是传说而已。")
			.t("notice", "我自由啦！")
			.t("die", "居然能战胜我！这是给你的奖励。")
			.t("escape", "现在时候不对，我先溜了。");
	}


	private static final float SPAWN_DELAY = 1f;
	private static final String TIMES = "times";
	private int times;

	{
		spriteClass = BeastYearSprite.class;
		baseSpeed = 1.5f;
		HP = HT = 1000;
		EXP = 0;
		defenseSkill = 30;
		viewDistance = 6;
		flying = true;
		resistances.add(ToxicGas.class);
		resistances.add(Poison.class);
		resistances.add(ScrollOfPsionicBlast.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(Terror.class);
		immunities.add(Fire.class);
		immunities.add(Vertigo.class);
		immunities.add(Burning.class);
	}

	@Override
	protected boolean act() {
		times++;
		if (Dungeon.level != null) {
			for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
				Buff.affect(mob, Burning.class).reignite(mob, 3f);
			}
			for (int offset : PathFinder.NEIGHBOURS9) {
				int cell = pos + offset;
				if (Dungeon.level.insideMap(cell)) GameScene.add(pd.actors.blobs.Blob.seed(cell, 2, Fire.class));
			}
		}
		return super.act();
	}

	@Override public int damageRoll() { return Random.NormalIntRange(40, 60); }
	@Override public int attackSkill(Char target) { return 40; }
	@Override protected boolean canAttack(Char enemy) { return Dungeon.level.distance(pos, enemy.pos) <= 2; }

	@Override
	public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) Buff.affect(enemy, Burning.class).reignite(enemy, 6f);
		else Buff.affect(enemy, Frost.class, Frost.DURATION);
		if (Random.Int(5) == 0) Buff.affect(enemy, Charm.class, 4f).object = id();
		if (Random.Int(5) == 0) {
			int opposite = enemy.pos + enemy.pos - pos;
			Ballistica trajectory = new Ballistica(enemy.pos, opposite, Ballistica.MAGIC_BOLT);
			WandOfFlow.throwChar(enemy, trajectory, 1);
			Buff.affect(enemy, Vertigo.class, 3f);
		}
		if (enemy == Dungeon.hero && Random.Int(10) == 0) disarm(Dungeon.hero);
		return damage;
	}

	private void disarm(Hero hero) {
		if (Random.Int(2) == 0) {
			KindOfWeapon weapon = hero.belongings.weapon();
			if (weapon != null && !weapon.cursed && !(weapon instanceof Knuckles)
					&& !(weapon instanceof FightGloves)) {
				hero.belongings.weapon = null;
				Dungeon.quickslot.clearItem(weapon);
				weapon.updateQuickslot();
				Dungeon.level.drop(weapon, hero.pos).sprite.drop();
				GLog.w(Messages.get(this, "disarm"));
			}
		} else {
			Armor armor = hero.belongings.armor();
			if (armor != null && !armor.cursed && !(armor instanceof WoodenArmor)
					&& !(armor instanceof RubberArmor) && !(armor instanceof BaseArmor)) {
				hero.belongings.armor = null;
				Dungeon.level.drop(armor, hero.pos).sprite.drop();
				GLog.w(Messages.get(this, "disarm"));
			}
		}
	}

	@Override
	public int defenseProc(Char enemy, int damage) {
		if (damage > 200 && buff(GlassShield.class) == null) Buff.affect(this, GlassShield.class).turns(3);
		Buff.prolong(this, DefenceUp.class, 3f).level(20);
		Buff.prolong(this, AttackUp.class, 3f).level(20);
		return super.defenseProc(enemy, damage);
	}

	@Override
	public void damage(int damage, Object source) {
		if (source instanceof FireCracker || source instanceof MoneyPack) times = 0;
		super.damage(damage, source);
	}

	@Override
	public void die(Object cause) {
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		if (times >= (month < 3 ? 1000 : 5)) {
			yell(Messages.get(this, "escape"));
		} else {
			yell(Messages.get(this, "die"));
			if (Dungeon.level != null) {
				Heap heap = Dungeon.level.drop(new Vault(), pos);
				if (heap.sprite != null) heap.sprite.drop();
			}
		}
		times = 0;
		destroy();
		if (sprite != null) {
			sprite.killAndErase();
			CellEmitter.get(pos).burst(ElmoParticle.FACTORY, 6);
		}
	}

	public static YearBeast spawnAt(int pos) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(pos)
				|| !Dungeon.level.passable[pos] || Actor.findChar(pos) != null) return null;
		YearBeast beast = new YearBeast();
		beast.pos = pos;
		beast.state = beast.HUNTING;
		GameScene.add(beast, SPAWN_DELAY);
		return beast;
	}

	@Override public void notice() { super.notice(); yell(Messages.get(this, "notice")); }
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(TIMES, times); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); times = bundle.getInt(TIMES); }
	public int elapsedTurns() { return times; }
}
