package com.example.pocketledger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocketledger.ui.accounts.AccountsScreen
import com.example.pocketledger.ui.dashboard.DashboardScreen
import com.example.pocketledger.ui.loans.LoansScreen
import com.example.pocketledger.ui.transaction.AddTransactionScreen
import com.example.pocketledger.ui.transactions.TransactionsListScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object AddTransaction : Screen("add_transaction")
    object Accounts : Screen("accounts")
    object Loans : Screen("loans")
    object Transactions : Screen("transactions")
}

@Composable
fun PocketLedgerNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onAddTransactionClick = { navController.navigate(Screen.AddTransaction.route) },
                onAccountsClick = { navController.navigate(Screen.Accounts.route) },
                onLoansClick = { navController.navigate(Screen.Loans.route) },
                onTransactionsClick = { navController.navigate(Screen.Transactions.route) }
            )
        }
        composable(Screen.AddTransaction.route) {
            AddTransactionScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Accounts.route) {
            AccountsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Loans.route) {
            LoansScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Screen.Transactions.route) {
            TransactionsListScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
