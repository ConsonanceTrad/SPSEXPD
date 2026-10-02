/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.wands;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.effects.CellEmitter;
import pd.effects.Lightning;
import pd.effects.particles.SparkParticle;
import pd.items.Heap;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.PixelScene;
import render.utils.data.BArray;
import render.utils.data.Callback;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The original SPS-PD chaining lightning wand. */
public class WandOfLightning extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfLightning.class)
			.t("name", "雷霆法杖")
			.t("staff_name", "雷霆魔杖")
			.t("ondeath", "你用雷霆法杖害死了自己...")
			.t("desc", "这根_电属性_法杖由实心金属制成，这使它惊人地沉重。电弧在顶端两个内弯的叉齿间跳跃。")
			.t("stats_desc", "这根法杖能向目标放出强大的电弧并造成_%1$d~%2$d点伤害_。电能会在附近多个目标间跳跃并分散伤害，在水里还会变得更强。要是太接近，你自己同样可能被电到！")
			.t("bmage_desc", "当_战斗法师_以雷霆魔杖近战攻击目标时，有概率获得10回合起电效果。起电状态下的战斗法师免疫雷电伤害，电弧的连锁范围也更广。")
			.t("eleblast_desc", "雷霆魔杖的元素风暴造成100%伤害，击晕目标5回合并使水带电。")
			.t("lightningcharge.name", "起电")
			.t("lightningcharge.desc", "战斗法师现已被充能，获得了对其雷霆魔杖的伤害免疫并扩大了其电弧的连锁范围。\n\n剩余回合数：%s");
	}


	{
		image = EquipmentWandBasicWandDict.WAND_SPS_LIGHTNING;
		collisionProperties = Ballistica.PROJECTILE;
	}

	private final ArrayList<Char> affected = new ArrayList<>();
	private final ArrayList<Lightning.Arc> arcs = new ArrayList<>();

	@Override public int min(int level) { return 5 + level; }
	@Override public int max(int level) { return Math.round(10 + level * level / 4f); }

	public static float chainMultiplier(int targets, boolean targetInWater) {
		if (targets <= 0) return 0f;
		float multiplier = 0.4f + 0.6f / targets;
		return targetInWater ? multiplier * 1.5f : multiplier;
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica bolt) {
		float multiplier = chainMultiplier(affected.size(), Dungeon.level.water[bolt.collisionPos]);
		for (Char target : affected) {
			wandProc(target, chargesPerCast());
			int rolled = Random.NormalIntRange(min(level()), max(level()));
			int damage = (int)(magicSkillMultiplier(Dungeon.hero.magicSkill())
					* Math.round(rolled * multiplier));
			target.damage(damage, this);
			if (target == Dungeon.hero) PixelScene.shake(2, 0.3f);
			if (target.sprite != null) {
				target.sprite.centerEmitter().burst(SparkParticle.FACTORY, 3);
				target.sprite.flash();
			}
		}
		if (!curUser.isAlive()) Dungeon.fail(this);

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.shockhit();
	}

	private void arc(Char target) {
		affected.add(target);

		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = target.pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			Char next = Actor.findChar(cell);
			if (next != null && !affected.contains(next)) {
				arcs.add(new Lightning.Arc(target.pos, next.pos));
				arc(next);
			}
		}

		if (Dungeon.level.water[target.pos] && !target.flying) {
			PathFinder.buildDistanceMap(target.pos, BArray.not(Dungeon.level.solid, null), 2);
			for (int cell = 0; cell < PathFinder.distance.length; cell++) {
				if (PathFinder.distance[cell] == Integer.MAX_VALUE || !Dungeon.level.insideMap(cell)) continue;
				Char next = Actor.findChar(cell);
				if (next == Dungeon.hero || next == null || affected.contains(next)) continue;
				arcs.add(new Lightning.Arc(target.pos, next.pos));
				arc(next);
			}
		}
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		affected.clear();
		arcs.clear();
		arcs.add(new Lightning.Arc(bolt.sourcePos, bolt.collisionPos));

		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			arc(target);
		} else {
			CellEmitter.center(bolt.collisionPos).burst(SparkParticle.FACTORY, 3);
		}
		curUser.sprite.parent.add(new Lightning(arcs, null));
		callback.call();
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}
}
