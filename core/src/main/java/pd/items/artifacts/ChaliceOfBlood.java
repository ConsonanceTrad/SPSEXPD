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

package pd.items.artifacts;

import pd.Assets;
import pd.Badges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.BloodAngry;
import pd.actors.buffs.Buff;
import pd.actors.hero.Hero;
import pd.effects.particles.ElmoParticle;
import pd.effects.particles.ShadowParticle;
import pd.items.Item;
import pd.journal.Catalog;
import pd.messages.Messages;
import pd.plants.Earthroot;
import pd.scenes.GameScene;
import pd.sprites.ItemSpriteSheet;
import pd.utils.GLog;
import pd.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

import java.util.ArrayList;

public class ChaliceOfBlood extends Artifact {

	{
		image = ItemSpriteSheet.ARTIFACT_CHALICE1;

		levelCap = 10;
		defaultAction = AC_BLOODANGRY;
	}

	public static final String AC_PRICK = "PRICK";
	public static final String AC_BLOODANGRY = "BLOODANGRY";

	@Override
	public ArrayList<String> actions( Hero hero ) {
		ArrayList<String> actions = super.actions( hero );
		if (isEquipped(hero) && level() < levelCap && !cursed)
			actions.add(AC_PRICK);
		if (isEquipped(hero) && level() > 3 && !cursed)
			actions.add(AC_BLOODANGRY);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action ) {
		super.execute(hero, action);

		if (action.equals(AC_PRICK)) {
			if (shouldWarnAboutPrick(hero)) {
				GameScene.show(new WndOptions(
						Messages.get(this, "name"),
						Messages.get(this, "prick_warn"),
						Messages.get(this, "yes"),
						Messages.get(this, "no")) {
					@Override
					protected void onSelect(int index) {
						if (index == 0) {
							prick(Dungeon.hero);
						}
					}
				});
			} else {
				prick(hero);
			}
		} else if (action.equals(AC_BLOODANGRY)) {
			useBloodAngry(hero);
		}
	}

	boolean shouldWarnAboutPrick(Hero hero) {
		return 3 * level() * level() > hero.HP * 0.75f;
	}

	void useBloodAngry(Hero hero) {
		if (!isEquipped(hero) || level() < 4 || cursed) {
			GLog.i(Messages.get(Artifact.class, "need_to_equip"));
			return;
		}
		level(level() - 3);
		Sample.INSTANCE.play(Assets.Sounds.BURNING);
		if (hero.sprite != null) hero.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
		Buff.affect(hero, BloodAngry.class).set(100f);
		hero.spend(Actor.TICK);
		hero.busy();
		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		updateQuickslot();
	}

	void prick(Hero hero){
		int damage = 2 * level() * level();

		//need to process on-hit effects manually
		Earthroot.Armor armor = hero.buff(Earthroot.Armor.class);
		if (armor != null) {
			damage = armor.absorb(damage);
		}

		damage -= Random.IntRange(0, hero.drRoll());

		if (hero.sprite != null) hero.sprite.operate(hero.pos);
		hero.busy();
		hero.spend(3f);
		GLog.w( Messages.get(this, "onprick") );
		if (damage <= 0){
			damage = 1;
		} else {
			Sample.INSTANCE.play(Assets.Sounds.CURSED);
			if (hero.sprite != null) hero.sprite.emitter().burst(ShadowParticle.CURSE, 4 + damage / 10);
		}

		hero.damage(damage, this);
		Buff.affect(hero, Bleeding.class).set(level() * level());

		if (!hero.isAlive()) {
			Badges.validateDeathFromFriendlyMagic();
			Dungeon.fail( this );
			GLog.n( Messages.get(this, "ondeath") );
		} else {
			upgrade();
			Catalog.countUse(getClass());
		}
	}

	@Override
	public Item upgrade() {
		if (level() >= 6)
			image = ItemSpriteSheet.ARTIFACT_CHALICE3;
		else if (level() >= 2)
			image = ItemSpriteSheet.ARTIFACT_CHALICE2;
		return super.upgrade();
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (level() >= 7) image = ItemSpriteSheet.ARTIFACT_CHALICE3;
		else if (level() >= 3) image = ItemSpriteSheet.ARTIFACT_CHALICE2;
	}

	@Override
	protected ArtifactBuff passiveBuff() {
		return new chaliceRegen();
	}
	
	@Override
	public String desc() {
		String desc = super.desc();

		if (isEquipped (Dungeon.hero)){
			desc += "\n\n";
			if (cursed)
				desc += Messages.get(this, "desc_cursed");
			else if (level() == 0)
				desc += Messages.get(this, "desc_1");
			else if (level() < levelCap)
				desc += Messages.get(this, "desc_2");
			else
				desc += Messages.get(this, "desc_3");
		}

		return desc;
	}

	public class chaliceRegen extends ArtifactBuff {
		//see Regeneration.class for effect
	}

}
