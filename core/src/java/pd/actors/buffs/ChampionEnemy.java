/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2026 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package pd.actors.buffs;

import pd.Challenges;
import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.blobs.Blob;
import pd.actors.blobs.Fire;
import pd.actors.mobs.Bat;
import pd.actors.mobs.Crab;
import pd.actors.mobs.Guard;
import pd.actors.mobs.Mob;
import pd.actors.mobs.Thief;
import pd.mechanics.pathfind.PathFinder;
import pd.messages.Messages;
import pd.scenes.GameScene;
import pd.ui.BuffIndicator;
import render.noosa.Image;
import render.utils.data.BArray;
import render.utils.math.Random;
import render.utils.serialize.Bundle;
import pd.messages.InlineText;

public abstract class ChampionEnemy extends Buff {
	//SPSEXPD: inline Chinese text (generated from messages/actors/zh)
	static {
		InlineText.of(ChampionEnemy.class)
			.t("warn", "你感受到了强敌的气场。")
			.t("$blazing.name", "烈焰精英")
			.t("$blazing.desc", "烈焰精英造成25%额外近战伤害，会点燃攻击目标，免疫火焰，且会在死亡时引燃周遭。不过，它们的火焰无法向水上蔓延。")
			.t("$projecting.name", "索敌精英")
			.t("$projecting.desc", "索敌精英拥有25%额外近战伤害，且拥有额外的3格近战攻击距离。")
			.t("$antimagic.name", "敌法精英")
			.t("$antimagic.desc", "敌法精英拥有50%的伤害减免，且完全免疫所有魔法效果。")
			.t("$giant.name", "巨型精英")
			.t("$giant.desc", "巨型精英拥有80%的伤害减免与额外的1格近战攻击距离，但无法进入狭窄的空间。")
			.t("$blessed.name", "天佑精英")
			.t("$blessed.desc", "天佑精英拥有4倍精准与闪避。")
			.t("$growing.name", "成长精英")
			.t("$growing.desc", "成长精英可额外获得稳定增长的精准、闪避、物理伤害与伤害减免。\n\n当前精准/闪避/伤害加成：%1$d%%\n当前伤害减免：%2$d%%");
	}




	{
		type = buffType.POSITIVE;
		revivePersists = true;
	}

	protected int color;
	protected int rays;

	@Override
	public int icon() {
		return BuffIndicator.CORRUPT;
	}

	@Override
	public void tintIcon(Image icon) {
		icon.hardlight(color);
	}

	@Override
	public void fx(boolean on) {
		if (on) target.sprite.aura( color, rays );
		else target.sprite.clearAura();
	}

	public void onAttackProc(Char enemy ){

	}

	public boolean canAttackWithExtraReach( Char enemy ){
		return false;
	}

	public float meleeDamageFactor(){
		return 1f;
	}

	public float damageTakenFactor(){
		return 1f;
	}

	public float evasionAndAccuracyFactor(){
		return 1f;
	}

	{
		immunities.add(AllyBuff.class);
	}

	public static void rollForChampion(Mob m){
		Dungeon.mobsToChampion--;

		//we roll for a champion enemy even if we aren't spawning one to ensure that
		//mobsToChampion does not affect levelgen RNG (number of calls to Random.Int() is constant)
		Class<?extends ChampionEnemy> buffCls;
		switch (Random.Int(6)){
			case 0: default:    buffCls = Blazing.class;      break;
			case 1:             buffCls = Projecting.class;   break;
			case 2:             buffCls = AntiMagic.class;    break;
			case 3:             buffCls = Giant.class;        break;
			case 4:             buffCls = Blessed.class;      break;
			case 5:             buffCls = Growing.class;      break;
		}

		if (Dungeon.mobsToChampion <= 0 && Dungeon.isChallenged(Challenges.CHAMPION_ENEMIES)) {

			//we block certain standout enemies on floor <10 from becoming champions
			if (m instanceof Crab  && Dungeon.scalingDepth() <= 3) return;
			if (m instanceof Thief && Dungeon.scalingDepth() <= 4) return;
			if (m instanceof Guard && Dungeon.scalingDepth() <= 7) return;
			if (m instanceof Bat   && Dungeon.scalingDepth() <= 9) return;

			Buff.affect(m, buffCls);
			//numbers of mobs until a champion scales from 1/8 to 1/6 as depths increases
			Dungeon.mobsToChampion += 8 - Math.min(20, Dungeon.scalingDepth()-1)/10f;
			if (m.state != m.PASSIVE) {
				m.state = m.WANDERING;
			}
		}
	}

	public static class Blazing extends ChampionEnemy {

		{
			color = 0xFF8800;
			rays = 4;
		}

		@Override
		public void onAttackProc(Char enemy) {
			if (!Dungeon.level.water[enemy.pos]) {
				Buff.affect(enemy, Burning.class).reignite(enemy);
			}
		}

		@Override
		public void detach() {
			//don't trigger when killed by being knocked into a pit
			if (target.flying || !Dungeon.level.pit[target.pos]) {
				for (int i : PathFinder.NEIGHBOURS9) {
					if (!Dungeon.level.solid[target.pos + i] && !Dungeon.level.water[target.pos + i]) {
						GameScene.add(Blob.seed(target.pos + i, 2, Fire.class));
					}
				}
			}
			super.detach();
		}

		@Override
		public float meleeDamageFactor() {
			return 1.25f;
		}

		{
			immunities.add(Burning.class);
		}
	}

	public static class Projecting extends ChampionEnemy {

		{
			color = 0x8800FF;
			rays = 4;
		}

		@Override
		public float meleeDamageFactor() {
			return 1.25f;
		}

		@Override
		public boolean canAttackWithExtraReach(Char enemy) {
			if (Dungeon.level.distance( target.pos, enemy.pos ) > 4){
				return false;
			} else {
				boolean[] passable = BArray.not(Dungeon.level.solid, null);
				for (Char ch : Actor.chars()) {
					//our own tile is always passable
					passable[ch.pos] = ch == target;
				}

				PathFinder.buildDistanceMap(enemy.pos, passable, 4);

				return PathFinder.distance[target.pos] <= 4;
			}
		}
	}

	public static class AntiMagic extends ChampionEnemy {

		{
			color = 0x00FF00;
			rays = 5;
		}

		@Override
		public float damageTakenFactor() {
			return 0.5f;
		}

		{
			immunities.addAll(pd.items.equipment.armor.glyphs.AntiMagic.RESISTS);
		}

	}

	//Also makes target large, see Char.properties()
	public static class Giant extends ChampionEnemy {

		{
			color = 0x0088FF;
			rays = 5;
		}

		@Override
		public float damageTakenFactor() {
			return 0.2f;
		}

		@Override
		public boolean canAttackWithExtraReach(Char enemy) {
			if (Dungeon.level.distance( target.pos, enemy.pos ) > 2){
				return false;
			} else {
				boolean[] passable = BArray.not(Dungeon.level.solid, null);
				for (Char ch : Actor.chars()) {
					//our own tile is always passable
					passable[ch.pos] = ch == target;
				}

				PathFinder.buildDistanceMap(enemy.pos, passable, 2);

				return PathFinder.distance[target.pos] <= 2;
			}
		}
	}

	public static class Blessed extends ChampionEnemy {

		{
			color = 0xFFFF00;
			rays = 6;
		}

		@Override
		public float evasionAndAccuracyFactor() {
			return 4f;
		}
	}

	public static class Growing extends ChampionEnemy {

		{
			color = 0xFF2222; //a little white helps it stick out from background
			rays = 6;
		}

		private float multiplier = 1.19f;

		@Override
		public boolean act() {
			multiplier += 0.01f;
			spend(4*TICK);
			return true;
		}

		@Override
		public float meleeDamageFactor() {
			return multiplier;
		}

		@Override
		public float damageTakenFactor() {
			return 1f/multiplier;
		}

		@Override
		public float evasionAndAccuracyFactor() {
			return multiplier;
		}

		@Override
		public String desc() {
			return Messages.get(this, "desc", (int)(100*(multiplier-1)), (int)(100*(1 - 1f/multiplier)));
		}

		private static final String MULTIPLIER = "multiplier";

		@Override
		public void storeInBundle(Bundle bundle) {
			super.storeInBundle(bundle);
			bundle.put(MULTIPLIER, multiplier);
		}

		@Override
		public void restoreFromBundle(Bundle bundle) {
			super.restoreFromBundle(bundle);
			multiplier = bundle.getFloat(MULTIPLIER);
		}
	}

}
