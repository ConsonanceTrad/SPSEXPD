/* Special Surprise Pixel Dungeon, GPLv3 or later. */
package pd.items.equipment.weapon.missiles;

/**
 * SPSEXPD: 「一次性投掷武器」的标记接口（果实、魔术手、砖头、地形塑造块等）。
 *
 * ⚠️ 注意：本接口**故意不提供 default 方法**。
 * 曾经在这里写过 durabilityPerUse/isSimilar/isUpgradable 的默认实现，但那是无效的——
 * Java 里**类（含从父类继承来的）实现优先于接口 default**，而 MissileWeapon / Item
 * 都已经有这些方法，于是 default 全被盖掉，接口形同虚设（果实因此一度变回
 * 「有耐久 + 不堆叠 + 可升级」）。真正生效的实现统一放在 {@link MissileWeapon} 里，
 * 由它 instanceof 判定本接口。
 *
 * 为什么是接口而不是抽象基类：这些物品的直接父类各不相同
 * （SpsFruit 系走 MissileWeapon、MagicHand 走 Arrows、Brick 走 MeleeThrowWeapon），
 * Java 单继承下没法共享基类。
 *
 * 统一语义（实现见 MissileWeapon）：
 *  · 无限磨损 —— 耐久永不减少，也不受魔法套筒/暗器袋等使用次数加成影响
 *  · 使用即删除 —— 命中敌人时消耗掉这一件；落空 / 命中空地仍保留（可捡回）
 *  · 不在描述里显示耐久度，背包里也不会用橙色/黄色警告数字
 *  · 按种类堆叠合并，不走投掷武器的「每组 3 个」与 setID 判定
 *  · 无法升级
 *
 * 后续要再指定某个投掷武器为一次性，让它 implements 本接口即可。
 */
public interface SingleUseMissile {
}
