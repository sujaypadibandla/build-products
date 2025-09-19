package com.gymplus.gymPlus;

import com.gymplus.core.Garray;
import com.gymplus.core.Gmap;

public class GymPlusApplication {

    public static void main(String[] args) throws Exception {

        GymPlusPlugin gp = new GymPlusPlugin();
        gp.invokeAuthApi();
        Gmap response = gp.getWeeklyClasses();
        Garray classes = gp.getClassesToBook(response.getm("payload"));
        int classId = gp.fetchBookableClassId(classes);
        gp.bookAclass(classId);
    }
}
