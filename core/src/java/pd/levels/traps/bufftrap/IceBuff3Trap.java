package pd.levels.traps.bufftrap;
import pd.actors.blobs.effectblobs.FrostCloud;
import pd.messages.InlineText;
public class IceBuff3Trap extends ElementalBuffTrap {
	//SPSEXPD: inline Chinese text (generated from messages/levels/zh)
	static {
		InlineText.of(IceBuff3Trap.class)
			.t("name", "冰种大陷阱")
			.t("desc", "会释放大范围寒冰场的陷阱。");
	}
 public IceBuff3Trap(){ super(TEAL, STARS, FrostCloud.class, 2, 9, false); } }
