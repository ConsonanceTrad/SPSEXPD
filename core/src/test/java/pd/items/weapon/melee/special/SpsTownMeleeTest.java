package pd.items.weapon.melee.special;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Amok;
import pd.actors.buffs.Buff;
import pd.actors.buffs.Charm;
import pd.actors.buffs.HolyStun;
import pd.actors.buffs.Paralysis;
import pd.actors.buffs.Terror;
import pd.actors.hero.Hero;
import pd.actors.mobs.Mob;
import pd.actors.mobs.npcs.TownNpc;
import pd.items.weapon.enchantments.Blazing;
import pd.items.weapon.ranges.RangePan;
import pd.sprites.ItemSpriteSheet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

public final class SpsTownMeleeTest {

	private static final String[] ICON_HASHES = {
			"BC0288C1A9054EA04529C952CBB2CE4D2CD63B2AD4130F2D307DFC5CD8EFD9F9",
			"0DEEC0C8775CCA01DA2BF871FD8E2D738B14468B45D367B0E4703C0E94F5633E"
	};

	public static void main(String[] args) throws Exception {
		try {
			testSourcesAndStats();
			testSockEffects();
			testPanForms();
			testIcons();
			System.out.println("SPS城镇近战奖励测试通过：袜子四状态、煎锅双形态、状态传递、NPC来源和原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
		}
	}

	private static void testSourcesAndStats() {
		TownNpc afly = new TownNpc().configure(TownNpc.Spec.A_FLY);
		check(afly.SupercreateLoot() instanceof AFlySock
				&& afly.properties().contains(Char.Property.ELF)
				&& afly.properties().contains(Char.Property.DEMONIC), "AFly掉落或属性错误");
		TownNpc coconut = new TownNpc().configure(TownNpc.Spec.COCONUT2);
		check(coconut.SupercreateLoot() instanceof MeleePan
				&& coconut.properties().contains(Char.Property.MECH), "Coconut2掉落或机械属性错误");
		AFlySock sock = new AFlySock();
		check(sock.min(0) == 1 && sock.max(0) == 5 && sock.min(3) == 4 && sock.max(3) == 8
				&& sock.STRReq(0) == 10 && sock.image == ItemSpriteSheet.AFLY_SOCK,
				"袜子的伤害成长、力量需求或图标错误");
		MeleePan pan = new MeleePan();
		check(pan.min(0) == 8 && pan.max(0) == 8 && pan.min(3) == 11 && pan.max(3) == 11
				&& pan.STRReq(0) == 10 && pan.image == ItemSpriteSheet.MELEE_PAN,
				"近战煎锅的伤害成长、力量需求或图标错误");
	}

	private static void testSockEffects() {
		Hero attacker = hero();
		AFlySock sock = new AFlySock();
		PlainMob[] targets = {new PlainMob(), new PlainMob(), new PlainMob(), new PlainMob()};
		for (PlainMob target : targets) Actor.add(target);
		sock.applyEffect(attacker, targets[0], 0);
		sock.applyEffect(attacker, targets[1], 1);
		sock.applyEffect(attacker, targets[2], 2);
		sock.applyEffect(attacker, targets[3], 3);
		check(targets[0].buff(Paralysis.class) != null, "袜子麻痹分支错误");
		check(targets[1].buff(Charm.class) != null && targets[1].buff(Charm.class).object == attacker.id(),
				"袜子魅惑分支错误");
		check(targets[2].buff(Terror.class) != null && targets[2].buff(Terror.class).object == attacker.id(),
				"袜子恐惧分支错误");
		check(targets[3].buff(Amok.class) != null, "袜子狂乱分支错误");
	}

	private static void testPanForms() {
		Hero hero = hero();
		MeleePan pan = new MeleePan();
		pan.upgrade(3);
		pan.enchant(new Blazing());
		pan.reinforced = true;
		pan.levelKnown = true;
		pan.cursedKnown = true;
		pan.cursed = true;
		hero.belongings.weapon = pan;
		check(pan.actions(hero).contains(MeleePan.AC_CHANGE), "装备近战煎锅时没有切换动作");
		RangePan ranged = pan.changeToRange(hero);
		check(hero.belongings.weapon() == ranged && ranged.trueLevel() == 3
				&& ranged.enchantment instanceof Blazing && ranged.reinforced
				&& ranged.levelKnown && ranged.cursedKnown && ranged.cursed,
				"切换远程煎锅时没有完整保留装备状态");
		check(ranged.min(0) == 4 && ranged.max(0) == 8 && ranged.STRReq(0) == 10,
				"远程煎锅基础数值错误");
		MeleePan restored = ranged.changeToMelee(hero);
		check(hero.belongings.weapon() == restored && restored.trueLevel() == 3
				&& restored.enchantment instanceof Blazing && restored.reinforced && restored.cursed,
				"切回近战煎锅时没有完整保留装备状态");
		PlainMob target = new PlainMob();
		Buff.prolong(target, HolyStun.class, 2f);
		check(target.buff(HolyStun.class) != null, "煎锅的2回合神圣眩晕状态不可用");
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
			for (int y = 816; y < 832; y++) for (int x = 208 + i * 16; x < 224 + i * 16; x++) {
				pixels.putInt(sheet.getRGB(x, y));
			}
			byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
			StringBuilder actual = new StringBuilder(64);
			for (byte value : digest) actual.append(String.format("%02X", value & 0xFF));
			check(ICON_HASHES[i].equals(actual.toString()), "城镇近战奖励第" + (i + 1) + "个图标不一致：" + actual);
		}
	}

	private static class PlainMob extends Mob { }

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsTownMeleeTest() { }
}
