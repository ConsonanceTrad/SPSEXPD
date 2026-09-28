package com.shatteredpixel.shatteredpixeldungeon.sprites;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.security.MessageDigest;

import javax.imageio.ImageIO;

/** Verifies that every main-route boss animation uses its original SPS-PD sheet. */
public final class SpsBossSpriteTest {

	public static void main(String[] args) throws Exception {
		checkAsset("sprites/mobs/sps_goo.png", "4D6F77E821CB27B98F82C3AD2602F13EB8687A1726F703B071AF9F59C015184E", 20, 14, 10);
		checkAsset("sprites/mobs/sps_poison_goo.png", "A3FE52219404559B9F52243E63BD55597F441A8CC0407D6466EA66608AE2A996", 20, 14, 10);
		checkAsset("sprites/mobs/sps_sewer_heart.png", "BD6FF042878CEFB8653981C244F50FD199534AA48B84A9F7440ED06913064FAA", 16, 16, 7);
		checkAsset("sprites/mobs/sps_sewer_lasher.png", "9B969FDD19814B77B5EBAFEC6144FC366DC4453721E859B79FB2D058992F037F", 12, 16, 6);
		checkAsset("sprites/mobs/sps_plague_doctor.png", "4247D7806CB0FB5D5A3FFA833FBCDA92C125B890FF0505355EDDD0BB79C98305", 16, 16, 10);
		checkAsset("sprites/mobs/sps_shadow_rat.png", "7B7580B84402C02D3E895891611087E4B363C1826E4FF22BA37F0233C866DC4E", 16, 15, 78);

		checkAsset("sprites/mobs/sps_tengu.png", "E77D0578CC00601145979B8CAC089BCA4B34E594A80E3A4677487D56BDF5A0B8", 14, 16, 10);
		checkAsset("sprites/mobs/sps_prison_wander.png", "C3A115EB45516915BCFC97ACBA9110D5B7A4AB453DC29F5C3FE6F77DC2963126", 12, 16, 14);
		checkAsset("sprites/mobs/sps_seeking_bomb.png", "18EBE5D660F580C70F07C62FAA3C183ECBBCF5C50A23F054F8E1F52CE4E9EC8B", 16, 16, 4);
		checkAsset("sprites/mobs/sps_tank.png", "E3CD4814447F34F1FF969E9842D2393DA4D913EBD3C1AEE041A1DDA4284CE5E6", 16, 14, 14);

		checkAsset("sprites/mobs/sps_hybrid.png", "4AE1E82EC8DE55D990F45134F1F51BAB61F11E8CB47B6F1A40CAD7E8B5FF6163", 22, 18, 10);
		checkAsset("sprites/mobs/sps_dm300.png", "8D4620ECA5E52562875B3188F01F2EE9A07B827BABC112073F19FDC76E07086F", 22, 20, 8);
		checkAsset("sprites/mobs/sps_tower.png", "C4C9357E77455CBABA443B8112812A5F10F5D4A495ED187AB3964987A2DAC9A4", 16, 16, 0);
		checkAsset("sprites/mobs/sps_broken_robot.png", "41ECE0DAA92189F3BB1A89EAEF90DDC176B0B366A10C5443031E05A1CE7760C9", 16, 18, 13);
		checkAsset("sprites/mobs/sps_spider_queen.png", "369499EE57701FB6733EDAE4147664AE596D567203A94E624210BB1E21854466", 16, 16, 11);

		checkAsset("sprites/mobs/sps_lich_dancer.png", "3B393D7FA219B6D492AF14D16740178FCCEF8C37F32F097341DE732DC7347FE2", 16, 16, 13);
		checkAsset("sprites/mobs/sps_elder_avatar.png", "B68EE521BF84E59A33F81E1006238BD3EFCC52FA7B5B49996909487E3B754DE0", 12, 15, 15);
		checkAsset("sprites/mobs/sps_king.png", "F9636F2F472AB5FF588632F059473B533BC590E9B11310A9BE8592D6FDB47CF5", 16, 16, 24);
		checkAsset("sprites/mobs/sps_dwarf_king_tomb.png", "8FAB7FA3A1DB7261AEB0E0E5794B38354B43C05DDC043028C18CE92EB2106C11", 16, 16, 0);
		checkAsset("sprites/mobs/sps_dwarf_lich.png", "11ADF3E9ADD5BE200B83EF70F0A86E5AC8B8E87027B8A48F6EDFFB2746679F52", 12, 16, 16);

		checkAsset("sprites/mobs/sps_yog.png", "915EB9542D4930CC2EFA0E93B574E87967DAAB28E4BB050164EF1E57D2F5085D", 20, 19, 9);
		checkAsset("sprites/mobs/sps_rotting_fist.png", "BA650F8C191A68F621680C845ACB3BF0A138681536A595B7448FDB38D8756508", 24, 17, 4);
		checkAsset("sprites/mobs/sps_burning_fist.png", "EEBB5FC6083ABD6FFDC0E5D572A61020AFBE92DD94E99ABBE5D2F5CACBFB3D32", 24, 17, 6);
		checkAsset("sprites/mobs/sps_infecting_fist.png", "A085CBFD1F2FC49F2A94A8B5369D33627F047B9BAAE79460D36DD3DDD388E047", 24, 17, 4);
		checkAsset("sprites/mobs/sps_pinning_fist.png", "51B061C4BD4A9092D2DD12DD28FCE7CDB7DDA0B297999948CCA7385FB24BE9D1", 24, 17, 6);
		checkAsset("sprites/mobs/sps_yog_larva.png", "0162B270D66693CCDA169F90D740C8FCD8398EAEB706967EF852129A9426536E", 12, 8, 8);

		System.out.println("SPS主线首领动画测试通过：26张原始图集及其切帧范围均正常。");
	}

	private static void checkAsset(String path, String expectedHash,
			int frameWidth, int frameHeight, int highestFrame) throws Exception {
		File file = new File(path);
		check(file.isFile(), "缺少首领动画：" + path);
		String actualHash = hex(MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(file.toPath())));
		check(expectedHash.equals(actualHash), "首领动画不是0.9.8原图：" + path);

		BufferedImage image = ImageIO.read(file);
		check(image != null, "无法读取首领动画：" + path);
		int frameCount = (image.getWidth() / frameWidth) * (image.getHeight() / frameHeight);
		check(frameCount > highestFrame,
				"首领动画帧越界：" + path + " #" + highestFrame + "/" + frameCount);
	}

	private static String hex(byte[] bytes) {
		StringBuilder result = new StringBuilder(bytes.length * 2);
		for (byte value : bytes) result.append(String.format("%02X", value & 0xFF));
		return result.toString();
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}

	private SpsBossSpriteTest() { }
}
