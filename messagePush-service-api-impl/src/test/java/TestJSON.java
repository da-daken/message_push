import com.alibaba.fastjson.JSONObject;

public class TestJSON {

    public void testJSON(){
        JSONObject jsonObject = JSONObject.parseObject("{$user}用户您关注的主播{$anchor}开播啦！赶紧来看:{$url}链接");

    }

}
