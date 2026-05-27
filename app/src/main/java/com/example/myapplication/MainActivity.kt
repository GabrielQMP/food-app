package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                FoodApp()
            }
        }
    }
}

@Composable
fun FoodApp() {
    val navController = rememberNavController()
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "details/{restaurantId}") {
                BottomNavigationBar(navController, currentRoute)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreen(navController)
            }

            composable("profile") {
                ProfileScreen()
            }

            composable("cart") {
                CartScreen()
            }

            composable("details/{restaurantId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("restaurantId")?.toIntOrNull()
                val restaurant = getRestaurants().find { it.id == id }

                if (restaurant != null) {
                    RestaurantDetailsScreen(navController, restaurant)
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    currentRoute: String?
) {
    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Início") }
        )

        NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = {
                navController.navigate("cart") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
            label = { Text("Carrinho") }
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    launchSingleTop = true
                }
            },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil") }
        )
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    var searchText by remember { mutableStateOf("") }

    val restaurants = getRestaurants().filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.category.contains(searchText, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeaderSection()
        }

        item {
            SearchBar(
                value = searchText,
                onValueChange = { searchText = it }
            )
        }

        item {
            PromoCard()
        }

        item {
            SectionTitle("Categorias")
            CategoryList()
        }

        item {
            SectionTitle("Restaurantes populares")
        }

        items(restaurants) { restaurant ->
            RestaurantCard(
                restaurant = restaurant,
                onClick = {
                    navController.navigate("details/${restaurant.id}")
                }
            )
        }
    }
}

@Composable
fun HeaderSection() {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null)

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Entrega em São Paulo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Escolha seu restaurante favorito",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Buscar comida ou restaurante") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PromoCard() {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Oferta especial",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Frete grátis em restaurantes selecionados",
                color = Color.White
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Ver ofertas")
            }
        }
    }
}

@Composable
fun CategoryList() {
    val categories = listOf(
        "🍔 Burger",
        "🍕 Pizza",
        "🍣 Sushi",
        "🥗 Saudável",
        "🍰 Doces"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { category ->
            AssistChip(
                onClick = {},
                label = { Text(category) }
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = restaurant.emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = restaurant.category,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${restaurant.rating} • ${restaurant.deliveryTime} • ${restaurant.deliveryFee}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Icon(Icons.Default.FavoriteBorder, contentDescription = null)
        }
    }
}

@Composable
fun RestaurantDetailsScreen(
    navController: NavHostController,
    restaurant: Restaurant
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = restaurant.emoji,
                    style = MaterialTheme.typography.displayLarge
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = restaurant.category,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "⭐ ${restaurant.rating} • ${restaurant.deliveryTime} • ${restaurant.deliveryFee}"
            )
        }

        item {
            SectionTitle("Mais pedidos")
        }

        items(getMenuItems()) { item ->
            MenuItemCard(item)
        }
    }
}

@Composable
fun MenuItemCard(item: MenuItem) {
    ElevatedCard(
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.emoji,
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = item.price,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(onClick = {}) {
                Text("Adicionar")
            }
        }
    }
}

@Composable
fun CartScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Seu carrinho está vazio",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Gabriel",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text("Cliente FoodApp")
    }
}

data class Restaurant(
    val id: Int,
    val name: String,
    val category: String,
    val rating: Double,
    val deliveryTime: String,
    val deliveryFee: String,
    val emoji: String
)

data class MenuItem(
    val name: String,
    val description: String,
    val price: String,
    val emoji: String
)

fun getRestaurants(): List<Restaurant> {
    return listOf(
        Restaurant(1, "Burger House", "Hambúrguer", 4.5, "30-40 min", "R$ 5,99", "🍔"),
        Restaurant(2, "Pizza Prime", "Pizza", 4.7, "25-35 min", "Grátis", "🍕"),
        Restaurant(3, "Sushi Go", "Japonesa", 4.8, "40-50 min", "R$ 8,99", "🍣"),
        Restaurant(4, "Açaí Tropical", "Açaí e Sobremesas", 4.6, "20-30 min", "R$ 3,99", "🍧"),
        Restaurant(5, "Marmita Express", "Brasileira", 4.4, "35-45 min", "R$ 4,99", "🍛")
    )
}

fun getMenuItems(): List<MenuItem> {
    return listOf(
        MenuItem("Combo especial", "Prato principal + bebida", "R$ 39,90", "🍽️"),
        MenuItem("Promoção da casa", "Item mais vendido do restaurante", "R$ 29,90", "🔥"),
        MenuItem("Bebida gelada", "Refrigerante ou suco natural", "R$ 8,90", "🥤")
    )
}