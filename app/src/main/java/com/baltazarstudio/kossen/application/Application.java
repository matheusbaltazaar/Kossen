package com.baltazarstudio.kossen.application;

import com.baltazarstudio.kossen.context.AppContext;
import com.baltazarstudio.kossen.database.Database;

public class Application extends android.app.Application {


    @Override
    public void onCreate() {
        super.onCreate();

        AppContext.setPerfil(new Database(this).carregarPerfil());

    }
}
