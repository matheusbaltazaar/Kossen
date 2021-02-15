package com.baltazarstudio.kossen.context;

import com.baltazarstudio.kossen.model.Perfil;

public class AppContext {

    private static Perfil perfil;

    public static Perfil getPerfil() {
        return perfil;
    }

    public static void setPerfil(Perfil perfil) {
        AppContext.perfil = perfil;
    }
}
