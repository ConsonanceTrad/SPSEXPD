/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs.pets;

import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.VelociroosterSprite;
import render.utils.Random;

public class Velocirooster extends PET {
	{
		spriteClass = VelociroosterSprite.class;
		baseSpeed = 1.5f;
		cooldown = 50;
		flying = false;
		properties.add(Property.BEAST);
		properties.add(Property.HUMAN);
		updateStats(true);
	}
	@Override protected Kind kind() { return Kind.VELOCIROOSTER; }
	@Override public void updateStats(boolean refill) {
		int old = HT;
		HT = 50 + petLevel() * 10;
		defenseSkill = 10 + petLevel();
		if (refill) HP = HT; else if (HT > old) HP = Math.min(HT, HP + HT - old);
	}
	@Override public int drRoll() { return Random.Int(Math.max(1, petLevel() + 1)); }
	@Override public int attackSkill(Char target) { return petLevel() + 10; }
	@Override public int damageRoll() {
		int low = 5 + petLevel();
		int high = 5 + petLevel() * 3;
		if (cooldown == 0) { low = low * 5 / 2; high *= 2; }
		return Random.NormalIntRange(low, Math.max(low, high));
	}
	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(4) == 0) damage = damage * 6 / 5;
		if (cooldown > 0) cooldown--;
		if (cooldown == 0 && enemy != null) charm(enemy, "yell1", Math.max(5, 30 - petLevel()));
		return super.attackProc(enemy, damage);
	}
	@Override public int defenseProc(Char enemy, int damage) {
		if (cooldown > 0) cooldown--;
		if (cooldown == 0 && enemy != null) charm(enemy, "yell2", 5);
		return super.defenseProc(enemy, damage);
	}
	private void charm(Char enemy, String message, int nextCooldown) {
		if (sprite != null) sprite.showStatus(CharSprite.NEUTRAL, Messages.get(this, message));
		Buff.affect(enemy, Charm.class, 5f).object = id();
		cooldown = nextCooldown;
	}
}
