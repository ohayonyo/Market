package GUI;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

import static GUI.Request.*;

public class WebView {
    public static Map<String, Object> initModel(Context context) {
        Map<String, Object> model = new HashMap<>();
        MsgWrapper wrapper = new MsgWrapper(getSessionAttribute(context, "locale"));
        model.put("msg", wrapper);
        model.put("current_user", getSessionAttribute(context, "current_user"));
        return model;
    }

}
