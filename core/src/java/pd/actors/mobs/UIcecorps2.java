/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.actors.Char;
import pd.actors.buffs.BoxStar;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Frost;
import pd.actors.buffs.StoneIce;
import pd.items.consum.eggs.EasterEgg;
import pd.sprites.IceRabbit2Sprite;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

/** FrostNova's invulnerable opening phase and faster final form. */
public class UIcecorps2 extends UIcecorps {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(UIcecorps2.class)
			.t("name", "术士冬痕-终结形态")
			.t("desc", "兔人术士燃烧仅存的生命力，准备完成最后一战。");
	}

	private int shieldTurns = 30;

	{
		spriteClass = IceRabbit2Sprite.class;
		baseSpeed = 1.5f;
		HP = HT = 1500;
		EXP = 20;
		loot = new EasterEgg();
		lootChance = 1f;
	}

	@Override protected boolean act() {
		if (shieldTurns > 1) {
			Buff.prolong(this, BoxStar.class, 3f);
			shieldTurns--;
		}
		return super.act();
	}

	@Override public int attackProc(Char enemy, int damage) {
		if (Random.Int(2) == 0) Buff.affect(enemy, StoneIce.class).level(3f);
		enemy.damage(damageRoll() * 3 / 4, Frost.class);
		return damage / 4;
	}

	@Override protected Class<? extends BossRushBoss> nextBoss() { return UYog.class; }

	private static final String SHIELD = "shield";
	@Override public void storeInBundle(Bundle bundle) { super.storeInBundle(bundle); bundle.put(SHIELD, shieldTurns); }
	@Override public void restoreFromBundle(Bundle bundle) { super.restoreFromBundle(bundle); shieldTurns = bundle.contains(SHIELD) ? bundle.getInt(SHIELD) : 30; }
}
