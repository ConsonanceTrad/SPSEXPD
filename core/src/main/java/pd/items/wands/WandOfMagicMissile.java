/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.wands;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.MagicWeak;
import pd.items.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.ItemSpriteSheet;
import pd.ui.BuffIndicator;
import watabou.noosa.Image;
import watabou.utils.Bundle;

/** The original SPS-PD magic missile and magic-weakness wand. */
public class WandOfMagicMissile extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_SPS_MAGIC_MISSILE;
		collisionProperties = Ballistica.MAGIC_BOLT;
	}

	@Override public int min(int level) { return 2 + level; }
	@Override public int max(int level) { return 6 + 5 * level; }

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			target.damage((int)(damageRoll()
					* magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
			Buff.affect(target, MagicWeak.class, level());
			if (target.sprite != null) target.sprite.burst(0xFF99CCFF, 2);
		}
	}

	@Override public int initialCharges() { return 3; }

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// The Shattered battlemage charge effect is retained below for save compatibility only.
	}

	/** Retained so saves made before the SPS restoration can still load and expire this buff. */
	public static class MagicCharge extends FlavourBuff {
		{ type = buffType.POSITIVE; announced = true; }

		public static final float DURATION = 4f;
		private int level;
		private Wand wandJustApplied;

		public void setup(Wand wand) {
			if (level < wand.buffedLvl()) {
				level = wand.buffedLvl();
				wandJustApplied = wand;
			}
		}

		@Override public void detach() { super.detach(); updateQuickslot(); }
		public int level() { return level; }
		public Wand wandJustApplied() {
			Wand result = wandJustApplied;
			wandJustApplied = null;
			return result;
		}
		@Override public int icon() { return BuffIndicator.UPGRADE; }
		@Override public void tintIcon(Image icon) { icon.hardlight(0.2f, 0.6f, 1f); }
		@Override public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}
		@Override public String desc() { return Messages.get(this, "desc", level(), dispTurns()); }

		private static final String LEVEL = "level";
		@Override public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(LEVEL, level);
		}
		@Override public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			level = bundle.getInt(LEVEL);
		}
	}
}
