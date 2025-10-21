package com.upc.ep.ServicesIMPL;

import com.upc.ep.Entidades.Detalle_Ped;
import com.upc.ep.Repositorio.Detalle_PedRepos;
import com.upc.ep.Services.Detalle_PedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Detalle_PedIMPL implements Detalle_PedService {
    @Autowired
    private Detalle_PedRepos dpRepos;

    @Override
    public Detalle_Ped saveDP(Detalle_Ped dp) {
        return dpRepos.save(dp);
    }

    @Override
    public List<Detalle_Ped> listarDP() {
        return dpRepos.findAll();
    }
}
