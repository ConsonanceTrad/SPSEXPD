/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.wands;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Ooze;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.items.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.audio.Sample;
import watabou.utils.Callback;
import watabou.utils.Random;

/** The direct-damage acid wand from SPS-PD 0.9.8. */
public class WandOfAcid extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_ACID;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public int min(int lvl) {
		return 2 + lvl;
	}

	@Override
	public int max(int lvl) {
		return 6 + 4 * lvl;
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	static Class<Ooze> oozeEffectClass() {
		return Ooze.class;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.earthhit();

		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			if (target.isAlive() && Random.Int(2) == 0) {
				Buff.affect(target, oozeEffectClass()).set(level());
			}
			target.damage((int) (damageRoll() * magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
		}
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.FOLIAGE,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
