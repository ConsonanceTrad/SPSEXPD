/*
 * SPSEXPD: 局内「特殊能力点数」的显示来源接口。
 *
 * 血条下方的小号 SP 数字默认显示 {@code Hero.spp}；当英雄身上挂着实现本接口的 buff
 * （例如武僧的 {@code MonkEnergy}）且其 {@link #active(Hero)} 为真时，改显示该来源的数值。
 * 以后要接入新的点数资源（纳米无人机、职业技能充能等），实现本接口即可，无需改 HUD。
 */

package pd.actors.hero;

public interface SpPointSource {

	/** 当前是否应由本来源接管显示 */
	boolean active( Hero hero );

	/** 当前点数 */
	int value( Hero hero );

	/** 上限；小于 0 表示无上限（只显示当前值） */
	int max( Hero hero );
}
