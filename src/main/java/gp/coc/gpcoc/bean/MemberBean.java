package gp.coc.gpcoc.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MemberBean implements Comparable<MemberBean> {

  private String name;
  private int donationsReceived;
  private int donations;
  private int userLevel;
  private String smallUrl;

  @Override
  public int compareTo(MemberBean o) {
    if (this.donations > o.donations) {
      return -1;
    }
    return 1;
  }
}
