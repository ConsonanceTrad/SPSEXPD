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

package pd.items.equipment.artifacts;

import pd.atlas.items.EquipmentJewelleryArtifactDict;

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
import pd.utils.GLog;
import pd.windows.WndOptions;
import render.noosa.audio.Sample;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

public class ChaliceOfBlood extends Artifact {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(ChaliceOfBlood.class)
			.t("name", "蓄血圣杯")
			.t("ac_prick", "血祭")
			.t("ac_bloodangry", "耗竭-血怒")
			.t("yes", "是的，我知道我在做什么")
			.t("no", "不，我改主意了")
			.t("prick_warn", "每次使用圣杯都会消耗更多的生命能量，要是不够小心，这种行为可以轻易地杀死你。\n\n你确定要给它更多的生命能量吗？")
			.t("onprick", "你刺破了自己的手指，使你的生命精华流入了圣杯。")
			.t("ondeath", "圣杯将你的生命精华吸噬殆尽了...")
			.t("desc", "这个闪闪发光的银质圣杯在边沿突兀地装饰着几颗造型尖锐的宝石。")
			.t("desc_cursed", "被诅咒的圣杯将自己固定在你手上，抑制着你回复生命的能力。")
			.t("desc_1", "握住圣杯的那一刻，你涌起一股想在那些尖锐宝石上刺伤自己的奇特冲动。")
			.t("desc_2", "你的一些血液汇集到圣杯里，你可以隐约感受到杯子在为你送来生命能量。你还想用圣杯继续割伤自己，即便你知道那很疼。")
			.t("desc_3", "圣杯已经被你的生命精华填满。你可以感觉到圣杯正将生命能量倾泻般回馈给你。");
	}


	{
		image = EquipmentJewelleryArtifactDict.ARTIFACT_CHALICE1;

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
			image = EquipmentJewelleryArtifactDict.ARTIFACT_CHALICE3;
		else if (level() >= 2)
			image = EquipmentJewelleryArtifactDict.ARTIFACT_CHALICE2;
		return super.upgrade();
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		if (level() >= 7) image = EquipmentJewelleryArtifactDict.ARTIFACT_CHALICE3;
		else if (level() >= 3) image = EquipmentJewelleryArtifactDict.ARTIFACT_CHALICE2;
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
