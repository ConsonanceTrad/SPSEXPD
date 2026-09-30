package pd.items.eggs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessFiles;
import com.badlogic.gdx.utils.GdxNativesLoader;
import pd.actors.mobs.pets.*;
import pd.actors.Char;
import pd.actors.blobs.CorruptGas;
import pd.actors.blobs.NmGas;
import pd.actors.blobs.ToxicGas;
import pd.actors.blobs.VenomGas;
import pd.actors.buffs.AttackUp;
import pd.actors.buffs.Burning;
import pd.actors.buffs.Poison;
import pd.Dungeon;
import pd.items.Generator;
import pd.items.quest.AdventureJournal;
import pd.items.eggs.randomone.*;
import pd.items.sellitem.VIPcard;
import pd.sprites.ItemSpriteSheet;
import com.watabou.noosa.Game;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public final class SpsPetEggsTest {

	public static void main(String[] args) throws Exception {
		GdxNativesLoader.load();
		Gdx.files = new HeadlessFiles();
		Game.version = "test";
		testDirectEggs();
		testRandomPools();
		testGeneratorPools();
		testBreakRewardsAndItems();
		testCommonPetRules();
		testPetHomeDepth();
		testChineseResources();
		System.out.println("SPS宠物灵魂测试通过：龙魂、随机包、十二月映射、生成池、VIP奖励和中文资源均正常。");
	}

	private static void testDirectEggs() {
		assertEgg(new BlueDragonEgg(), ItemSpriteSheet.BLUE_DRAGON_EGG, BlueDragon.class);
		assertEgg(new BlueGirlEgg(), ItemSpriteSheet.BLUE_GIRL_EGG, BlueGirl.class);
		assertEgg(new BugDragonEGG(), ItemSpriteSheet.BUG_DRAGON_EGG, BugDragon.class);
		assertEgg(new GreenDragonEgg(), ItemSpriteSheet.GREEN_DRAGON_EGG, GreenDragon.class);
		assertEgg(new LeryFireEgg(), ItemSpriteSheet.LERY_FIRE_EGG, LeryFire.class);
		assertEgg(new LightDragonEgg(), ItemSpriteSheet.LIGHT_DRAGON_EGG, LightDragon.class);
		assertEgg(new RedDragonEgg(), ItemSpriteSheet.RED_DRAGON_EGG, RedDragon.class);
		assertEgg(new ScorpionEgg(), ItemSpriteSheet.SCORPION_EGG, Scorpion.class);
		assertEgg(new VioletDragonEgg(), ItemSpriteSheet.VIOLET_DRAGON_EGG, VioletDragon.class);
		GoldDragonEgg gold = new GoldDragonEgg();
		check(gold.image == ItemSpriteSheet.GOLD_DRAGON_EGG && gold.value() == 500, "金龙魂图标或价值错误");
		LegacyPet goldResult = gold.hatchling();
		check(goldResult instanceof GoldDragon || goldResult instanceof BugDragon, "金龙魂没有遵循原版特殊孵化规则");
		check(new BlueDragonEgg().freezes == 20 && new BlueGirlEgg().poisons == 30
				&& new LeryFireEgg().moves == 50 && new LightDragonEgg().darks == 20
				&& new ScorpionEgg().moves == 2000, "直接龙魂的原版能量数据错误");
	}

	private static void testRandomPools() {
		assertPool(new RandomAtkEgg(), Kodora.class, Snake.class, RibbonRat.class, GentleCrab.class);
		assertPool(new RandomDefEgg(), DogPet.class, Chocobo.class, Fly.class, Stone.class, Spider.class);
		assertPool(new RandomColEgg(), ButterflyPet.class, Monkey.class, PigPet.class, Datura.class);
		assertPool(new RandomEgg1(), Kodora.class, DogPet.class, Datura.class);
		assertPool(new RandomEgg2(), GentleCrab.class, Stone.class, FoxHelper.class);
		assertPool(new RandomEgg3(), RibbonRat.class, DwarfBoy.class, FrogPet.class);
		assertPool(new RandomEgg4(), Kodora.class, Fly.class, Monkey.class);
		assertPool(new RandomEgg5(), Snake.class, Chocobo.class, PigPet.class);
		assertPool(new RandomEgg6(), LitDemon.class, Spider.class, ButterflyPet.class);
		assertPool(new RandomEgg7(), GentleCrab.class, DwarfBoy.class, FrogPet.class);
		assertPool(new RandomEgg8(), StarKid.class, DogPet.class, FoxHelper.class);
		assertPool(new RandomEgg9(), RibbonRat.class, Chocobo.class, Datura.class);
		assertPool(new RandomEgg10(), StarKid.class, Stone.class, PigPet.class);
		assertPool(new RandomEgg11(), Snake.class, Fly.class, ButterflyPet.class);
		assertPool(new RandomEgg12(), LitDemon.class, Spider.class, Monkey.class);
		Class<?>[] months = {RandomEgg1.class, RandomEgg2.class, RandomEgg3.class, RandomEgg4.class,
				RandomEgg5.class, RandomEgg6.class, RandomEgg7.class, RandomEgg8.class,
				RandomEgg9.class, RandomEgg10.class, RandomEgg11.class, RandomEgg12.class};
		for (int month = 0; month < months.length; month++) {
			check(RandomEgg.monthEgg(month).getClass() == months[month], "第" + (month + 1) + "月宠物包映射错误");
		}
	}

	private static void testGeneratorPools() {
		Class<?>[] eggs = {BlueDragonEgg.class, CocoCatEgg.class, EasterEgg.class, Egg.class,
				LightDragonEgg.class, GreenDragonEgg.class, LeryFireEgg.class, RedDragonEgg.class,
				ScorpionEgg.class, ShadowDragonEgg.class, VioletDragonEgg.class, GoldDragonEgg.class,
				RandomEgg.class};
		float[] eggWeights = {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
		Class<?>[] basePets = {RandomAtkEgg.class, RandomDefEgg.class, RandomColEgg.class,
				RandomEasterEgg.class, RandomEgg1.class, RandomEgg2.class, RandomEgg3.class,
				RandomEgg4.class, RandomEgg5.class, RandomEgg6.class, RandomEgg7.class,
				RandomEgg8.class, RandomEgg9.class, RandomEgg10.class, RandomEgg11.class,
				RandomEgg12.class};
		check(Arrays.equals(Generator.Category.EGGS.classes, eggs)
				&& Arrays.equals(Generator.Category.EGGS.probs, eggWeights), "EGGS池顺序或权重与0.9.8不一致");
		check(Arrays.equals(Generator.Category.BASEPET.classes, basePets), "BASEPET池顺序与0.9.8不一致");
		for (float weight : Generator.Category.BASEPET.probs) check(weight == 1, "BASEPET池存在非1权重");
		check(Generator.Category.EGGS.defaultProbs == null && Generator.Category.BASEPET.defaultProbs == null,
				"宠物池不应使用破碎版的递减牌堆抽取");
	}

	private static void testBreakRewardsAndItems() {
		Egg empty = new Egg();
		check(empty.failedHatchReward() == null, "未成长灵魂错误地产生了随机包");
		empty.moves = 100;
		check(empty.failedHatchReward() instanceof RandomEgg, "成长100步的失败灵魂没有转化为随机包");
		check(Egg.VIP_DROP_DENOMINATOR == 10, "破魂VIP卡概率不是10%");
		RandomEgg random = new RandomEgg(); RandomMonthEgg allMonths = new RandomMonthEgg(); VIPcard vip = new VIPcard();
		check(random.stackable && RandomEgg.AC_USE.equals(random.defaultAction()) && random.value() == 500,
				"随机灵魂的堆叠、默认动作或价值错误");
		check(allMonths.stackable && allMonths.value() == 500, "随机月份灵魂属性错误");
		check(vip.stackable && vip.image == ItemSpriteSheet.VIP_CARD && vip.value() == 400,
				"VIP卡图标、堆叠或价值错误");
	}

	private static void testCommonPetRules() {
		PET pet = new BlueDragon();
		check(pet.properties().contains(Char.Property.IMMOVABLE), "旧版宠物缺少不可强制位移属性");
		check(pet.isImmune(ToxicGas.class) && pet.isImmune(VenomGas.class)
				&& pet.isImmune(Burning.class) && pet.isImmune(CorruptGas.class)
				&& pet.isImmune(NmGas.class), "旧版宠物公共免疫不完整");
		check(!new Poison().attachTo(pet) && pet.buff(Poison.class) == null,
				"旧版宠物错误接受了白名单外状态");
		AttackUp attackUp = new AttackUp();
		check(attackUp.attachTo(pet) && pet.buff(AttackUp.class) == attackUp,
				"旧版宠物没有接受攻击提升状态");
		attackUp.detach();
	}

	private static void testPetHomeDepth() {
		Dungeon.depth = AdventureJournal.anchorDepth(0);
		Dungeon.branch = AdventureJournal.branchFor(0);
		check(Dungeon.legacyDepth() == 50 && Egg.petHomeDepth(),
				"宠物灵魂没有识别旧版50层宠物之家");
		Dungeon.depth = AdventureJournal.anchorDepth(1);
		Dungeon.branch = AdventureJournal.branchFor(1);
		check(Dungeon.legacyDepth() == 51 && !Egg.petHomeDepth(),
				"旧版51层错误套用了宠物之家规则");
		Dungeon.depth = 1;
		Dungeon.branch = 0;
	}

	private static void testChineseResources() throws Exception {
		String text = Files.readString(Paths.get("messages/items/items_zh.properties"), StandardCharsets.UTF_8);
		check(text.contains("items.eggs.randomone.randomegg12.name=随机十二月灵魂")
				&& text.contains("items.eggs.bugdragonegg.name=BUG龙之魂")
				&& text.contains("items.sellitem.vipcard.name=VIP卡"), "宠物灵魂中文资源缺失或乱码");
	}

	private static void assertEgg(Egg egg, int image, Class<? extends LegacyPet> type) {
		check(egg.image == image && egg.value() == 500 && egg.hatchling().getClass() == type,
				type.getSimpleName() + "灵魂孵化类型、图标或价值错误");
	}

	@SafeVarargs
	private static void assertPool(RandomPetEgg egg, Class<? extends LegacyPet>... expected) {
		check(Arrays.equals(egg.possiblePets(), expected), egg.getClass().getSimpleName() + "候选宠物错误");
		check(egg.image == ItemSpriteSheet.SPS_PET_EGG && egg.value() == 500,
				egg.getClass().getSimpleName() + "图标或价值错误");
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsPetEggsTest() {
	}
}
