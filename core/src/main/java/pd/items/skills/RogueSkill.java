/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.Assets;
import pd.Dungeon;
import pd.actors.buffs.ArmorBreak;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Disarm;
import pd.actors.buffs.GoldTouch;
import pd.actors.buffs.HasteBuff;
import pd.actors.buffs.HighAttack;
import pd.actors.buffs.Invisibility;
import pd.actors.buffs.ItemSteal;
import pd.actors.buffs.MoonFury;
import pd.actors.buffs.Silent;
import pd.actors.mobs.Mob;
import pd.effects.particles.ElmoParticle;
import pd.items.Generator;
import pd.items.Item;
import pd.sprites.ItemSpriteSheet;
import render.noosa.audio.Sample;
import render.utils.math.Random;

/** The four rogue class skills from SPS-PD 0.9.8. */
public class RogueSkill extends ClassSkill {

	private static final float SKILL_TIME = 1f;

	{
		image = ItemSpriteSheet.ARTIFACT_CLOAK;
	}

	@Override
	public void doSpecial() {
		addCooldown(15);
		if (curUser.lvl > 55) {
			Buff.affect(curUser, MoonFury.class);
			Buff.affect(curUser, HasteBuff.class, 15f);
			Buff.affect(curUser, AttackUp.class, 15f).level(50);
		} else {
			switch (Random.Int(3)) {
				case 0: Buff.affect(curUser, MoonFury.class); break;
				case 1: Buff.affect(curUser, HasteBuff.class, 15f); break;
				default: Buff.affect(curUser, AttackUp.class, 15f).level(50); break;
			}
		}
		for (Mob mob : Dungeon.level.mobs) {
			if (mob.pos >= 0 && mob.pos < Dungeon.level.heroFOV.length
					&& Dungeon.level.heroFOV[mob.pos]
					&& Dungeon.level.distance(curUser.pos, mob.pos) <= 10) {
				Buff.affect(mob, Silent.class, 9999f);
				Buff.affect(mob, Disarm.class, 5f);
				Buff.affect(mob, ArmorBreak.class, 10f).level(50);
				Buff.prolong(mob, Blindness.class, 3f);
			}
		}
		finishCast();
	}

	@Override
	public void doSpecial2() {
		addCooldown(20);
		float duration = curUser.lvl > 55 ? 30f : 15f;
		Buff.affect(curUser, ItemSteal.class, duration);
		Buff.affect(curUser, GoldTouch.class, duration);
		finishCast();
	}

	@Override
	public void doSpecial3() {
		Item ring = Generator.random(Generator.Category.RING);
		if (ring != null) {
			ring.identify().uncurse().upgrade(5);
			if (curUser.lvl > 55) ring.reinforce();
			Dungeon.level.drop(ring, curUser.pos).sprite.drop(curUser.pos);
		}
		addCooldown(20);
		finishCast();
	}

	@Override
	public void doSpecial4() {
		addCooldown(20);
		Buff.affect(curUser, HighAttack.class);
		finishCast();
	}

	private void finishCast() {
		if (curUser.sprite != null) {
			curUser.spend(SKILL_TIME);
			curUser.busy();
			curUser.sprite.centerEmitter().burst(ElmoParticle.FACTORY, 4);
			curUser.sprite.operate(curUser.pos);
		} else {
			curUser.spendAndNext(SKILL_TIME);
		}
		Sample.INSTANCE.play(Assets.Sounds.READ);
	}
}
