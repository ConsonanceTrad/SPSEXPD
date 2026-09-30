/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.wands;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.Beam;
import pd.effects.CellEmitter;
import pd.effects.particles.PurpleParticle;
import pd.items.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.sprites.ItemSpriteSheet;
import pd.tiles.DungeonTilemap;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;

import java.util.ArrayList;

/** The short-range, obstacle-piercing disintegration wand from SPS-PD 0.9.8. */
public class WandOfDisintegration extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_SPS_DISINTEGRATION;
		collisionProperties = Ballistica.WONT_STOP;
	}

	@Override public int min(int level) { return 2 + level; }
	@Override public int max(int level) { return 8 + 4 * level; }

	public static int maxDistance(int level) {
		return Math.min(8, level + 2);
	}

	public static int damageLevel(int level, int targets, int terrainBonus) {
		return Math.max(1, level + Math.max(0, targets - 1) + terrainBonus);
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica beam) {
		int maximum = Math.min(maxDistance(level()), beam.dist);
		ArrayList<Char> targets = new ArrayList<>();
		int terrainPassed = 2;
		int terrainBonus = 0;

		for (int cell : beam.subPath(1, maximum)) {
			Char target = Actor.findChar(cell);
			if (target != null) {
				terrainBonus += terrainPassed / 3;
				terrainPassed %= 3;
				targets.add(target);
			}
			if (Dungeon.level.solid[cell]) terrainPassed++;
			CellEmitter.center(cell).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
		}

		int damageLevel = damageLevel(level(), targets.size(), terrainBonus);
		for (Char target : targets) {
			wandProc(target, chargesPerCast());
			target.damage((int)(damageRoll(damageLevel)
					* magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
			if (target.sprite != null) {
				target.sprite.centerEmitter().burst(PurpleParticle.BURST, Random.IntRange(1, 2));
				target.sprite.flash();
			}
		}
	}

	@Override
	public void fx(Ballistica beam, Callback callback) {
		int cell = beam.path.get(Math.min(beam.dist, maxDistance(level())));
		curUser.sprite.parent.add(new Beam.DeathRay(curUser.sprite.center(),
				DungeonTilemap.tileCenterToWorld(cell)));
		Sample.INSTANCE.play(Assets.Sounds.RAY);
		callback.call();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}
}
