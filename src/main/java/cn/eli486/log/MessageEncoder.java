package cn.eli486.log;

import cn.eli486.dto.PageMessage;
import com.alibaba.fastjson.JSONObject;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * @author eli
 */
public class MessageEncoder  implements Encoder.Text<PageMessage>  {

    @Override
    public String encode (PageMessage pageMessage) throws EncodeException {
        return JSONObject.toJSONString (pageMessage);
    }

    @Override
    public void init (EndpointConfig endpointConfig) {

    }

    @Override
    public void destroy () {

    }
}
