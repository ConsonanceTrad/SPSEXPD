/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.actors.hero.spells;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.Paralysis;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.hero.Talent;
import pd.effects.Beam;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.wands.Wand;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.tiles.DungeonTilemap;
import pd.ui.HeroIcon;
import pd.ui.QuickSlotButton;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import pd.messages.InlineText;

public class Sunray extends TargetedClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(Sunray.class)
			.t("name", "阳炎射线")
			.t("short_desc", "造成远程魔法伤害并致盲目标一次。")
			.t("desc", "牧师向目标发射致盲光束，造成%1$d~%2$d伤害并使其失明%3$d回合。阳炎射线必定对亡灵和恶魔目标造成最大伤害。\n\n被该法术击中后敌人会调节其视觉，以防被射线再次致盲。然而，如果在被该法术致盲后再次被该法术击中，那么压倒性的强光会将其麻痹而非致盲。");
	}


	public static final Sunray INSTANCE = new Sunray();

	@Override
	public int icon() {
		return HeroIcon.SUNRAY;
	}

	@Override
	public String desc() {
		int min = Dungeon.hero.pointsInTalent(Talent.SUNRAY) == 2 ? 6 : 4;
		int max = Dungeon.hero.pointsInTalent(Talent.SUNRAY) == 2 ? 12 : 8;
		int dur = Dungeon.hero.pointsInTalent(Talent.SUNRAY) == 2 ? 6 : 4;
		return Messages.get(this, "desc", min, max, dur) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.hasTalent(Talent.SUNRAY);
	}

	@Override
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
		if (target == null){
			return;
		}

		Ballistica aim = new Ballistica(hero.pos, target,  targetingFlags());

		if (Actor.findChar( aim.collisionPos ) == hero){
			GLog.i( Messages.get(Wand.class, "self_target") );
			return;
		}

		if (Actor.findChar(aim.collisionPos) != null) {
			QuickSlotButton.target(Actor.findChar(aim.collisionPos));
		} else {
			QuickSlotButton.target(Actor.findChar(target));
		}

		hero.busy();
		Sample.INSTANCE.play( Assets.Sounds.RAY );
		hero.sprite.zap(target);

		hero.sprite.parent.add(
				new Beam.SunRay(hero.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(aim.collisionPos)));
		Sample.INSTANCE.play( Assets.Sounds.RAY );

		Char ch = Actor.findChar( aim.collisionPos );
		if (ch != null) {
			ch.sprite.burst(0xFFFFFF44, 5);

			if (Char.hasProp(ch, Char.Property.UNDEAD) || Char.hasProp(ch, Char.Property.DEMONIC)){
				if (hero.pointsInTalent(Talent.SUNRAY) == 2) {
					ch.damage(12, Sunray.this);
				} else {
					ch.damage(8, Sunray.this);
				}
			} else {
				if (hero.pointsInTalent(Talent.SUNRAY) == 2) {
					ch.damage(Hero.heroDamageIntRange(6, 12), Sunray.this);
				} else {
					ch.damage(Hero.heroDamageIntRange(4, 8), Sunray.this);
				}
			}

			if (ch.isAlive()) {
				if (ch.buff(Blindness.class) != null && ch.buff(SunRayRecentlyBlindedTracker.class) != null) {
					Buff.prolong(ch, Paralysis.class, 2f + 2f*hero.pointsInTalent(Talent.SUNRAY));
					ch.buff(SunRayRecentlyBlindedTracker.class).detach();
				} else if (ch.buff(SunRayUsedTracker.class) == null) {
					Buff.prolong(ch, Blindness.class, 2f + 2f*hero.pointsInTalent(Talent.SUNRAY));
					Buff.prolong(ch, SunRayRecentlyBlindedTracker.class, 2f + 2f*hero.pointsInTalent(Talent.SUNRAY));
					Buff.affect(ch, SunRayUsedTracker.class);
				}
				if (hero.subClass == HeroSubClass.PRIEST){
					Buff.affect(ch, GuidingLight.Illuminated.class);
				}
			}
		}

		hero.spend( 1f );
		hero.next();

		onSpellCast(tome, hero);

	}

	public static class SunRayUsedTracker extends Buff {}
	public static class SunRayRecentlyBlindedTracker extends FlavourBuff {}

}
