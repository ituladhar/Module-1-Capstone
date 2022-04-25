package com.techelevator;

import com.techelevator.view.Menu;
import java.util.*;

public class VendingMachineCLI {
	//colors for better UI
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String GREEN_UNDERLINED = "\033[4;32m";
	public static final String RED_UNDERLINED = "\033[4;31m";
	public static final String RED_BOLD = "\033[1;31m";


	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS  = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";

	// Added as per Application Requirements
	private static final String FEED_MONEY = "Feed Money";
	private static final String SELECT_PRODUCT = "Select Product";
	private static final String FINISH_TRANSACTION = "Finish Transaction";
	// HIDDEN MENU STILL WORKING ON SOLUTION
	private static final String MAIN_MENU_SALES_REPORT = "**ADMIN ACCESS**";
	private static final String MAIN_MENU_OPTION_EXIT = "Exit";

	private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE, MAIN_MENU_OPTION_EXIT, MAIN_MENU_SALES_REPORT };
	private static final String[] PURCHASE_ITEMS_SUB_MENU_OPTIONS = {FEED_MONEY,SELECT_PRODUCT, FINISH_TRANSACTION };
	private static final String[] PURCHASE_AFTER_FEEDING_MONEY = {SELECT_PRODUCT, FINISH_TRANSACTION};

	private Menu menu;

	public Scanner customerInput = new Scanner(System.in);
	public VendingMachineCLI(Menu menu) {
		this.menu = menu;
	}

	public void run() {
		VendingMachine vm = new VendingMachine("src/main/java/com/techelevator/data/vendingmachine.csv");
		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				System.out.println(vm.displayInventory());
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				while (true) {
					System.out.println(vm.getCustomerBalance());
					String subChoice = (String) menu.getChoiceFromOptions(PURCHASE_ITEMS_SUB_MENU_OPTIONS);
					if (subChoice.equals(FEED_MONEY)) {
						boolean validInput = false;
						while (!validInput) {
							System.out.print(RED_BOLD+"Enter the amount: $" +ANSI_RESET);
							String balanceInput = customerInput.nextLine();
							validInput = vm.addToBalance(balanceInput);
						}} else if (subChoice.equals(SELECT_PRODUCT)) {
						try {
							vm.getBalance();
							System.out.println(ANSI_GREEN+"Enter the slot (Item Selection): "+ANSI_RESET);
							System.out.println(vm.displayInventory());
							String userSelection = customerInput.nextLine().toUpperCase(Locale.ROOT);
							System.out.println(vm.makePurchase(userSelection));
						} catch (BalanceZeroException e) {
							System.out.println(e.getMessage());
						}
					}else if (subChoice.equals(FINISH_TRANSACTION)) {

						System.out.println(vm.finishTransaction());
						break;
					}
				}
			}else if (choice.equals(MAIN_MENU_OPTION_EXIT)){
				break;
			}else if(choice.equals(MAIN_MENU_SALES_REPORT)){
				vm.generateSalesReport();
				System.out.println(RED_UNDERLINED+"\n**********************\nSales Report Generated\n**********************" +ANSI_RESET);
			}
		}
	}

	public static void main(String[] args) {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}
}
