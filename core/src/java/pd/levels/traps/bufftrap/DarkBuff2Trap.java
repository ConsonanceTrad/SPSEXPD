package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.ShadowGas;
import pd.messages.InlineText;
public class DarkBuff2Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(DarkBuff2Trap.class)
			.t("name", "暗种中陷阱")
			.t("desc", "会释放中等范围暗影场的陷阱。");
	}
 public DarkBuff2Trap(){ super(VIOLET, WAVES, ShadowGas.class, 1, 6, false); } }
