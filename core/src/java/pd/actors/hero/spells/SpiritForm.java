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

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Corruption;
import pd.actors.buffs.FlavourBuff;
import pd.actors.hero.Hero;
import pd.actors.hero.Talent;
import pd.actors.hero.abilities.cleric.Trinity;
import pd.actors.mobs.Wraith;
import pd.items.equipment.armor.ClassArmor;
import pd.items.equipment.artifacts.AlchemistsToolkit;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.artifacts.DriedRose;
import pd.items.equipment.artifacts.EtherealChains;
import pd.items.equipment.artifacts.HolyTome;
import pd.items.equipment.artifacts.HornOfPlenty;
import pd.items.equipment.artifacts.MasterThievesArmband;
import pd.items.equipment.artifacts.SandalsOfNature;
import pd.items.equipment.artifacts.SkeletonKey;
import pd.items.equipment.artifacts.TalismanOfForesight;
import pd.items.equipment.artifacts.TimekeepersHourglass;
import pd.items.equipment.artifacts.UnstableSpellbook;
import pd.items.equipment.rings.Ring;
import pd.items.equipment.rings.RingOfMight;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.plants.Blindweed;
import pd.plants.Fadeleaf;
import pd.plants.Firebloom;
import pd.plants.Icecap;
import pd.plants.Sorrowmoss;
import pd.plants.Stormvine;
import pd.plants.Swiftthistle;
import pd.scenes.AlchemyScene;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import pd.ui.HeroIcon;
import pd.ui.QuickSlotButton;
import render.noosa.Game;
import render.noosa.Image;
import render.utils.math.Random;
import render.utils.serialize.Bundlable;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class SpiritForm extends ClericSpell {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(SpiritForm.class)
			.t("name", "魂之位格")
			.t("short_desc", "为三位一体选择戒指或神器。")
			.t("desc", "牧师选择本局已鉴定的戒指或神器(神圣法典除外)并使三位一体模拟其效果。\n\n如果所选物品为戒指，三位一体会获得所选戒指的%1$d级效果20回合。\n\n如果所选物品为神器，三位一体会复制所选神器的%2$d级效果。三位一体使用神器时的具体效果与护甲充能消耗因神器而异。\n\n该法术会刷新三位一体当前已模拟的任何魂之位格效果。")
			.t("$spiritformbuff.name", "魂之位格")
			.t("$spiritformbuff.desc", "位格法术魂之位格正使你获得物品的效果，仿佛你已将其装备于装备栏。\n\n注意，魂之位格无法复制你已经装备的装备效果。\n\n当前的效果：%1$s\n\n剩余回合数：%2$s");
	}




	public static SpiritForm INSTANCE = new SpiritForm();

	@Override
	public int icon() {
		return HeroIcon.SPIRIT_FORM;
	}

	@Override
	public String desc() {
		return Messages.get(this, "desc", ringLevel(), artifactLevel()) + "\n\n" + Messages.get(this, "charge_cost", (int)chargeUse(Dungeon.hero));
	}

	@Override
	public float chargeUse(Hero hero) {
		return 4;
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.hasTalent(Talent.SPIRIT_FORM);
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {

		GameScene.show(new Trinity.WndItemtypeSelect(tome, this));

	}

	public static int ringLevel(){
		return Dungeon.hero.pointsInTalent(Talent.SPIRIT_FORM);
	}

	public static int artifactLevel(){
		return 2 + 2*Dungeon.hero.pointsInTalent(Talent.SPIRIT_FORM);
	}

	public static class SpiritFormBuff extends FlavourBuff{

		{
			type = buffType.POSITIVE;
		}

		public static final float DURATION = 20f;

		private Bundlable effect;

		@Override
		public int icon() {
			return BuffIndicator.TRINITY_FORM;
		}

		@Override
		public void tintIcon(Image icon) {
			icon.hardlight(0, 1, 0);
		}

		@Override
		public float iconFadePercent() {
			return Math.max(0, (DURATION - visualcooldown()) / DURATION);
		}

		public void setEffect(Bundlable effect){
			this.effect = effect;
			if (effect instanceof RingOfMight){
				((Ring) effect).level(ringLevel());
				Dungeon.hero.updateHT( false );
			}
		}

		@Override
		public void detach() {
			super.detach();
			if (effect instanceof RingOfMight){
				Dungeon.hero.updateHT( false );
			}
		}

		public Ring ring(){
			if (effect instanceof Ring){
				((Ring) effect).level(ringLevel());
				return (Ring) effect;
			}
			return null;
		}

		public Artifact artifact(){
			if (effect instanceof Artifact){
				if (((Artifact) effect).visiblyUpgraded() < artifactLevel()){
					((Artifact) effect).transferUpgrade(artifactLevel() - ((Artifact) effect).visiblyUpgraded());
				}
				return (Artifact) effect;
			}
			return null;
		}

		@Override
		public String desc() {
			if (ring() != null){
				return Messages.get(this, "desc", Messages.titleCase(ring().name()), dispTurns());
			} else if (artifact() != null){
				return Messages.get(this, "desc", Messages.titleCase(artifact().name()), dispTurns());
			}
			return super.desc();
		}

		private static final String EFFECT = "effect";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(EFFECT, effect);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			effect = bundle.get(EFFECT);
		}

	}

	public static void applyActiveArtifactEffect(ClassArmor armor, Artifact effect){
		if (effect instanceof AlchemistsToolkit){
			Talent.onArtifactUsed(Dungeon.hero);
			AlchemyScene.assignToolkit((AlchemistsToolkit) effect);
			Game.switchScene(AlchemyScene.class);

		} else if (effect instanceof DriedRose){
			ArrayList<Integer> spawnPoints = new ArrayList<>();
			for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
				int p = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[i];
				if (Actor.findChar(p) == null && !Dungeon.level.solid[p]) {
					spawnPoints.add(p);
				}
			}
			if (!spawnPoints.isEmpty()) {
				Wraith w = Wraith.spawnAt(Random.element(spawnPoints), Wraith.class);

				w.HP = w.HT = 20 + 8*artifactLevel();
				Buff.affect(w, Corruption.class);
			}
			Talent.onArtifactUsed(Dungeon.hero);
			Dungeon.hero.spendAndNext(1f);

		} else if (effect instanceof EtherealChains){
			GameScene.selectCell(((EtherealChains) effect).caster);
			if (Dungeon.quickslot.contains(armor)) {
				QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(armor));
			}

		} else if (effect instanceof HornOfPlenty){
			((HornOfPlenty) effect).doEatEffect(Dungeon.hero, 1);

		} else if (effect instanceof MasterThievesArmband){
			GameScene.selectCell(((MasterThievesArmband) effect).targeter);
			if (Dungeon.quickslot.contains(armor)) {
				QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(armor));
			}

		} else if (effect instanceof SandalsOfNature){
			((SandalsOfNature) effect).curSeedEffect = Random.oneOf(
					Blindweed.Seed.class, Fadeleaf.Seed.class, Firebloom.Seed.class,
					Icecap.Seed.class, Sorrowmoss.Seed.class, Stormvine.Seed.class
			);

			GameScene.selectCell(((SandalsOfNature) effect).cellSelector);
			if (Dungeon.quickslot.contains(armor)) {
				QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(armor));
			}

		} else if (effect instanceof TalismanOfForesight){
			GameScene.selectCell(((TalismanOfForesight) effect).scry);

		} else if (effect instanceof TimekeepersHourglass){
			Buff.affect(Dungeon.hero, Swiftthistle.TimeBubble.class).reset(artifactLevel());
			Dungeon.hero.spendAndNext(1f);

		} else if (effect instanceof UnstableSpellbook){
			((UnstableSpellbook) effect).doReadEffect(Dungeon.hero);

		} else if (effect instanceof SkeletonKey){
			GameScene.selectCell(((SkeletonKey) effect).targeter);
		}
	}

}
