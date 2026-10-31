package cl.duoc.alba_Lab.model

data class ItemCarrito(
    val producto: Producto,
    val cantidad: Int
) {
    val subtotal: Int get() = producto.precio * cantidad
}