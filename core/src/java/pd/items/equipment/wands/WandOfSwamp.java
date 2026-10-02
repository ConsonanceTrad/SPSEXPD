/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.SwampGas;
import pd.effects.MagicMissile;
import pd.items.Heap;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import pd.messages.InlineText;

/** The direct-damage swamp-gas wand from SPS-PD 0.9.8. */
public class WandOfSwamp extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfSwamp.class)
			.t("name", "沼泽法杖")
			.t("desc", "这根地属性法杖由一根枯木枝巧琢而成。不知为何它还活着。")
			.t("stats_desc", "该法杖能射出一颗会在目标位置爆炸的沼泽法球，造成_%1$d~%2$d点伤害_并释放沼泽气体，使其中的生物易伤且越来越迟缓。");
	}




	{
		image = EquipmentWandBasicWandDict.WAND_ACID;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public int min(int lvl) {
		return lvl;
	}

	@Override
	public int max(int lvl) {
		return 8 + 4 * lvl;
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			target.damage((int) (damageRoll() * magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
		}

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.earthhit();

		GameScene.add(Blob.seed(bolt.collisionPos, 100, SwampGas.class));
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.POISON,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
