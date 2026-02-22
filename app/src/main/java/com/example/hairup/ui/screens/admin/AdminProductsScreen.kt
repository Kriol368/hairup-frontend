package com.example.hairup.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hairup.model.Product

private val CarbonBlack = Color(0xFF121212)
private val DarkGray = Color(0xFF1E1E1E)
private val CardBg = Color(0xFF1A1A1A)
private val Gold = Color(0xFFD4AF37)
private val TextGray = Color(0xFFB0B0B0)
private val White = Color(0xFFFFFFFF)
private val GreenConfirmed = Color(0xFF4CAF50)
private val RedCancel = Color(0xFFE53935)
private val LeatherBrown = Color(0xFF8B5E3C)

private val defaultCategories = listOf(
    "Champús", "Acondicionadores", "Tratamientos", "Styling", "Accesorios"
)

private val initialProducts = listOf(
    Product(1, "Champú Reparador", "Reparación intensiva para cabello dañado", 25.0, "", true, "Champús"),
    Product(2, "Acondicionador Premium", "Suavidad y brillo duradero", 20.0, "", true, "Acondicionadores"),
    Product(3, "Mascarilla Hidratante", "Hidratación profunda en 5 minutos", 18.0, "", true, "Tratamientos"),
    Product(4, "Sérum Capilar", "Tratamiento sin aclarado para puntas", 32.0, "", false, "Tratamientos"),
    Product(5, "Spray Protector Térmico", "Protección hasta 230°C", 15.0, "", true, "Styling")
)

@Composable
fun AdminProductsScreen() {
    var products by remember { mutableStateOf(initialProducts) }
    var categories by remember { mutableStateOf(defaultCategories) }
    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CarbonBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Inventario de Productos",
                style = MaterialTheme.typography.titleMedium,
                color = Gold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "${products.count { it.available }} disponibles · ${products.count { !it.available }} no disponibles",
                style = MaterialTheme.typography.bodySmall,
                color = TextGray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            products.forEach { product ->
                ProductCard(
                    product = product,
                    onToggleAvailability = {
                        products = products.map {
                            if (it.id == product.id) it.copy(available = !it.available) else it
                        }
                    },
                    onEdit = {
                        editingProduct = product
                        showDialog = true
                    },
                    onDelete = {
                        productToDelete = product
                        showDeleteDialog = true
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(72.dp))
        }

        FloatingActionButton(
            onClick = { editingProduct = null; showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Gold,
            contentColor = CarbonBlack
        ) {
            Icon(Icons.Default.Add, contentDescription = "Añadir producto")
        }
    }

    if (showDialog) {
        ProductDialog(
            product = editingProduct,
            categories = categories,
            onAddCategory = { newCat ->
                if (newCat.isNotBlank() && !categories.contains(newCat)) {
                    categories = categories + newCat
                }
            },
            onDismiss = { showDialog = false; editingProduct = null },
            onSave = { name, description, price, imageUrl, category ->
                val current = editingProduct
                if (current != null) {
                    products = products.map {
                        if (it.id == current.id)
                            it.copy(name = name, description = description, price = price, image = imageUrl, category = category)
                        else it
                    }
                } else {
                    val newId = (products.maxOfOrNull { it.id } ?: 0) + 1
                    products = products + Product(newId, name, description, price, imageUrl, true, category)
                }
                showDialog = false
                editingProduct = null
            }
        )
    }

    if (showDeleteDialog && productToDelete != null) {
        val prod = productToDelete!!
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false; productToDelete = null },
            containerColor = DarkGray,
            titleContentColor = White,
            textContentColor = TextGray,
            title = { Text("Eliminar producto", fontWeight = FontWeight.Bold) },
            text = { Text("¿Eliminar \"${prod.name}\"? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        products = products.filter { it.id != prod.id }
                        showDeleteDialog = false
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedCancel, contentColor = White),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false; productToDelete = null }) {
                    Text("Cancelar", color = Gold)
                }
            }
        )
    }
}

@Composable
private fun ProductCard(
    product: Product,
    onToggleAvailability: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val availColor = if (product.available) GreenConfirmed else TextGray

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Gold.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Inventory,
                    contentDescription = null,
                    tint = Gold,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "€${product.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Gold
                    )
                    if (product.category.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(LeatherBrown.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = product.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = LeatherBrown,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(availColor.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (product.available) "Disponible" else "No disponible",
                            style = MaterialTheme.typography.labelSmall,
                            color = availColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Gold, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = RedCancel, modifier = Modifier.size(20.dp))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp)
        ) {
            Button(
                onClick = onToggleAvailability,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (product.available) LeatherBrown.copy(alpha = 0.2f) else GreenConfirmed.copy(alpha = 0.15f),
                    contentColor = if (product.available) LeatherBrown else GreenConfirmed
                ),
                shape = RoundedCornerShape(10.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (product.available) "Marcar como no disponible" else "Marcar como disponible",
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialog(
    product: Product?,
    categories: List<String>,
    onAddCategory: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (name: String, description: String, price: Double, imageUrl: String, category: String) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var priceText by remember { mutableStateOf(product?.price?.toInt()?.toString() ?: "") }
    var imageUrl by remember { mutableStateOf(product?.image ?: "") }
    var selectedCategory by remember {
        mutableStateOf(product?.category?.ifBlank { categories.first() } ?: categories.first())
    }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showNewCategoryField by remember { mutableStateOf(false) }
    var newCategoryText by remember { mutableStateOf("") }
    var newCategoryError by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf(false) }
    var priceError by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Gold,
        unfocusedBorderColor = LeatherBrown,
        focusedLabelColor = Gold,
        unfocusedLabelColor = TextGray,
        cursorColor = Gold,
        focusedTextColor = White,
        unfocusedTextColor = White
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkGray,
        titleContentColor = White,
        title = {
            Text(
                text = if (product != null) "Editar producto" else "Nuevo producto",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = false },
                    label = { Text("Nombre") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors,
                    supportingText = if (nameError) {
                        { Text("El nombre es obligatorio", color = RedCancel) }
                    } else null
                )

                // Descripción
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )

                // Precio
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it; priceError = false },
                    label = { Text("Precio (€)") },
                    isError = priceError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors,
                    supportingText = if (priceError) {
                        { Text("Introduce un precio válido", color = RedCancel) }
                    } else null
                )

                // Categoría (dropdown)
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = fieldColors
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                        modifier = Modifier.background(DarkGray)
                    ) {
                        // Categorías existentes
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = category,
                                        color = if (category == selectedCategory) Gold else White,
                                        fontWeight = if (category == selectedCategory) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedCategory = category
                                    categoryExpanded = false
                                    showNewCategoryField = false
                                }
                            )
                        }

                        // Separador
                        Divider(
                            color = LeatherBrown.copy(alpha = 0.3f),
                            thickness = 0.5.dp
                        )

                        // Opción nueva categoría
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        tint = Gold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Nueva categoría",
                                        color = Gold,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            },
                            onClick = {
                                categoryExpanded = false
                                showNewCategoryField = true
                                newCategoryText = ""
                                newCategoryError = false
                            }
                        )
                    }
                }

                // Campo inline para nueva categoría
                if (showNewCategoryField) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = newCategoryText,
                                onValueChange = { newCategoryText = it; newCategoryError = false },
                                label = { Text("Nueva categoría") },
                                isError = newCategoryError,
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = fieldColors
                            )
                            IconButton(
                                onClick = {
                                    val trimmed = newCategoryText.trim()
                                    when {
                                        trimmed.isBlank() -> newCategoryError = true
                                        categories.any { it.equals(trimmed, ignoreCase = true) } -> newCategoryError = true
                                        else -> {
                                            onAddCategory(trimmed)
                                            selectedCategory = trimmed
                                            showNewCategoryField = false
                                            newCategoryText = ""
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Gold.copy(alpha = 0.15f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Confirmar",
                                    tint = Gold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        if (newCategoryError) {
                            Text(
                                text = if (newCategoryText.isBlank()) "El nombre no puede estar vacío"
                                       else "Esta categoría ya existe",
                                style = MaterialTheme.typography.labelSmall,
                                color = RedCancel,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                // URL de imagen
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de imagen (opcional)") },
                    placeholder = { Text("https://...", color = TextGray.copy(alpha = 0.5f)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val trimmedName = name.trim()
                    val price = priceText.toDoubleOrNull()
                    nameError = trimmedName.isBlank()
                    priceError = price == null || price < 0
                    if (!nameError && !priceError) {
                        onSave(trimmedName, description.trim(), price!!, imageUrl.trim(), selectedCategory)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = CarbonBlack
                ),
                shape = RoundedCornerShape(8.dp)
            ) { Text("Guardar", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray)
            }
        }
    )
}
