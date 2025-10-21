package com.upc.ep.Services;

import com.upc.ep.Entidades.Detalle_Ped;

import java.util.List;

public interface Detalle_PedService {
    public Detalle_Ped saveDP(Detalle_Ped dp);
    public List<Detalle_Ped> listarDP();
}
