/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Burning;
import pd.effects.MagicMissile;
import pd.effects.particles.FlameParticle;
import pd.items.Heap;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.scenes.GameScene;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import pd.messages.InlineText;

/** The single-target firebolt wand from SPS-PD 0.9.8. */
public class WandOfFirebolt extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfFirebolt.class)
			.t("name", "火球法杖")
			.t("desc", "这根火属性法杖由红漆木制成，饰以金叶，这使它看起来相当庄严。它的顶端噼啪作响、嘶嘶而鸣，渴望释放强大的魔法。")
			.t("stats_desc", "该法杖会发射一枚火球，造成_%1$d~%2$d点伤害_，使目标燃烧5回合并点燃落点。");
	}




	{
		image = EquipmentWandBasicWandDict.WAND_SPS_FIREBOLT;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override
	public int min(int lvl) {
		return lvl;
	}

	@Override
	public int max(int lvl) {
		return 10 + 6 * lvl;
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.burn();

		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			target.damage((int) (damageRoll() * magicSkillMultiplier(Dungeon.hero.magicSkill())), this);
			Buff.affect(target, Burning.class).reignite(target, 5f);
			if (target.sprite != null) target.sprite.emitter().burst(FlameParticle.FACTORY, 5);
		}

		GameScene.add(Blob.seed(bolt.collisionPos, 1, Fire.class));
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.FIRE,
				curUser.sprite, bolt.collisionPos, callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}
}
