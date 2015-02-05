package ru.redmadrobot.alexgontarenko.slideshow.parcers;

import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.util.ArrayList;

import ru.redmadrobot.alexgontarenko.slideshow.enity.PanoramioSlideObject;

public class PanoramioParcer  extends BaseJSONParser {

    protected BasePanoramioResponse object;

    public BasePanoramioResponse getObject(){
        return object;
    }

    @Override
    protected boolean parse(JsonReader reader, JsonToken token, String path, String nodeName) throws IOException {
        if (token.equals(JsonToken.STRING)) {
            if(nodeName.equals("photo_file_url")) {
               addImage(reader.nextString());
                return true;
            }
        }
        return false;
    }

    protected void addImage (String url) {
        if(object == null)
            object = new BasePanoramioResponse();
        final PanoramioSlideObject data = object.getData();
        if(data != null) {
            final ArrayList<String> slide = data.getImgList();
            if(slide!=null)
                slide.add(url);
        }
    }
}
