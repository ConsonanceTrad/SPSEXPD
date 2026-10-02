/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles.throwing;

import pd.atlas.items.SpecificPlaceHolderDict;

import pd.Assets;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Charm;
import pd.actors.mobs.Mob;
import pd.effects.Pushing;
import pd.effects.Splash;
import pd.items.Item;
import pd.scenes.GameScene;
import pd.sprites.LynnSprite;
import render.noosa.Game;
import render.noosa.audio.Sample;
import render.noosa.tweeners.AlphaTweener;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;
import pd.atlas.items.ConsumSummorDict;

/** Lynn's cursed throwing doll and its remote hunter. */
public class LynnDoll extends TossWeapon {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(LynnDoll.class)
			.t("name", "梦瑶娃娃")
			.t("desc", "奇怪的玩偶，好像有不好的东西附在上面。")
			.t("$cursedoll.name", "诡异少女")
			.t("$cursedoll.desc", "和娃娃差不多的少女。");
	}




	{
		image = ConsumSummorDict.FAIRY_DOLL;
		tier = 1;
		baseUses = 1;
		bones = false;
	}

	public LynnDoll() { this(1); }
	public LynnDoll(int number) { quantity = number; }

	@Override public int min(int level) { return 1; }
	@Override public int max(int level) { return 1; }
	@Override public int STRReq(int level) { return 10; }

	@Override
	public int proc(Char attacker, Char defender, int damage) {
		shatter(defender, defender.pos);
		return super.proc(attacker, defender, damage);
	}

	@Override
	public Item random() {
		quantity = Random.Int(1, 2);
		return this;
	}

	@Override public int value() { return 20 * quantity; }

	public CurseDoll shatter(Char owner, int origin) {
		if (Dungeon.level == null || !Dungeon.level.insideMap(origin)) return null;
		if (Game.instance != null && Dungeon.level.heroFOV[origin]) {
			Sample.INSTANCE.play(Assets.Sounds.SHATTER);
			Splash.at(origin, 0xffd500, 5);
		}

		CurseDoll doll = new CurseDoll();
		int destination = randomOpenCell();
		if (destination < 0) return null;
		doll.setPotInfo(origin, owner);
		doll.HP = doll.HT;
		doll.pos = destination;
		GameScene.add(doll);
		Dungeon.level.occupyCell(doll);
		Actor.add(new Pushing(doll, origin, destination));
		if (doll.sprite != null) {
			doll.sprite.alpha(0);
			if (doll.sprite.parent != null) doll.sprite.parent.add(new AlphaTweener(doll.sprite, 1, 0.15f));
		}
		if (Game.instance != null) Sample.INSTANCE.play(Assets.Sounds.BEE);
		return doll;
	}

	private int randomOpenCell() {
		ArrayList<Integer> candidates = new ArrayList<>();
		for (int cell = 0; cell < Dungeon.level.length(); cell++) {
			if (Dungeon.level.insideMap(cell) && Dungeon.level.passable[cell]
					&& !Dungeon.level.pit[cell] && Actor.findChar(cell) == null) candidates.add(cell);
		}
		return candidates.isEmpty() ? -1 : Random.element(candidates);
	}

	public static int energyDamage(int heroLevel) {
		return Random.NormalIntRange(4 * heroLevel, 8 * heroLevel);
	}

	public static class CurseDoll extends Mob {

		private static final String POT_POS = "potpos";
		private static final String POT_HOLDER = "potholder";
		private int potPos;
		private int potHolder = -1;

		{
			spriteClass = LynnSprite.class;
			HT = 10000;
			defenseSkill = 0;
			viewDistance = 8;
			baseSpeed = 3f;
			flying = true;
			state = WANDERING;
			properties.add(Property.UNKNOW);
			immunities.add(Amok.class);
			immunities.add(Charm.class);
		}

		public void setPotInfo(int potPos, Char holder) {
			this.potPos = potPos;
			potHolder = holder == null ? -1 : holder.id();
		}

		public int potPos() { return potPos; }
		public int potHolder() { return potHolder; }
		@Override public int attackSkill(Char target) { return 1000; }
		@Override public int damageRoll() { return energyDamage(Math.max(1, Dungeon.hero.lvl)); }

		@Override
		public int attackProc(Char enemy, int damage) {
			int energy = damageRoll();
			if (enemy instanceof Mob) ((Mob)enemy).aggro(this);
			enemy.damage(energy,
					pd.actors.damagetype.DamageType.ENERGY_DAMAGE);
			if (energy > enemy.HP) {
				destroy();
				if (sprite != null) sprite.die();
			}
			return 0;
		}

		@Override
		protected Char chooseEnemy() {
			Actor holder = Actor.findById(potHolder);
			return holder instanceof Char ? (Char)holder : null;
		}

		@Override
		protected boolean getCloser(int target) {
			if (enemy != null && Actor.findById(potHolder) == enemy) {
				target = enemy.pos;
			} else if (potPos != -1 && (state == WANDERING
					|| Dungeon.level.distance(target, potPos) > 3)) {
				this.target = target = potPos;
			}
			return super.getCloser(target);
		}

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(POT_POS, potPos);
			bundle.put(POT_HOLDER, potHolder);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			potPos = bundle.getInt(POT_POS);
			potHolder = bundle.getInt(POT_HOLDER);
		}
	}
}
