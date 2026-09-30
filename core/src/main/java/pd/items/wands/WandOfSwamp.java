/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.wands;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SwampGas;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.items.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import render.noosa.audio.Sample;
import render.utils.Callback;

/** The direct-damage swamp-gas wand from SPS-PD 0.9.8. */
public class WandOfSwamp extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_POISON;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public int min(int lvl) {
		return lvl;
	}

	@Override
	public int max(int lvl) {
		return 8 + 4 * lvl;
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			target.damage((int) (damageRoll() * magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
		}

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.earthhit();

		GameScene.add(Blob.seed(bolt.collisionPos, 100, SwampGas.class));
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.POISON,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
