/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.arrows;

import pd.actors.hero.Hero;

import pd.Dungeon;
import pd.actors.buffs.*;
import pd.items.Item;
import pd.items.StoneOre;
import pd.levels.Level;
import pd.levels.Terrain;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.CharSprite;
import render.utils.math.Random;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.ConfusionGas;
import pd.actors.blobs.damageblobs.LightEffectDamage;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Disarm;
import pd.actors.buffs.Locked;
import pd.actors.buffs.Silent;
import pd.actors.buffs.Vertigo;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.InlineText;

public class BlindFruit extends SpsFruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(BlindFruit.class)
			.t("name", "致盲果实")
			.t("desc", "人工种植的致盲草结出的果实。落地会散出混乱气体，命中则使目标沉默。");
	}




	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_BLINDWEED;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		baseUses = 1;
		tier = 1;
		levelKnown = true;
	}

	public BlindFruit() { this(1); }
	public BlindFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_BLINDWEED, 10, 10); quantity(number); }

	@Override public int min(int lvl) { return scaled(10); }
	@Override public int max(int lvl) { return scaled(10); }
	@Override public int STRReq(int lvl) { return 10; }



	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 10 * quantity; }


	@Override protected void onThrow(int cell) {
		//SPSEXPD: 小型 3×3、大型 5×5 圆形；浓度整体 ×3
		if (landsAt(cell)) seedArea(cell, scaled(4), ConfusionGas.class);
		else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		//SPSEXPD: 只保留沉默（去掉缴械）
		Buff.prolong(defender, Silent.class, scaled(2f));
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		//SPSEXPD: 食用效果为正面（发光），默认动作为「食用」
		Buff.affect(hero, Light.class, scaled(10f));
	}
}
