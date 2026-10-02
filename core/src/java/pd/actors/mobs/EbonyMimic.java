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

package pd.actors.mobs;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.effects.CellEmitter;
import pd.effects.Speck;
import pd.items.EquipableItem;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.armor.Armor;
import pd.items.equipment.artifacts.Artifact;
import pd.items.equipment.wands.Wand;
import pd.items.equipment.weapon.Weapon;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.levels.Terrain;
import pd.levels.features.Door;
import pd.messages.Messages;
import pd.sprites.MimicSprite;
import pd.utils.GLog;
import render.noosa.audio.Sample;
import pd.messages.InlineText;

public class EbonyMimic extends Mimic {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(EbonyMimic.class)
			.t("name", "黑檀宝箱怪")
			.t("reveal", "那里有一个宝箱怪！")
			.t("hidden_name", "可疑的轮廓")
			.t("hidden_desc", "那里似乎有什么东西，但它几乎是完全透明的。")
			.t("desc", "宝箱怪是一种能随意改变外形的魔法生物。在地牢里它们几乎一直以宝箱形态出现，因为这样总能吸引疏于防备的冒险家。\n\n黑檀宝箱怪凭借其隐匿能力使自身近乎隐形。它们会在冒险家可能交互的事物之上设伏诱敌，攻其不备。与此同时，它们体内也含有其专属的战利品。若能识破其伪装，其攻防能力与普通宝箱怪无异。但若稍有不慎，其出其不意的伏击足以致命。")
			.t("discover_hint", "你可通过某件饰物遇到该敌人。");
	}




	{
		spriteClass = MimicSprite.Ebony.class;
	}

	@Override
	public String name() {
		if (alignment == Alignment.NEUTRAL){
			return Messages.get(this, "hidden_name");
		} else {
			return super.name();
		}
	}

	@Override
	public String description() {
		if (alignment == Alignment.NEUTRAL){
			return Messages.get(this, "hidden_desc");
		} else {
			return super.description();
		}
	}

	@Override
	public boolean stealthy() {
		return true;
	}

	public void stopHiding(){
		state = HUNTING;
		if (sprite != null) sprite.idle();
		if (Actor.chars().contains(this) && Dungeon.level.heroFOV[pos]) {
			enemy = Dungeon.hero;
			target = Dungeon.hero.pos;
			GLog.w(Messages.get(this, "reveal") );
			CellEmitter.get(pos).burst(Speck.factory(Speck.STAR), 10);
			Sample.INSTANCE.play(Assets.Sounds.MIMIC, 1, 0.85f);
		}
		if (Actor.chars().contains(this) && Dungeon.level.map[pos] == Terrain.DOOR){
			Door.enter( pos );
		}
	}

	@Override
	public int damageRoll() {
		if (alignment == Alignment.NEUTRAL){
			return Math.round(super.damageRoll()*2f); //BIG damage on surprise
		} else {
			return super.damageRoll();
		}
	}

	@Override
	protected void generatePrize( boolean useDecks ) {
		super.generatePrize( useDecks );
		//add one extra random loot item, on top of the one granted by mimic tooth
		items.add(Generator.randomUsingDefaults());

		//all existing prize items are guaranteed uncursed, and are always at least +1
		for (Item i : items){
			if (i instanceof EquipableItem || i instanceof Wand){
				i.cursed = false;
				i.cursedKnown = true;
				if (i instanceof Weapon && ((Weapon) i).hasCurseEnchant()){
					((Weapon) i).enchant(null);
				}
				if (i instanceof Armor && ((Armor) i).hasCurseGlyph()){
					((Armor) i).inscribe(null);
				}
				if (!(i instanceof Artifact) && i.level() == 0){
					i.upgrade();
				}
			}
		}
	}

}
