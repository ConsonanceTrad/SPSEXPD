/*
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * Pet families adapted from Special Surprise Pixel Dungeon.
 * Distributed under the GNU General Public License v3 or later.
 */

package pd.actors.mobs.npcs;

import pd.Dungeon;
import pd.actors.Char;
import pd.actors.buffs.Blindness;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Cripple;
import pd.actors.buffs.Poison;
import pd.mechanics.Ballistica;
import pd.messages.Messages;
import pd.sprites.BatSprite;
import pd.sprites.CrabSprite;
import pd.sprites.ElementalSprite;
import pd.sprites.PiranhaSprite;
import pd.sprites.RatSprite;
import pd.sprites.SheepSprite;
import pd.sprites.SpinnerSprite;
import pd.sprites.WraithSprite;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public class FusionPet extends DirectableAlly {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(FusionPet.class)
			.t("name_0", "缎带鼠")
			.t("name_1", "月兔")
			.t("name_2", "温顺蟹")
			.t("name_3", "沼泽蛙")
			.t("name_4", "猎犬")
			.t("name_5", "蓝猫")
			.t("name_6", "灵猴")
			.t("name_7", "陆行鸟")
			.t("name_8", "蝶灵")
			.t("name_9", "幼蛛")
			.t("name_10", "灵蛇")
			.t("name_11", "迅猛鸡")
			.t("name_12", "幼龙")
			.t("name_13", "星之子")
			.t("name_14", "灯魔")
			.t("name_15", "哈罗精灵")
			.t("role_0", "护卫型：生命与防御较高，但伤害较低。")
			.t("role_1", "突击型：近战伤害较高，但生命较低。")
			.t("role_2", "远射型：可以隔着直线攻击，远射伤害降至四分之三。")
			.t("role_3", "支援型：基础能力均衡，偶尔使敌人中毒。")
			.t("desc", "来自特别惊喜宠物谱系的同行伙伴。%s伙伴数值只随英雄等级有限成长，不会生成物品或经验。");
	}


	public static final int TYPE_COUNT = 16;

	private int type;

	{
		EXP = 0;
		maxLvl = -1;
		state = HUNTING;
	}

	public FusionPet configure(int type) {
		this.type = Math.max(0, Math.min(TYPE_COUNT - 1, type));
		setSprite();
		updateStats(true);
		return this;
	}

	public int type() {
		return type;
	}

	private int role() {
		return type % 4;
	}

	private void setSprite() {
		switch (type % 8) {
			case 0: spriteClass = RatSprite.class; break;
			case 1: spriteClass = SheepSprite.class; break;
			case 2: spriteClass = CrabSprite.class; break;
			case 3: spriteClass = PiranhaSprite.class; break;
			case 4: spriteClass = BatSprite.class; break;
			case 5: spriteClass = SpinnerSprite.class; break;
			case 6: spriteClass = WraithSprite.class; break;
			default: spriteClass = ElementalSprite.Fire.class;
		}
	}

	private void updateStats(boolean refill) {
		int level = Dungeon.hero == null ? 1 : Dungeon.hero.lvl;
		int oldHT = HT;
		HT = 10 + 2 * level;
		if (role() == 0) HT += 6 + level / 2;
		if (role() == 1) HT -= 2;
		defenseSkill = 5 + level + (role() == 0 ? 3 : 0);
		if (refill) HP = HT;
		else if (HT != oldHT) HP = Math.min(HT, HP + Math.max(0, HT - oldHT));
	}

	@Override
	protected boolean act() {
		updateStats(false);
		return super.act();
	}

	@Override
	public String name() {
		return Messages.get(this, "name_" + type);
	}

	@Override
	public String description() {
		return Messages.get(this, "desc", Messages.get(this, "role_" + role()));
	}

	@Override
	public int damageRoll() {
		int level = Dungeon.hero == null ? 1 : Dungeon.hero.lvl;
		int min = 1 + level / 6;
		int max = 3 + level / 3;
		if (role() == 1) {
			min++;
			max += 2;
		} else if (role() == 0) {
			max--;
		}
		return Random.NormalIntRange(min, Math.max(min, max));
	}

	@Override
	public int attackSkill(Char target) {
		return 10 + (Dungeon.hero == null ? 1 : Dungeon.hero.lvl);
	}

	@Override
	public int drRoll() {
		int level = Dungeon.hero == null ? 1 : Dungeon.hero.lvl;
		return Random.NormalIntRange(0, role() == 0 ? 2 + level / 7 : 1 + level / 10);
	}

	@Override
	protected boolean canAttack(Char enemy) {
		return super.canAttack(enemy) || role() == 2
				&& new Ballistica(pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
	}

	@Override
	protected boolean doAttack(Char enemy) {
		if (role() == 2 && !Dungeon.level.adjacent(pos, enemy.pos)) {
			if (sprite != null && (sprite.visible || enemy.sprite.visible)) {
				sprite.zap(enemy.pos);
				return false;
			}
			rangedAttack();
			return true;
		}
		return super.doAttack(enemy);
	}

	private void rangedAttack() {
		spend(TICK);
		if (enemy != null && enemy.isAlive() && hit(this, enemy, true)) {
			enemy.damage(Math.max(1, Math.round(damageRoll() * 0.75f)), this);
		}
	}

	public void onZapComplete() {
		rangedAttack();
		next();
	}

	@Override
	public int attackProc(Char enemy, int damage) {
		damage = super.attackProc(enemy, damage);
		if (Random.Int(8) == 0) {
			if (role() == 1) Buff.affect(enemy, Cripple.class, 1f);
			else if (role() == 2) Buff.affect(enemy, Blindness.class, 1f);
			else if (role() == 3) Buff.affect(enemy, Poison.class).set(2f);
		}
		return damage;
	}

	public int feed() {
		int missing = HT - HP;
		int healed = Math.min(missing, Math.max(5, HT / 2));
		HP += healed;
		return healed;
	}

	private static final String TYPE = "fusion_pet_type";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(TYPE, type);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		type = Math.max(0, Math.min(TYPE_COUNT - 1, bundle.getInt(TYPE)));
		setSprite();
		updateStats(false);
	}
}
