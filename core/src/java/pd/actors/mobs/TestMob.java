/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.buffs.ShieldArmor;
import pd.actors.buffs.Sleep;
import pd.actors.buffs.Terror;
import pd.actors.buffs.Vertigo;
import pd.items.equipment.bags.HeartOfScarecrow;
import pd.sprites.ScarecrowSprite;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** The original passive damage-test scarecrow. */
public class TestMob extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(TestMob.class)
			.t("desc", "试试看，你能打多少伤害。")
			.t("name", "稻草人");
	}




	private static final String SKILL = "skill";
	private boolean skill;

	{
		spriteClass = ScarecrowSprite.class;
		HP = HT = 100000;
		defenseSkill = 0;
		state = PASSIVE;
		properties.add(Property.PLANT);
		immunities.add(Terror.class);
		immunities.add(Amok.class);
		immunities.add(Charm.class);
		immunities.add(Sleep.class);
		immunities.add(Vertigo.class);
	}

	@Override
	public int damageRoll() {
		return 10;
	}

	@Override
	public int attackSkill(Char target) {
		return 100;
	}

	@Override
	public int drRoll() {
		return 6;
	}

	@Override
	public void beckon(int cell) {
	}

	@Override
	public void die(Object cause) {
		super.die(cause);
		Dungeon.level.drop(new HeartOfScarecrow(), pos).sprite.drop();
		dropLegacyDew(pos);
		for (Mob mob : Dungeon.level.mobs().toArray(new Mob[0])) {
			if (mob instanceof TestMob && mob.isAlive()) {
				mob.HP += 10;
				Buff.affect(mob, ShieldArmor.class).level(1000);
			}
		}
	}

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(SKILL, skill);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		skill = bundle.getBoolean(SKILL);
	}
}
