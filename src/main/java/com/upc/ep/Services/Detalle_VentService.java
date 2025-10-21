package com.upc.ep.Services;

import com.upc.ep.Entidades.Detalle_Vent;

import java.util.List;

public interface Detalle_VentService {
    public Detalle_Vent saveDV(Detalle_Vent dv);
    public List<Detalle_Vent> listarDV();
}
