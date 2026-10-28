package cl.duoc.alba_Lab.data

import cl.duoc.alba_Lab.R
import cl.duoc.alba_Lab.model.Producto

// Catálogo compartido por la tienda y el carrito; se guardan los ID, no los recursos Android.
object CatalogoProductos {
    val elementos = listOf(
        Producto(id = "difusor_aroma_100",
            nombre = "Difusor de aroma 100 ml",
            imagen = R.drawable.difusor_aroma,
            descripcion = "Aromatizador de ambiente en formato spray, en envase PET de 100 ml. Aromas publicados: Alba, coco lavanda, vainilla y verbena limón.",
            precio = 7500),
        Producto(id = "difusor_colgante",
            nombre = "Difusor colgante",
            imagen = R.drawable.difusor_colgante,
            descripcion = "Aromatizador de 8 ml con piedras naturales y envase de vidrio. Pensado para espacios pequeños, como el auto o el clóset. Aromas publicados: Alba, coco lavanda, vainilla y verbena limón.",
            precio = 7500))

}
