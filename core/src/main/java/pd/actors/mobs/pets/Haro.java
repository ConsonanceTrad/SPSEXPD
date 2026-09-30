/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.HolyStun;
import pd.actors.buffs.Recharging;
import pd.sprites.HaroSprite;
import render.utils.math.Random;

public class Haro extends PET {
	{
		spriteClass = HaroSprite.class;
		cooldown = 10;
		flying = false;
		properties.add(Property.MECH);
		updateStats(true);
	}
	@Override protected Kind kind() { return Kind.HARO; }
	@Override public void updateStats(boolean refill) {
		int old = HT;
		HT = 70 + petLevel() * 5;
		defenseSkill = petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int drRoll() { return Random.IntRange(8 + petLevel(), 10 + petLevel()); }
	@Override public int attackSkill(Char target) { return petLevel() + 20; }
	@Override public int damageRoll() { return Random.NormalIntRange(5, 10 + petLevel() * 4); }
	@Override public void move(int step, boolean travelling) {
		super.move(step, travelling);
		if (cooldown > 0) cooldown--;
	}
	@Override public int attackProc(Char enemy, int damage) {
		if (Dungeon.hero != null) {
			Dungeon.hero.belongings.reloadGuns();
			Buff.affect(Dungeon.hero, Recharging.class, 2f);
		}
		return super.attackProc(enemy, damage);
	}
	@Override public int defenseProc(Char enemy, int damage) {
		if (cooldown == 0 && enemy != null) {
			Buff.affect(enemy, HolyStun.class, 5f);
			cooldown = Math.max(10, 50 - petLevel());
		}
		return super.defenseProc(enemy, damage);
	}
}
