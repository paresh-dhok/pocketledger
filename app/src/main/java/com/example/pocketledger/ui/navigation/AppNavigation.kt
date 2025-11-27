package com.example.pocketledger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocketledger.ui.screens.dashboard.DashboardScreen
import com.example.pocketledger.ui.screens.accounts.AccountsScreen
import com.example.pocketledger.ui.screens.transactions.TransactionsScreen
import com.example.pocketledger.ui.screens.loans.LoansScreen
import com.example.pocketledger.ui.screens.add_transaction.AddTransactionScreen
import com.example.pocketledger.ui.screens.settings.SettingsScreen
import com.example.pocketledger.ui.screens.debug.DebugScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onNavigateToAccounts = { navController.navigate(Screen.Accounts.route) },
                onNavigateToTransactions = { navController.navigate(Screen.Transactions.route) },
                onNavigateToLoans = { navController.navigate(Screen.Loans.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToDebug = { navController.navigate(Screen.Debug.route) }
            )
        }
        
        composable(Screen.Transactions.route) {
            TransactionsScreen(
                onNavigateToAddTransaction = { navController.navigate(Screen.AddTransaction.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Accounts.route) {
            AccountsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Loans.route) {
            LoansScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDebug = { navController.navigate(Screen.Debug.route) }
            )
        }
        
        composable(Screen.Debug.route) {
            DebugScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Transactions : Screen("transactions")
    object Accounts : Screen("accounts")
    object Loans : Screen("loans")
    object AddTransaction : Screen("add_transaction")
    object Settings : Screen("settings")
    object Debug : Screen("debug")
}
