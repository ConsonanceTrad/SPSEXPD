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
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Paralysis;
import pd.effects.MagicMissile;
import pd.items.equipment.weapon.melee.MagesStaff;
import pd.mechanics.Ballistica;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.sprites.ItemSprite;
import render.noosa.audio.Sample;
import render.utils.data.Callback;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import pd.messages.InlineText;

/** SFB's expanding fire-wave wand from SPS-PD 0.9.8. */
public class WandOfShatteredFireblast extends DamageWand {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(WandOfShatteredFireblast.class)
			.t("name", "破碎暴风火杖")
			.t("desc", "这根火属性法杖是工会法师的武器，它能释放大范围的火焰波。")
			.t("stats_desc", "该法杖会消耗%1$d点充能，造成_%2$d~%3$d点伤害_，并使敌人困在火焰中。");
	}


	private static final ItemSprite.Glowing WHITE = new ItemSprite.Glowing(0xFFFFFF);
	private Set<Integer> affectedCells = new HashSet<>();
	private Set<Integer> visualCells = new HashSet<>();
	private Map<Integer, Float> spreadStrength = new HashMap<>();
	private int direction;

	{
		image = EquipmentWandBasicWandDict.WAND_SPS_FIREBOLT;
		collisionProperties = Ballistica.STOP_SOLID;
	}

	@Override public ItemSprite.Glowing glowing() { return WHITE; }

	@Override
	public int min(int level) {
		return Math.round((1 + level) * castMultiplier(chargesPerCast()));
	}

	@Override
	public int max(int level) {
		return Math.round((5 + 3 * level) * castMultiplier(chargesPerCast()));
	}

	public static float castMultiplier(int charges) {
		return (float)Math.pow(1.5f, Math.max(0, charges - 1));
	}

	public static int chargeCost(int currentCharges) {
		return Math.max(1, (int)Math.ceil(currentCharges * 0.3f));
	}

	public static int maximumDistance(int charges) {
		return (int)(4 * Math.pow(1.5f, Math.max(0, charges - 1)));
	}

	@Override
	public void onZap(Ballistica bolt) {
		for (int cell : affectedCells) {
			if (cell == bolt.sourcePos || !Dungeon.level.insideMap(cell)) continue;
			if (!Dungeon.level.adjacent(bolt.sourcePos, cell) || Dungeon.level.flamable[cell]) {
				GameScene.add(Blob.seed(cell, 1 + chargesPerCast(), Fire.class));
			}
			Char target = Actor.findChar(cell);
			if (target != null) {
				wandProc(target, chargesPerCast());
				target.damage(damageRoll(), this);
				if (target.isActive()) {
					Buff.affect(target, Burning.class).reignite(target);
					if (chargesPerCast() == 2) Buff.affect(target, Cripple.class, 4f);
					else if (chargesPerCast() >= 3) Buff.affect(target, Paralysis.class, 4f);
				}
			}
		}
	}

	public Set<Integer> prepareFlameCells(Ballistica bolt) {
		affectedCells = new HashSet<>();
		visualCells = new HashSet<>();
		spreadStrength = new HashMap<>();
		int maxDistance = maximumDistance(chargesPerCast());
		int distance = Math.min(bolt.dist, maxDistance);
		if (distance < 1 || bolt.path.size() < 2) return new HashSet<>(affectedCells);

		for (int i = 0; i < PathFinder.NEIGHBOURS8.length; i++) {
			if (bolt.sourcePos + PathFinder.NEIGHBOURS8[i] == bolt.path.get(1)) {
				direction = i;
				break;
			}
		}

		float strength = maxDistance;
		for (int cell : bolt.subPath(1, distance)) {
			strength--;
			affectedCells.add(cell);
			if (strength > 1f) {
				spreadFlames(cell + PathFinder.NEIGHBOURS8[left(direction)], strength - 1f);
				spreadFlames(cell + PathFinder.NEIGHBOURS8[direction], strength - 1f);
				spreadFlames(cell + PathFinder.NEIGHBOURS8[right(direction)], strength - 1f);
			} else {
				visualCells.add(cell);
			}
		}
		visualCells.remove(bolt.path.get(distance));
		return new HashSet<>(affectedCells);
	}

	private void spreadFlames(int cell, float strength) {
		if (!Dungeon.level.insideMap(cell)) return;
		Float previousStrength = spreadStrength.get(cell);
		if (previousStrength != null && previousStrength >= strength) return;
		spreadStrength.put(cell, strength);
		if (strength >= 0f && (Dungeon.level.passable[cell] || Dungeon.level.flamable[cell])) {
			affectedCells.add(cell);
			if (strength >= 1.5f) {
				visualCells.remove(cell);
				spreadFlames(cell + PathFinder.NEIGHBOURS8[left(direction)], strength - 1.5f);
				spreadFlames(cell + PathFinder.NEIGHBOURS8[direction], strength - 1.5f);
				spreadFlames(cell + PathFinder.NEIGHBOURS8[right(direction)], strength - 1.5f);
			} else {
				visualCells.add(cell);
			}
		} else if (!Dungeon.level.passable[cell]) {
			visualCells.add(cell);
		}
	}

	private static int left(int direction) { return direction == 0 ? 7 : direction - 1; }
	private static int right(int direction) { return direction == 7 ? 0 : direction + 1; }

	@Override
	public void fx(Ballistica bolt, Callback callback) {
		prepareFlameCells(bolt);
		int distance = Math.min(bolt.dist, maximumDistance(chargesPerCast()));
		if (distance < 1) {
			callback.call();
			return;
		}
		for (int cell : visualCells) {
			MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.FIRE_CONE,
					curUser.sprite, cell, null);
		}
		MagicMissile.boltFromChar(curUser.sprite.parent, MagicMissile.FIRE_CONE,
				curUser.sprite, bolt.path.get(distance), callback);
		Sample.INSTANCE.play(Assets.Sounds.ZAP);
	}

	@Override protected int chargesPerCast() { return chargeCost(curCharges); }

	@Override
	public String statsDesc() {
		return levelKnown
				? Messages.get(this, "stats_desc", chargesPerCast(), min(), max())
				: Messages.get(this, "stats_desc", chargesPerCast(), min(0), max(0));
	}

	@Override
	public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
		// SPS-PD predates battlemage wand-on-hit effects.
	}
}
