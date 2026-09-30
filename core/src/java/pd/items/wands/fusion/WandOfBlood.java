package pd.items.wands.fusion;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.items.wands.DamageWand;
import pd.items.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.sprites.ItemSpriteSheet;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;

/** The original SPS-PD blood wand, kept in the fusion package for save compatibility. */
public class WandOfBlood extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_BLOOD;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override public int min(int level) { return level; }
	@Override public int max(int level) { return 6 + 2 * level; }

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			target.damage((int)(damageRoll() * magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
			if (curUser.HP < curUser.HT) {
				int healing = Random.Int(0, damageRoll());
				curUser.HP += Math.min(healing, curUser.HT - curUser.HP);
			}
		}

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.darkhit();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.SHADOW,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
