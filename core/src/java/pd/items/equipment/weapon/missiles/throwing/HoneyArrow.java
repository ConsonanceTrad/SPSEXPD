/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.ConsumThrowsDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.Bee;
import pd.actors.mobs.Mob;
import pd.effects.Pushing;
import pd.effects.Splash;
import pd.items.Honeypot;
import pd.items.Item;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.noosa.tweeners.AlphaTweener;
import render.utils.math.Random;

import java.util.ArrayList;
import pd.messages.InlineText;

/** Honey Poooot's disposable needle, which summons bees around its target. */
public class HoneyArrow extends TossWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(HoneyArrow.class)
			.t("name", "蜜蜂针头")
			.t("desc", "罐罐开发的新型飞镖，可以呼唤一群蜜蜂。");
	}




	{
		image = ConsumThrowsDict.HONEY_ARROW;
		tier = 1;
		baseUses = 1;
		bones = false;
	}

	public HoneyArrow() { this(2); }
	public HoneyArrow(int number) { quantity = number; }

	@Override public int min(int level) { return 1; }
	@Override public int max(int level) { return 1; }
	@Override public int STRReq(int level) { return 10; }

	@Override
	protected void onThrow(int cell) {
		if (Actor.findChar(cell) == null) shatter(null, cell);
		else super.onThrow(cell);
	}

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		for (int offset : PathFinder.NEIGHBOURS4) {
			int cell = defender.pos + offset;
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
					&& Actor.findChar(cell) == null) shatter(null, cell);
		}
		return super.proc(attacker, defender, damage);
	}

	@Override
	public Item random() {
		quantity = Random.Int(1, 2);
		return this;
	}

	@Override public int value() { return 20 * quantity; }

	public Mob createBee() {
		if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.LEADER) {
			Honeypot.SteelBee bee = new Honeypot.SteelBee();
			bee.spawn(Dungeon.legacyDepth());
			return bee;
		}
		Bee bee = new Bee();
		bee.spawn(Dungeon.legacyDepth());
		return bee;
	}

	public Mob shatter(Char owner, int pos) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(pos)) return null;
		if (Game.instance != null && Dungeon.level.heroFOV[pos]) {
			Sample.INSTANCE.play(Assets.Sounds.SHATTER);
			Splash.at(pos, 0xffd500, 5);
		}

		int newPos = pos;
		if (Actor.findChar(pos) != null) {
			ArrayList<Integer> candidates = new ArrayList<>();
			for (int offset : PathFinder.NEIGHBOURS4) {
				int cell = pos + offset;
				if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
						&& Actor.findChar(cell) == null) candidates.add(cell);
			}
			newPos = candidates.isEmpty() ? -1 : Random.element(candidates);
		}
		if (newPos == -1) return null;

		Mob bee = createBee();
		if (bee instanceof Bee) ((Bee)bee).setPotInfo(pos, owner);
		bee.HP = bee.HT;
		bee.pos = newPos;
		GameScene.add(bee);
		Dungeon.level.occupyCell(bee);
		if (newPos != pos) Actor.add(new Pushing(bee, pos, newPos));
		if (bee.sprite != null) {
			bee.sprite.alpha(0);
			if (bee.sprite.parent != null) bee.sprite.parent.add(new AlphaTweener(bee.sprite, 1, 0.15f));
		}
		if (Game.instance != null) Sample.INSTANCE.play(Assets.Sounds.BEE);
		return bee;
	}
}
