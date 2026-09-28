package com.shatteredpixel.shatteredpixeldungeon.items.misc;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Arcane;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArmorBreak;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SkillRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.TownNpc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

public final class SpsTownMiscTest {

	private static final String[] ICON_HASHES = {
			"5C722EC16FD5400158D662FFA7738E0BAC805E12C6474FA046C68ACFC219DE8B",
			"B28E1780E1B9BC8229BB6D4DD19F533BCB4E772F17637C77F304539E2685DD4F",
			"F162777E5A70AA3D826902C9DBE40C8C87B55A7DA59AB1D1166EADBA56D48C54",
			"1C02E76E45079063D9A00825538097A99B26CE84C57A3478CEDB2E7FFAF890C5"
	};

	public static void main(String[] args) throws Exception {
		try {
			testSourcesAndProperties();
			testFishBone();
			testGhostGirlRose();
			testRainShield();
			testCursePhone();
			testIcons();
			System.out.println("SPS城镇饰品测试通过：4件饰品、NPC来源、被动效果、售价和原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
		}
	}

	private static void testSourcesAndProperties() {
		TownNpc.Spec[] specs = {TownNpc.Spec.ALIVE_FISH, TownNpc.Spec.WHITE_GHOST,
				TownNpc.Spec.RAIN_TRAINER, TownNpc.Spec.RENNPC};
		Class<?>[] rewards = {FishBone.class, GhostGirlRose.class, RainShield.class, CursePhone.class};
		Char.Property[] properties = {Char.Property.BEAST, Char.Property.UNKNOW,
				Char.Property.ELEMENT, Char.Property.ELF};
		for (int i = 0; i < specs.length; i++) {
			TownNpc npc = new TownNpc().configure(specs[i]);
			check(rewards[i].isInstance(npc.SupercreateLoot()), specs[i] + "特殊掉落错误");
			check(npc.properties().contains(properties[i]), specs[i] + "旧版属性缺失");
		}
		check(new FishBone().value() == 500 && new GhostGirlRose().value() == 500
				&& new RainShield().value() == 500 && new CursePhone().value() == 300,
				"城镇饰品售价错误");
	}

	private static void testFishBone() {
		Hero hero = hero();
		FishBone bone = new FishBone();
		FishBone.FishFriend friend = bone.createBuff();
		check(friend.attachTo(hero), "鱼骨被动无法附着");
		check(FishBone.waterSpeedMultiplier(hero, true) == 2f
				&& FishBone.waterSpeedMultiplier(hero, false) == 1f, "鱼骨水中双倍移速错误");
		Fisher fisher = new Fisher();
		check(FishBone.protectsFrom(hero, fisher) && !FishBone.protectsFrom(hero, new PlainMob()),
				"鱼骨渔系免伤目标判断错误");
	}

	private static void testGhostGirlRose() {
		Hero hero = hero();
		GhostGirlRose rose = new GhostGirlRose();
		check(GhostGirlRose.experienceBonus(hero) == 0, "未装备幽魂余香时仍有经验加成");
		check(rose.createBuff().attachTo(hero) && GhostGirlRose.experienceBonus(hero) == 2,
				"幽魂余香没有提供每次2点经验加成");
	}

	private static void testRainShield() {
		Hero hero = hero();
		hero.HP = 50;
		RainShield shield = new RainShield();
		RainShield.RainShieldBuff buff = shield.createBuff();
		check(buff.attachTo(hero), "Rain盾被动无法附着");
		buff.act();
		check(hero.HP == 49 && hero.buff(ShieldArmor.class) != null
				&& hero.buff(ShieldArmor.class).level() == 51, "Rain盾没有按缺失生命生成物理护盾");
		hero.HP = 10;
		buff.act();
		check(hero.HP == 10, "Rain盾把生命扣到了10%以下");
	}

	private static void testCursePhone() {
		Hero hero = hero();
		CursePhone phone = new CursePhone();
		check(phone.cursed, "诅咒电话初始时没有诅咒");
		phone.applyCurseEffects(hero);
		check(hero.buff(Terror.class) != null && hero.buff(Terror.class).object == hero.id()
				&& hero.buff(Vertigo.class) != null && hero.buff(ArmorBreak.class) != null
				&& hero.buff(ArmorBreak.class).level() == 30 && hero.buff(Arcane.class) != null
				&& hero.buff(SkillRecharge.class) != null, "诅咒电话五种状态不完整");
	}

	private static Hero hero() {
		Actor.clear();
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static void testIcons() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		for (int i = 0; i < ICON_HASHES.length; i++) {
			ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
			for (int y = 816; y < 832; y++) for (int x = 144 + i * 16; x < 160 + i * 16; x++) {
				pixels.putInt(sheet.getRGB(x, y));
			}
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
			StringBuilder actual = new StringBuilder(64);
			for (byte value : digest) actual.append(String.format("%02X", value & 0xFF));
			check(ICON_HASHES[i].equals(actual.toString()), "城镇饰品第" + (i + 1) + "个图标不一致：" + actual);
		}
	}

	private static class Fisher extends Mob {
		{ properties.add(Property.FISHER); }
	}
	private static class PlainMob extends Mob { }

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsTownMiscTest() { }
}
