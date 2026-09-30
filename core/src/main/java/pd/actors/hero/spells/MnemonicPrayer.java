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
import pd.actors.buffs.AdrenalineSurge;
import pd.actors.buffs.ArcaneArmor;
import pd.actors.buffs.ArtifactRecharge;
import pd.actors.buffs.Barkskin;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Corrosion;
import pd.actors.buffs.Dread;
import pd.actors.buffs.FireImbue;
import pd.actors.buffs.FlavourBuff;
import pd.actors.buffs.GreaterHaste;
import pd.actors.buffs.Healing;
import pd.actors.buffs.LifeLink;
import pd.actors.buffs.Ooze;
import pd.actors.buffs.Poison;
import pd.actors.buffs.ShieldBuff;
import pd.actors.buffs.ToxicImbue;
import pd.actors.buffs.WellFed;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.AscendedForm;
import pd.actors.hero.abilities.cleric.PowerOfMany;
import pd.effects.Speck;
import pd.items.armor.glyphs.Viscosity;
import pd.items.artifacts.HolyTome;
import pd.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import pd.items.scrolls.exotic.ScrollOfChallenge;
import pd.items.weapon.enchantments.Kinetic;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.plants.Sungrass;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.ui.QuickSlotButton;
import pd.utils.GLog;
import watabou.noosa.audio.Sample;

public class MnemonicPrayer extends TargetedClericSpell {

	public static MnemonicPrayer INSTANCE = new MnemonicPrayer();

	@Override
	public int icon() {
		return HeroIcon.MNEMONIC_PRAYER;
	}

	@Override
	public int targetingFlags() {
		return Ballistica.STOP_TARGET;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.hasTalent(Talent.MNEMONIC_PRAYER);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {

		if (target == null){
			return;
		}

		Char ch = Actor.findChar(target);
		if (ch == null || !Dungeon.level.heroFOV[target]){
			GLog.w(Messages.get(this, "no_target"));
			return;
		}

		QuickSlotButton.target(ch);

		float extension = 2 + hero.pointsInTalent(Talent.MNEMONIC_PRAYER);
		affectChar(ch, extension);

		Char ally = PowerOfMany.getPoweredAlly();
		if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null){
			if (ch == hero){
				affectChar(ally, extension); //if cast on hero, duplicate to ally
			} else if (ch == ally){
				affectChar(hero, extension); //if cast on ally, duplicate to hero
			}
		}

		if (ch == hero){
			hero.sprite.operate(ch.pos);
			BuffIndicator.refreshHero();
		} else {
			hero.sprite.zap(ch.pos);
			hero.next();
		}

		onSpellCast(tome, hero);

	}

	private void affectChar( Char ch, float extension ){
		if (ch.alignment == Char.Alignment.ALLY){

			Sample.INSTANCE.play(Assets.Sounds.CHARGEUP);
			ch.sprite.emitter().start(Speck.factory(Speck.UP), 0.15f, 4);

			for (Buff b : ch.buffs()){
				if (b.type != Buff.buffType.POSITIVE || b.mnemonicExtended || b.icon() == BuffIndicator.NONE){
					continue;
				}

				//does not boost buffs from armor abilities or T4 spells
				if (b instanceof AscendedForm.AscendBuff
						|| b instanceof BodyForm.BodyFormBuff || b instanceof SpiritForm.SpiritFormBuff
						|| b instanceof PowerOfMany.PowerBuff || b instanceof BeamingRay.BeamingRayBoost || b instanceof LifeLink || b instanceof LifeLinkSpell.LifeLinkSpellBuff){
					continue;
				}

				//should consider some buffs that may be OP here, e.g. invuln
				if (b instanceof FlavourBuff)           Buff.affect(ch, (Class<?extends FlavourBuff>)b.getClass(), extension);
				else if (b instanceof AdrenalineSurge)  ((AdrenalineSurge) b).delay(extension);
				else if (b instanceof ArcaneArmor)      ((ArcaneArmor) b).delay(extension);
				else if (b instanceof ArtifactRecharge) ((ArtifactRecharge) b).extend(extension);
				else if (b instanceof Barkskin)         ((Barkskin) b).delay(extension);
				else if (b instanceof FireImbue)        ((FireImbue) b).extend(extension);
				else if (b instanceof GreaterHaste)     ((GreaterHaste) b).extend(extension);
				else if (b instanceof Healing)          ((Healing) b).increaseHeal((int)extension);
				else if (b instanceof ToxicImbue)       ((ToxicImbue) b).extend(extension);
				else if (b instanceof WellFed)          ((WellFed) b).extend(extension);
				else if (b instanceof ElixirOfAquaticRejuvenation.AquaHealing)  ((ElixirOfAquaticRejuvenation.AquaHealing) b).extend(extension);
				else if (b instanceof ScrollOfChallenge.ChallengeArena)         ((ScrollOfChallenge.ChallengeArena) b).extend(extension);
				else if (b instanceof ShieldBuff)               ((ShieldBuff) b).delay(extension);
				else if (b instanceof Kinetic.ConservedDamage)  ((Kinetic.ConservedDamage) b).delay(extension);
				else if (b instanceof Sungrass.Health)          ((Sungrass.Health) b).boost((int) extension);

				b.mnemonicExtended = true;

			}

		} else {

			Sample.INSTANCE.play(Assets.Sounds.DEBUFF);
			ch.sprite.emitter().start(Speck.factory(Speck.DOWN), 0.15f, 4);

			Buff.affect(ch, GuidingLight.Illuminated.class);

			for (Buff b : ch.buffs()){
				if (b.type != Buff.buffType.NEGATIVE || b.mnemonicExtended){
					continue;
				}

				//this might need a nerf of aggression vs bosses. (perhaps nerf the extension?)
				if (b instanceof FlavourBuff)       Buff.affect(ch, (Class<?extends FlavourBuff>)b.getClass(), extension);
				else if (b instanceof Bleeding)     ((Bleeding) b).extend( extension );
				else if (b instanceof Burning)      ((Burning) b).extend( extension );
				else if (b instanceof Corrosion)    ((Corrosion) b).extend( extension );
				else if (b instanceof Dread)        ((Dread) b).extend( extension );
				else if (b instanceof Ooze)         ((Ooze) b).extend( extension );
				else if (b instanceof Poison)       ((Poison) b).extend( extension );
				else if (b instanceof Viscosity.DeferedDamage)  ((Viscosity.DeferedDamage) b).extend( extension );

				b.mnemonicExtended = true;

			}

		}
	}

	public String desc(){
		return Messages.get(this, "desc", 2 + Dungeon.hero.pointsInTalent(Talent.MNEMONIC_PRAYER)) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

}
