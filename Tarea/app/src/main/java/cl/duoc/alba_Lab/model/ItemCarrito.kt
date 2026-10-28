package cl.duoc.alba_Lab.model

//producto seleccionado y cantidad agregada al carrito
data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int
) {
    val subtotal: Int get() = producto.precio * cantidad
}