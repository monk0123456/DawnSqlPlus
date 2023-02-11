package org.dawnsql.lib;

import java.util.ArrayList;
import java.util.List;

public class MyUtil {

    public List<String> toList(final String line)
    {
        List<String> lst = new ArrayList<>();
        for (char c : line.toCharArray())
        {
            lst.add(String.valueOf(c));
        }
        return lst;
    }
}
