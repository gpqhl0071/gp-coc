package gp.coc.gpcoc.bean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cocuser")
public class UserBean {
  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  private int id;
  private String userName;
  private String cocToken;
  private int level;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public String getCocToken() {
    return cocToken;
  }

  public void setCocToken(String cocToken) {
    this.cocToken = cocToken;
  }
}
