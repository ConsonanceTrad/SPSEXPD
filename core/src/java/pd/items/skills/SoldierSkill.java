/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.skills;

import pd.atlas.items.EquipmentEquipWeaponBombDict;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.*;
import pd.actors.hero.Hero;
import pd.actors.hero.HeroSubClass;
import pd.actors.mobs.npcs.MirrorImage;
import pd.items.Generator;
import pd.items.Item;
import pd.items.equipment.bombs.DungeonBomb;
import pd.items.consum.scrolls.ScrollOfTeleportation;
import pd.items.summon.ActiveMrDestructo;
import pd.items.summon.FairyCard;
import pd.items.summon.Mobile;
import pd.mechanics.pathfind.PathFinder;
import pd.scenes.GameScene;
import pd.sprites.BMirrorSprite;
import render.utils.math.Random;
import render.utils.serialize.Bundle;

import java.util.ArrayList;
import pd.messages.InlineText;

/** The four soldier class skills from SPS-PD 0.9.8. */
public class SoldierSkill extends ClassSkill {
	//SPSEXPD: inline Chinese text (generated from messages/items/zh)
	static {
		InlineText.of(SoldierSkill.class)
			.t("name", "星兵技能")
			.t("ac_special", "自爆幻影")
			.t("ac_special_two", "召唤机甲")
			.t("ac_special_three", "战斗号召")
			.t("ac_special_four", "空投补给")
			.t("desc", "_自爆幻影：_生成两个会在被摧毁时爆炸的战斗幻影。达到56级后同时清除部分负面状态。\n\n_召唤机甲（21级）：_获得机械护甲和物理护盾。达到56级后两者翻倍。\n\n_战斗号召（31级）：_获得随机物品或召唤物。达到56级后必定获得召唤物。\n\n_空投补给（41级）：_向自身四角投放投射武器和食物。达到56级后食物品质提高。")
			.t("$seekingbomb.name", "砰砰机器人")
			.t("$seekingbomb.desc", "携带炸药的战斗幻影，被摧毁时会发生爆炸。")
			.t("$seekinghugebomb.name", "砰砰砰砰机器人")
			.t("$seekinghugebomb.desc", "领袖强化过的自爆幻影，被摧毁时会连续爆炸两次。");
	}



	{ image = EquipmentEquipWeaponBombDict.BOMB_0; }

	@Override public void doSpecial() {
		ArrayList<Integer> cells = new ArrayList<>();
		for (int offset : PathFinder.NEIGHBOURS8) {
			int cell = curUser.pos + offset;
			if (Dungeon.level.insideMap(cell) && Actor.findChar(cell) == null
					&& (Dungeon.level.passable[cell] || Dungeon.level.avoid[cell])) cells.add(cell);
		}
		for (int count = 0; count < 2 && !cells.isEmpty(); count++) {
			int index = Random.index(cells);
			SeekingBomb bomb = curUser.subClass == HeroSubClass.LEADER ? new SeekingHugeBomb() : new SeekingBomb();
			bomb.duplicate(curUser);
			bomb.HP = bomb.HT = 100;
			bomb.pos = cells.remove(index);
			GameScene.add(bomb);
			if (bomb.sprite != null) ScrollOfTeleportation.appear(bomb, bomb.pos);
		}
		if (curUser.lvl > 55) {
			Buff.detach(curUser, Poison.class);
			Buff.detach(curUser, Cripple.class);
			Buff.detach(curUser, STRDown.class);
			Buff.detach(curUser, Burning.class);
			Buff.detach(curUser, Ooze.class);
			Buff.detach(curUser, Chill.class);
			Buff.prolong(curUser, Bless.class, 5f);
		}
		addCooldown(12);
		finishSkillCast();
	}

	@Override public void doSpecial2() {
		Buff.affect(curUser, MechArmor.class).level(curUser.lvl > 55 ? 600 : 300);
		Buff.affect(curUser, ShieldArmor.class).level(curUser.lvl * (curUser.lvl > 55 ? 6 : 3));
		addCooldown(25);
		finishSkillCast();
	}

	@Override public void doSpecial3() {
		Item summon;
		switch (Random.Int(3)) {
			case 0: summon = new FairyCard(); break;
			case 1: summon = new Mobile(); break;
			default: summon = new ActiveMrDestructo(); break;
		}
		dropAtHero(curUser.lvl > 55 || Random.Int(2) == 0 ? summon : Generator.random());
		addCooldown(15);
		finishSkillCast();
	}

	@Override public void doSpecial4() {
		int width = Dungeon.level.width();
		int[] diagonals = {width + 1, width - 1, -width + 1, -width - 1};
		for (int offset : diagonals) {
			int cell = curUser.pos + offset;
			if (!Dungeon.level.insideMap(cell)) continue;
			Item missile = Generator.random(Generator.Category.MISSILE);
			if (missile != null) Dungeon.level.drop(missile, cell);
			Generator.Category food = curUser.lvl > 55
					? Generator.Category.HIGHFOOD
					: Generator.Category.FOOD;
			Item meal = Generator.random(food);
			if (meal != null) Dungeon.level.drop(meal, cell);
		}
		Buff.prolong(curUser, Awareness.class, 5f);
		addCooldown(20);
		finishSkillCast();
	}

	public static class SeekingBomb extends MirrorImage {
		{ spriteClass = BMirrorSprite.class; }
		@Override public int damageRoll() { return Dungeon.hero.damageRoll(); }
		@Override public int attackSkill(Char target) { return 1000; }
		@Override public int drRoll() { return Dungeon.hero.drRoll(); }
		@Override public void die(Object cause) {
			new DungeonBomb().explode(pos);
			super.die(cause);
		}
	}

	public static class SeekingHugeBomb extends SeekingBomb {
		@Override public void die(Object cause) {
			new DungeonBomb().explode(pos);
			super.die(cause);
		}
	}
}
