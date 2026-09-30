package pd.items.sellitem;

import pd.Dungeon;
import pd.actors.Actor;
import pd.actors.Char;
import pd.actors.buffs.Arcane;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.BerryRegeneration;
import pd.actors.buffs.DefenceUp;
import pd.actors.buffs.Invisibility;
import pd.actors.hero.Hero;
import pd.actors.mobs.npcs.TownNpc;
import pd.sprites.ItemSpriteSheet;
import watabou.noosa.Game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

public final class SpsApostleBoxTest {

	private static final String ICON_HASH =
			"82765C125C5C9CF9B5E369FD65257A2D4AADBC5E23268B6B6A53392D68DF09E2";

	public static void main(String[] args) throws Exception {
		Game.version = "test";
		try {
			testItemAndSource();
			testEffects();
			testConsumption();
			testIcon();
			System.out.println("SPS混沌之盒测试通过：四种效果、时长、单件消耗、Apostle掉落、属性和原始图标均正常。");
		} finally {
			Actor.clear();
			Dungeon.hero = null;
		}
	}

	private static void testItemAndSource() {
		ApostleBox box = new ApostleBox();
		box.quantity(3);
		check(box.stackable && box.value() == 360 && !box.isUpgradable() && box.isIdentified(),
				"混沌之盒堆叠、售价或鉴定属性错误");
		check(box.image == ItemSpriteSheet.APOSTLE_BOX
				&& box.actions(hero()).contains(ApostleBox.AC_APPLY), "混沌之盒动作或图标槽位错误");
		TownNpc apostle = new TownNpc().configure(TownNpc.Spec.APOSTLE);
		check(apostle.SupercreateLoot() instanceof ApostleBox, "Apostle没有掉落混沌之盒");
		check(apostle.properties().contains(Char.Property.MECH)
				&& apostle.properties().contains(Char.Property.ELEMENT), "Apostle旧版机械或元素属性缺失");
	}

	private static void testEffects() {
		ApostleBox box = new ApostleBox();
		Hero red = hero(); box.applyEffect(red, 0);
		check(red.buff(AttackUp.class) != null && red.buff(AttackUp.class).level() == 50
				&& red.buff(AttackUp.class).cooldown() == 50f, "红光未给予50级、50回合攻击强化");
		Hero green = hero(); box.applyEffect(green, 1);
		check(green.buff(BerryRegeneration.class) != null, "绿光未给予50级莓果恢复");
		Hero blue = hero(); box.applyEffect(blue, 2);
		check(blue.buff(DefenceUp.class) != null && blue.buff(DefenceUp.class).level() == 50
				&& blue.buff(DefenceUp.class).cooldown() == 50f, "蓝光未给予50级、50回合防御强化");
		Hero violet = hero(); box.applyEffect(violet, 3);
		check(violet.buff(Invisibility.class) != null && violet.buff(Invisibility.class).cooldown() == 50f
				&& violet.buff(Arcane.class) != null && violet.buff(Arcane.class).cooldown() == 10f,
				"紫光未给予50回合隐身和10回合奥术");
	}

	private static void testConsumption() {
		Hero hero = hero();
		ApostleBox box = new ApostleBox();
		box.quantity(2);
		check(box.collect(hero.belongings.backpack), "混沌之盒无法放入背包");
		box.activate(hero, 0);
		check(box.quantity() == 1 && hero.buff(AttackUp.class) != null && hero.cooldown() == 1f,
				"混沌之盒没有消耗单件、施加效果或耗时1回合");
	}

	private static Hero hero() {
		Actor.clear();
		Hero hero = new Hero();
		hero.HP = hero.HT = 100;
		Dungeon.hero = hero;
		return hero;
	}

	private static void testIcon() throws Exception {
		BufferedImage sheet = ImageIO.read(new File("sprites/items/items.png"));
		ByteBuffer pixels = ByteBuffer.allocate(16 * 16 * 4).order(ByteOrder.LITTLE_ENDIAN);
		for (int y = 848; y < 864; y++) for (int x = 224; x < 240; x++) pixels.putInt(sheet.getRGB(x, y));
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(pixels.array());
		StringBuilder actual = new StringBuilder(64);
		for (byte value : digest) actual.append(String.format("%02X", value & 0xFF));
		check(ICON_HASH.equals(actual.toString()), "混沌之盒图标与旧版像素不一致：" + actual);
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsApostleBoxTest() { }
}
