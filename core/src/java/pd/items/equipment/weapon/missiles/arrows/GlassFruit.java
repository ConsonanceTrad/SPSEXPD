package pd.items.equipment.weapon.missiles.arrows;

import pd.items.Heap;

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

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.Assets;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorrosiveGas;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class GlassFruit extends SpsFruit {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GlassFruit.class)
			.t("name", "水晶果实")
			.t("desc", "人工种植的石英花结出的果实。落地时有小概率留下矿石，命中则割伤目标。");
	}



	{
		image = pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_QUARTZFLOWER;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		baseUses = 1;
		tier = 2;
		levelKnown = true;
	}

	public GlassFruit() { this(1); }
	public GlassFruit(int number) { super(pd.atlas.items.ConsumPotionSeedSeedDict.FRUIT_QUARTZFLOWER, 10, 10); quantity(number); }

	@Override public int min(int lvl) { return scaled(10); }
	@Override public int max(int lvl) { return scaled(10); }



	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 2 * quantity; }


	@Override protected void onThrow(int cell) {
		if (landsAt(cell)) {
			int roll = Random.Int(100);
			if (roll < 5) {
				Heap heap = Dungeon.level.drop(pd.items.Generator.random(pd.items.Generator.Category.NORNSTONE), cell);
				if (heap.sprite != null) heap.sprite.drop(cell);
			} else if (roll < 30) {
				Heap heap = Dungeon.level.drop(new pd.items.StoneOre(), cell);
				if (heap.sprite != null) heap.sprite.drop(cell);
			}
		} else super.onThrow(cell);
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Bleeding.class).set(Math.max(1f, defender.HT * 0.03f));
		return super.proc(attacker, defender, 0);
	}

	@Override protected void onEat(Hero hero) {
		hero.damage(Math.max(1, Math.round(hero.HT * 0.1f)), this);
	}
}
