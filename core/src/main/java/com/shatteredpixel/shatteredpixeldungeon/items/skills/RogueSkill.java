/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package com.shatteredpixel.shatteredpixeldungeon.items.skills;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AttackUp;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Disarm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GoldTouch;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HasteBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HighAttack;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ItemSteal;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MoonFury;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Silent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

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
