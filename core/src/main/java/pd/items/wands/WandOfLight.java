/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.wands;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Light;
import pd.effects.Beam;
import pd.effects.Speck;
import pd.effects.particles.RainbowParticle;
import pd.effects.particles.ShadowParticle;
import pd.items.Heap;
import pd.items.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.sprites.ItemSpriteSheet;
import pd.tiles.DungeonTilemap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

/** The wall-piercing light wand from SPS-PD 0.9.8. */
public class WandOfLight extends DamageWand {

	{
		image = ItemSpriteSheet.WAND_LIGHT;
		collisionProperties = Ballistica.STOP_CHARS;
	}

	@Override
	public int min(int lvl) {
		return 3 + lvl;
	}

	@Override
	public int max(int lvl) {
		return 6 + 4 * lvl;
	}

	public static boolean blindRollSucceeds(int roll, int lvl) {
		return roll >= 3 && roll < 5 + lvl;
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	public static int damageAgainst(int damage, Char target) {
		return Char.hasProp(target, Char.Property.DEMONIC) || Char.hasProp(target, Char.Property.UNDEAD)
				? Math.round(damage * 1.333f) : damage;
	}

	@Override
	public void onZap(Ballistica beam) {
		Char target = Actor.findChar(beam.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			affectTarget(target);

			if (blindRollSucceeds(Random.Int(5 + level()), level())) {
				Buff.prolong(target, Blindness.class, 2f + level() * 0.34f);
				if (target.sprite != null) target.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6);
			}
			Buff.prolong(curUser, Light.class, 5f + level());
		}

		Heap heap = Dungeon.level.heaps.get(beam.collisionPos);
		if (heap != null) heap.lighthit();
	}

	private void affectTarget(Char target) {
		int damage = (int) (damageRoll() * magicSkillMultiplier(Dungeon.hero.magicSkill()));
		if (blindRollSucceeds(Random.Int(5 + level()), level())) {
			Buff.prolong(target, Blindness.class, 2f + level() * 0.333f);
			if (target.sprite != null) target.sprite.emitter().burst(Speck.factory(Speck.LIGHT), 6);
		}

		if (Char.hasProp(target, Char.Property.DEMONIC) || Char.hasProp(target, Char.Property.UNDEAD)) {
			if (target.sprite != null) target.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10 + level());
			Sample.INSTANCE.play(Assets.Sounds.BURNING);
		} else if (target.sprite != null) {
			target.sprite.centerEmitter().burst(RainbowParticle.BURST, 10 + level());
		}
		target.damage(damageAgainst(damage, target), this);
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica beam, Callback callback) {
		curUser.sprite.parent.add(new Beam.LightRay(curUser.sprite.center(),
				DungeonTilemap.raisedTileCenterToWorld(beam.collisionPos)));
		callback.call();
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
