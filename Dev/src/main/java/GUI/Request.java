package GUI;

import io.javalin.http.Context;

import java.util.Collection;
import java.util.LinkedList;

public class Request {

    public static String getSessionAttribute(Context context, String atr) {
        return context.sessionAttribute(atr);
    }

    public static boolean removeSessionAttribute(Context context, String atr) {
        String toBeRemoved = context.sessionAttribute(atr);
        context.sessionAttribute(atr, null);
        return toBeRemoved != null;
    }

    public static String getDate(Context ctx) {
        String [] date = ctx.formParam("date").split("-");
        String result = date[2] + "/" + date[1] + "/" + date[0];
        return result;
    }

    public static String getStringFormParam(Context context, String param) {
        return context.formParam(param);
    }

    public static Integer getIntegerFormParam(Context context, String param) {
        return Integer.parseInt(context.formParam(param));
    }

    public static Double getDoubleFormParam(Context context, String param) {
        return Double.parseDouble(context.formParam(param));
    }

    public static String getStringQueryParam(Context context, String param) {
        return context.queryParam(param);
    }

    public static Integer getIntegerQueryParam(Context context, String param) {
        return Integer.parseInt(context.queryParam(param));
    }

    public static Collection<Integer> getIntegerMultipleParam(Context ctx, String param) {
        Collection<String> product = ctx.formParams(param).subList(0, ctx.formParams(param).size());
        Collection<Integer> result = new LinkedList<>();
        for (String p: product) {
            String s = p.split("id: ")[1];
            String id = s.split(",")[0];
            result.add(Integer.parseInt(id));
        }
        return result;
    }

    public static Integer getIntegerFromStringParam(Context ctx, String param) {
        Collection<String> selections = ctx.formParams(param).subList(0, ctx.formParams(param).size());
        String selection1=(String) selections.toArray()[0];
        String s = selection1.split("id: ")[1];
        String id = s.split(",")[0];
        return Integer.parseInt(id);
    }

    public static Collection<Integer> getIntegerMultipleParam2(Context ctx, String param) {
        Collection<String> selections = ctx.formParams(param).subList(0, ctx.formParams(param).size());
        Collection<Integer> result = new LinkedList<>();
        String selection1=(String) selections.toArray()[0];
        String s = selection1.split("id: ")[1];
        String id = s.split(",")[0];
        return result;
    }


}
