package gp.coc.gpcoc.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gp.coc.gpcoc.bean.MemberBean;
import gp.coc.gpcoc.bean.UserBean;
import gp.coc.gpcoc.service.UserResponsitory;

@Controller
public class MemberController {
  @Autowired
  private UserResponsitory userResponsitory;

  public static String auth = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6ImNlNGVmNmE5LTNkMjMtNGM2NC1iN2Y2LWYwY2Y5YzlmNzQzMiIsImlhdCI6MTYxMTc5Njg2MCwic3ViIjoiZGV2ZWxvcGVyL2QxNDA4NDcyLWQ5Y2QtMGNmOC1mZDM3LWU4NjMzMWJmNWNmNSIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjExNy4xMDcuMTMyLjIzNCIsIjEyMS4zNi45Mi4yMzYiXSwidHlwZSI6ImNsaWVudCJ9XX0.IUVcJh_PUhr6Md0jh0LD3UId-thfLjVhs2a-XX2suEDPdGdJIB83pR55HllisxiRmQLCt3EU6p9CQ3NbpxfuWg";

  @RequestMapping(value = "/member", method = RequestMethod.GET)
  public String member(Model model) {
    Map<String, Integer> userLevelMap = queryUserLevelMap();

    String result = HttpUtil.createGet("https://api.clashofclans.com/v1/clans/%2322L8G9GJC/members?limit=50")
        .header("authorization", auth)
        .contentType(ContentType.JSON.getValue())
        .execute().body();
    System.out.println(JSONUtil.toJsonPrettyStr(result));
    JSONArray items = JSONUtil.parseArray(JSONUtil.parse(result).getByPath("items"));

    List<MemberBean> memberBeanList = new LinkedList<MemberBean>();
    for (int i = 0; i < items.size(); i++) {
      JSON member = JSONUtil.parse(items.get(i));
      String name = Convert.toStr(member.getByPath("name"));
      int donationsReceived = Convert.toInt(member.getByPath("donationsReceived"));
      int donations = Convert.toInt(member.getByPath("donations"));
      String smallUrl = Convert.toStr(member.getByPath("league.iconUrls.small"));
      String tag = Convert.toStr(member.getByPath("tag"));
//      System.out.println("玩家：" + name + ", 收：" + donationsReceived + ", 捐：" + donations);

//      JSON json = getMemberInfo(userTag);

      memberBeanList.add(
          MemberBean.builder()
              .name(name)
              .donationsReceived(donationsReceived)
              .donations(donations)
              .smallUrl(smallUrl)
              .userLevel(userLevelMap.get(tag))
//              .userRole(Convert.toStr(json.getByPath("role"), "未查到"))
              .build()
      );

    }

    Collections.sort(memberBeanList);

    model.addAttribute("list", memberBeanList);

    return "member.html";
  }

  @RequestMapping(value = "/add", method = RequestMethod.GET)
  public void add(HttpServletResponse response, HttpServletRequest request) {

    String result = HttpUtil.createGet("https://api.clashofclans.com/v1/clans/%2322L8G9GJC/members?limit=50")
        .header("authorization", auth)
        .contentType(ContentType.JSON.getValue())
        .execute().body();
    System.out.println(JSONUtil.toJsonPrettyStr(result));
    JSONArray items = JSONUtil.parseArray(JSONUtil.parse(result).getByPath("items"));

    for (int i = 0; i < items.size(); i++) {
      JSON member = JSONUtil.parse(items.get(i));
      String tag = Convert.toStr(member.getByPath("tag"));
      String name = Convert.toStr(member.getByPath("name"));

      JSON json = getMemberInfo(tag);

      UserBean userBean = new UserBean();
      userBean.setUserName(name);
      userBean.setLevel(Convert.toInt(json.getByPath("townHallLevel"), 0));
      userBean.setCocToken(tag);
      userResponsitory.save(userBean);
    }
  }

  private JSON getMemberInfo(String userTag) {
    String result = null;
    try {
      userTag = StrUtil.replace(userTag, "#", "%");
      result = HttpUtil.createGet("https://api.clashofclans.com/v1/players/" + userTag)
          .header("authorization", auth)
          .contentType(ContentType.JSON.getValue())
          .execute().body();
      System.out.println(result);
      Object townHallLevel = JSONUtil.parse(result).getByPath("townHallLevel");
    } catch (HttpException e) {
      return null;
    }

    return JSONUtil.parse(result);
  }

  private Map<String, Integer> queryUserLevelMap() {
    Iterable<UserBean> all = userResponsitory.findAll();
    Iterator<UserBean> iterator = all.iterator();

    Map<String, Integer> resultMap = new HashMap<String, Integer>();
    while (iterator.hasNext()) {
      UserBean u = iterator.next();
      resultMap.put(u.getCocToken(), u.getLevel());

    }
    return resultMap;
  }
}
