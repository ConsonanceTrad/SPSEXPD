/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.actors.mobs;

import pd.Dungeon;
import pd.Statistics;
import pd.actors.Char;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.items.Gold;
import pd.items.Item;
import pd.messages.Messages;
import pd.sprites.CharSprite;
import pd.sprites.GoldThiefSprite;
import pd.utils.GLog;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class GoldThief extends Mob {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(GoldThief.class)
			.t("name", "黄金小盗")
			.t("desc", "不同于其他小偷，它只关心闪闪发光的金币，并会在得手后立刻逃跑。")
			.t("stole", "黄金小盗从你那里偷走了%d枚金币！")
			.t("killcount", "已击败黄金小盗：%d");
	}




	public Item item;
	private int goldToDrop;

	{
		spriteClass = GoldThiefSprite.class;
		HP = HT = 100 + Statistics.goldThievesKilled;
		defenseSkill = 26;
		EXP = 1;
		lootChance = 1f;
		FLEEING = new Fleeing();
		properties.add(Property.ELF);
	}

	@Override public int damageRoll() {
		return Random.NormalIntRange(20, 30 + Statistics.goldThievesKilled / 2);
	}

	@Override public float attackDelay() { return 0.75f; }
	@Override public int attackSkill(Char target) { return 40; }
	@Override public int drRoll() { return 14 + Statistics.goldThievesKilled; }

	@Override public int attackProc(Char enemy, int damage) {
		if (item == null && enemy instanceof Hero && steal((Hero)enemy)) state = FLEEING;
		return damage;
	}

	@Override public int defenseProc(Char enemy, int damage) {
		if (state == FLEEING) Dungeon.level.drop(new Gold(), pos).sprite.drop();
		return super.defenseProc(enemy, damage);
	}

	protected boolean steal(Hero hero) {
		if (Dungeon.gold <= 0) return false;
		goldToDrop = Math.min(Random.Int(100, 300) + 100, Dungeon.gold);
		Dungeon.gold -= goldToDrop;
		GLog.w(Messages.get(this, "stole", goldToDrop));
		return true;
	}

	@Override public Item createLoot() {
		return new Gold(Random.NormalIntRange(goldToDrop + 50, goldToDrop + 100));
	}

	@Override public void die(Object cause) {
		super.die(cause);
		Statistics.goldThievesKilled++;
		GLog.w(Messages.get(this, "killcount", Statistics.goldThievesKilled));
		if (item != null) Dungeon.level.drop(item, pos).sprite.drop();
		dropLegacyDew(pos);
		SpsChallengeKillRewards.goldThief(pos);
	}

	private class Fleeing extends Mob.Fleeing {
		@Override protected void nowhereToRun() {
			if (buff(Terror.class) == null) {
				if (sprite != null) sprite.showStatus(CharSprite.NEGATIVE, Messages.get(Mob.class, "rage"));
				state = HUNTING;
			} else {
				super.nowhereToRun();
			}
		}
	}

	private static final String ITEM = "item";
	private static final String GOLD_TO_DROP = "gold_to_drop";

	@Override public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(ITEM, item);
		bundle.put(GOLD_TO_DROP, goldToDrop);
	}

	@Override public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		item = (Item)bundle.get(ITEM);
		goldToDrop = bundle.getInt(GOLD_TO_DROP);
	}
}
