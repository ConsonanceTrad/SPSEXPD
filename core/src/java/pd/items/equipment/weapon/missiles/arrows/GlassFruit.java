package pd.items.equipment.weapon.missiles.arrows;

import pd.atlas.items.ConsumPotionSeedSeedDict;

import pd.Assets;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.CorrosiveGas;
import pd.actors.buffs.Bleeding;
import pd.actors.buffs.Buff;
import pd.items.equipment.weapon.missiles.MissileWeapon;
import pd.scenes.GameScene;
import pd.messages.InlineText;
import pd.atlas.items.SpecificPlaceHolderDict;

public class GlassFruit extends MissileWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(GlassFruit.class)
			.t("name", "水晶果")
			.t("desc", "硅花人工培育出的水晶果。直接命中会使目标严重流血；未命中时，果实会破裂并释放一小团腐蚀气体。");
	}



	{
		image = SpecificPlaceHolderDict.SEED_HOLDER_0;
		hitSound = Assets.Sounds.HIT_STAB;
		hitSoundPitch = 1.2f;
		baseUses = 1;
		tier = 2;
		levelKnown = true;
	}

	@Override public int min(int lvl) { return 10; }
	@Override public int max(int lvl) { return 10; }

	@Override protected void onThrow(int cell) {
		Char target = Actor.findChar(cell);
		if (target == null || target == curUser) {
			GameScene.add(Blob.seed(cell, 6, CorrosiveGas.class));
		} else {
			super.onThrow(cell);
		}
	}

	@Override public int proc(Char attacker, Char defender, int damage) {
		Buff.affect(defender, Bleeding.class).set(damage);
		return super.proc(attacker, defender, damage);
	}

	@Override public boolean isUpgradable() { return false; }
	@Override public boolean isIdentified() { return true; }
	@Override public int value() { return 2 * quantity; }
}
