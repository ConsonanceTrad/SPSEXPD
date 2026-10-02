/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.actors.Char;
import pd.items.equipment.weapon.Weapon;
import pd.messages.Messages;
import pd.sprites.SentinelSprite;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The passive enchanted-weapon guardian from the legacy wisdom trial. */
public class Sentinel extends Statue {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Sentinel.class)
			.t("name", "魔法守卫")
			.t("desc", "手持_%s_的附魔守卫。它会静止不动，受到惊扰后便用所守护的武器攻击。");
	}



	private static final int LEGACY_DEPTH = 33;

	public Sentinel() {
		super();
		spriteClass = SentinelSprite.class;
		properties.add(Property.MECH);
		HP = HT = 15 + LEGACY_DEPTH * 8;
		defenseSkill = 4 + LEGACY_DEPTH * 2;
		EXP = 18;
		state = PASSIVE;
		createWeapon(false);
		weapon.identify();
		weapon.enchant(Weapon.Enchantment.random());
		for (int i = 0; i < 3; i++) weapon.upgrade(true);
	}

	@Override public int attackSkill(Char target) {
		return (int)((9 + LEGACY_DEPTH) * weapon.accuracyFactor(this, target));
	}

	@Override public int drRoll() {
		return Random.NormalIntRange(0, LEGACY_DEPTH);
	}

	@Override public void beckon(int cell) { }

	@Override public void die(Object cause) {
		dropLegacyDew(pos);
		super.die(cause);
	}

	@Override public String description() {
		return Messages.get(this, "desc", weapon.name());
	}
}
