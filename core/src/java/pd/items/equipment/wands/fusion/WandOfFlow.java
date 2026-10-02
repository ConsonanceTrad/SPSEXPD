package pd.items.equipment.wands.fusion;

import pd.atlas.items.EquipmentWandBasicWandDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Wet;
import pd.actors.mobs.npcs.SpsSokobanSheep;
import pd.effects.Beam;
import pd.effects.Pushing;
import pd.items.Heap;
import pd.items.equipment.wands.DamageWand;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.levels.SpsSokobanLevel;
import pd.mechanics.Ballistica;
import pd.tiles.DungeonTilemap;
import render.noosa.audio.Sample;
import render.utils.data.Callback;
import render.utils.math.Random;
import pd.messages.InlineText;

/** The original SPS-PD water and knockback wand. */
public class WandOfFlow extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfFlow.class)
			.t("name", "涌流法杖")
			.t("staff_name", "涌流魔杖")
			.t("ondeath", "你被自己的涌流法杖击溃了...")
			.t("desc", "这根_冰属性_法杖比普通法杖短，但顶端的小喷泉使它看起来和普通法杖一样高。")
			.t("typical_stats_desc", "涌流通常造成_%1$d~%2$d点伤害_，并将大多数敌人击退。")
			.t("stats_desc", "该法杖能向目标地点喷射强劲水流，造成_%1$d~%2$d点伤害_，击退目标并将其打湿。")
			.t("upgrade_stat_name_2", "击退")
			.t("bmage_desc", "战斗法师以涌流魔杖攻击麻痹目标时，会消耗麻痹并造成额外魔法伤害。")
			.t("eleblast_desc", "涌流魔杖的元素风暴造成67%伤害并击退范围外沿的敌人。");
	}


	{
		image = EquipmentWandBasicWandDict.WAND_FLOW;
		collisionProperties = Ballistica.PROJECTILE;
	}

	@Override public int min(int level) { return 1 + level; }
	@Override public int max(int level) { return 5 + 3 * level; }

	public static int pushStrength(int level) {
		return Math.min(level + 3, 5);
	}

	public static float magicSkillMultiplier(int magicSkill) {
		return 1f + 0.1f * magicSkill;
	}

	@Override
	public void onZap(Ballistica bolt) {
		Char target = Actor.findChar(bolt.collisionPos);
		if (target != null) {
			wandProc(target, chargesPerCast());
			int damage = (int)(damageRoll() * magicSkillMultiplier(Dungeon.hero.magicSkill()));
			target.damage(damage, this);
			Buff.affect(target, Wet.class, 5f);
			if (target.isAlive() && bolt.path.size() > bolt.dist + 1) {
				Ballistica trajectory = new Ballistica(target.pos,
						bolt.path.get(bolt.dist + 1), Ballistica.MAGIC_BOLT);
				throwChar(target, trajectory, pushStrength(level()));
			}
		}

		Heap heap = Dungeon.level.heaps.get(bolt.collisionPos);
		if (heap != null) heap.icehit();
	}

	public static void throwChar(Char target, Ballistica trajectory, int power) {
		push(target, trajectory, power, true);
	}

	public static void pushChar(Char target, Ballistica trajectory, int power) {
		push(target, trajectory, power, false);
	}

	private static void push(final Char target, final Ballistica trajectory, int power,
			boolean collisionEffects) {
		int distance = Math.min(trajectory.dist, power);
		if (target.properties().contains(Char.Property.BOSS)) distance /= 2;
		if (distance <= 0 || target.rooted) return;

		if (target instanceof SpsSokobanSheep) distance = Math.min(1, distance);
		if (Actor.findChar(trajectory.path.get(distance)) != null) distance--;
		if (distance <= 0) return;

		final int newPos = trajectory.path.get(distance);
		if (newPos == target.pos) return;
		final int finalDistance = distance;
		final int initialPos = target.pos;
		final boolean collided = collisionEffects && newPos == trajectory.collisionPos;

		Actor.add(new Pushing(target, initialPos, newPos, new Callback() {
			@Override
			public void call() {
				if (initialPos != target.pos || Actor.findChar(newPos) != null) {
					if (target.sprite != null) target.sprite.place(target.pos);
					return;
				}
				target.pos = newPos;
				Dungeon.level.occupyCell(target);
				if (target instanceof SpsSokobanSheep && Dungeon.level instanceof SpsSokobanLevel) {
					((SpsSokobanLevel)Dungeon.level).afterSheepMoved((SpsSokobanSheep)target);
				} else if (collided) {
					int minimum = (finalDistance + 1) / 2;
					target.damage(Random.NormalIntRange(minimum, finalDistance), WandOfFlow.class);
					if (target.isAlive()) {
						Paralysis.prolong(target, Paralysis.class,
								Random.NormalIntRange(minimum, finalDistance));
					}
				}
				if (target == Dungeon.hero) Dungeon.observe();
			}
		}));
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		curUser.sprite.parent.add(new Beam.WaterRay(curUser.sprite.center(),
				DungeonTilemap.tileCenterToWorld(bolt.collisionPos)));
		Sample.INSTANCE.play(Assets.Sounds.RAY);
		callback.call();
	}
}
