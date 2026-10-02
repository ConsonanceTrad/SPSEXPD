/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.effects.CellEmitter;
import pd.effects.MagicMissile;
import pd.effects.Speck;
import pd.effects.particles.SmokeParticle;
import pd.items.Heap;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import pd.scenes.PixelScene;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The direct-hit plus 3x3 meteor explosion from SPS-PD 0.9.8. */
public class WandOfMeteorite extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfMeteorite.class)
			.t("name", "陨星法杖")
			.t("desc", "这根火属性法杖由一种陨石制成，嵌有金饰并在顶部镶着一颗浑圆的黑陨石。它在你手中的感觉非常沉重。")
			.t("stats_desc", "该法杖会在目标位置召唤陨石，撞击造成_%1$d~%2$d点伤害_，随后对3×3范围再造成一次较低伤害。撞击可能使目标麻痹，并烧焦可燃地形。");
	}


	{
		image = SpecificPlaceHolderDict.SOMETHING_0;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override public int min(int lvl) { return lvl; }
	@Override public int max(int lvl) { return 12 + 6 * lvl; }

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	public static int splashDamage(int damageRoll, int magicSkill, int charges) {
		return (int) (damageRoll * magicSkillMultiplier(magicSkill) * charges / 9f);
	}

	public static int paralysisDurationMax(int lvl) {
		return Math.max(5, lvl);
	}

	@Override
	public void onZap(Ballistica bolt) {
		int center = bolt.collisionPos;
		Heap heap = Dungeon.level.heaps.get(center);
		if (heap != null) heap.firehit();
		Sample.INSTANCE.play(Assets.Sounds.ROCKS);

		Char target = Actor.findChar(center);
		if (target != null) {
			if (target.sprite != null) target.sprite.flash();
			wandProc(target, chargesPerCast());
			target.damage((int) (damageRoll() * magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
			if (target.isAlive() && Random.Int(2) == 0) {
				Buff.prolong(target, Paralysis.class,
						Random.IntRange(5, paralysisDurationMax(level())));
			}
			CellEmitter.get(center).start(Speck.factory(Speck.ROCK), 0.07f, 5);
			PixelScene.shake(3, 0.21f);
		}

		int width = Dungeon.level.width();
		int cx = center % width;
		int cy = center / width;
		for (int dx = -1; dx <= 1; dx++) {
			for (int dy = -1; dy <= 1; dy++) {
				int x = cx + dx;
				int y = cy + dy;
				if (x < 0 || x >= width || y < 0 || y >= Dungeon.level.height()) continue;
				int cell = x + y * width;
				if (Dungeon.level.heroFOV[cell]) {
					CellEmitter.get(cell).burst(SmokeParticle.FACTORY, 2);
				}
				if (Dungeon.level.insideMap(cell)
						&& (Dungeon.level.flamable[cell] || Dungeon.level.map[cell] == Terrain.GLASS_WALL)) {
					Level.set(cell, Terrain.EMBERS);
					GameScene.updateMap(cell);
				}

				Char splashTarget = Actor.findChar(cell);
				if (splashTarget != null) {
					int damage = splashDamage(damageRoll(), Dungeon.hero.magicSkill(), chargesPerCast());
					if (damage > 0) splashTarget.damage(damage, this);
				}
			}
		}
	}

	@Override public int initialCharges() { return 2; }
	@Override protected int chargesPerCast() { return 1; }

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.METEORITE,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
