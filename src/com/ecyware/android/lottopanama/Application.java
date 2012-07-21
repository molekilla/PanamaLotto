package com.ecyware.android.lottopanama;

import java.util.List;
import com.google.inject.Module;
import roboguice.application.RoboApplication;

// NOT IN USED
public class Application extends RoboApplication 
{
    @Override
    protected void addApplicationModules(List<Module> modules) {
        modules.add(new RoboguiceModule());
   }
}
