package com.techelevator;

import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;

public class VendingMachine {
    //colors for better UI
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String RED_UNDERLINED = "\033[4;31m";

    private Map<String, VendingItem> inventoryMap = new HashMap<>();

    //Each slot in the vending machine has enough room for 5 of that product.
    private final int STARTING_INVENTORY_LEVEL = 5;

    //The BigDecimal class provides operations on double numbers for money format conversion.
    private BigDecimal balance = BigDecimal.ZERO;
    private final int QUARTER_VALUE_IN_PENNIES = 25;
    private final int DIME_VALUE_IN_PENNIES = 10;
    private final int NICKEL_VALUE_IN_PENNIES = 5;

   /***********************************************************************************/
//   Dispensing an item prints the item name, cost, and the money
//    remaining. Dispensing also returns a message:
//            - All chip items print "Crunch Crunch, Yum!"
//            - All candy items print "Munch Munch, Yum!"
//            - All drink items print "Glug Glug, Yum!"
//            - All gum items print "Chew Chew, Yum!"
   /*********************************************************************************************/
    public Map<String,String> snackSounds = new HashMap<String, String>() {
        {
           put("Chip","Crunch Crunch, Yum!");
           put("Gum","Chew, Chew, Yum!");
           put("Drink","Glug Glug, Yum!");
           put("Candy","Munch Munch, Yum!");
        }
    };

    private BigDecimal totalSales = BigDecimal.ZERO;

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    private void addToTotalSales(BigDecimal transactionAmount){
        totalSales = totalSales.add(transactionAmount);
    }

    //BalanceZeroException Class is created.
    public BigDecimal getBalance() throws BalanceZeroException{
        if(balance.compareTo(BigDecimal.ZERO) == 0){
            throw new BalanceZeroException("Balance is $0.00. Reload the Balance.");
        }
        return balance;
    }


    /**********************************************************************************/
    //The input file that stocks the vending machine products is a pipe `|` delimited file.
    // Each line is a separate product in the file and follows the below format:
    /**********************************************************************************/
    public VendingMachine(String fileName){
        List<String[]> inventory = new ArrayList<>();
        File inputFile = new File(fileName);
        try(Scanner reader = new Scanner(inputFile)){
            while (reader.hasNextLine()){
                String line = reader.nextLine();
                String[] item = line.split("\\|");
                String sku = item[0];
                VendingItem itemForMap = new VendingItem(item[1], item[2], item[3], STARTING_INVENTORY_LEVEL);
                inventoryMap.put(sku, itemForMap);
            }
        }catch (FileNotFoundException e){
            System.out.println("Inventory File not found.");
            System.out.println(e.getMessage());
        }

    }

    // It gets the value when testing
    public int getQuantitySold(String location){
        return inventoryMap.get(location).getQuantitySold();
    }

    public String displayInventory(){
        String returnString = "";
        Set keySet = inventoryMap.keySet();
        List<String> keyArray = new ArrayList<>(keySet);
        Collections.sort(keyArray);
        String previousRowLetter = "A";
        for (int i = 0; i <keyArray.size() ; i++) {
            String keyValue = keyArray.get(i);
            String rowLetter = keyValue.substring(0,1);
            if(!previousRowLetter.equals(rowLetter)){
                returnString += "\n";
            }else {
                if(i > 0) {
                    //CLI will show  A1 - D4 and seprate by | in Display
                    returnString += "\t|\t";
                }
            }
            returnString +=  keyValue+ " "+ inventoryMap.get(keyValue);
            previousRowLetter = keyValue.substring(0,1);
        }
        return returnString;
    }

    private List <String[]> loadInventory (String fileName) {
        List<String[]> inventory = new ArrayList<>();
        File inputFile = new File(fileName);
        try(Scanner reader = new Scanner(inputFile)){
            while (reader.hasNextLine()){
                String line = reader.nextLine();
                String[] item = line.split(" ");
                inventory.add(item);
            }
        }catch (FileNotFoundException e){
            System.out.println("Inventory not found.");
            System.out.println(e.getMessage());
        }
        return inventory;
    }


    public boolean addToBalance(String amountToAdd){
        try{
            int amount = Integer.parseInt(amountToAdd);
            if(amount <0){
                throw new NumberFormatException();
            }
            BigDecimal convertedAmountToAdd = new BigDecimal(amount);
            balance = balance.add(convertedAmountToAdd);
            // call appendToAuditFile
            appendLogFile("FEED MONEY", convertedAmountToAdd);
            return true;
        }catch(NumberFormatException e) {
            System.out.println("Invalid Amount! Try again.");
            return false;
        }
    }

    public String getCustomerBalance() {
        String returnString = "";
        returnString = ANSI_GREEN+"*******************************\nCurrent Balance:  $ " + convertBDtoCurrency(balance)+"\nInsert enough money for desired product. \nSelect 2 for your desired product.\nIf done with purchase please select 3 for your change." +
                "\n*******************************"+ANSI_RESET;
        return returnString;
    }

    private void subtractFromBalance(BigDecimal amountToSubtract){
        balance = balance.subtract(amountToSubtract);
    }

    private boolean sufficientFunds(BigDecimal itemCost){
        boolean hasSufficientFunds = false;
        if (itemCost.compareTo(balance)<=0) {
            hasSufficientFunds = true;
        }
        return hasSufficientFunds;
    }

    private BigDecimal convertBDtoCurrency(BigDecimal number){
        return number.setScale(2, RoundingMode.DOWN);
    }

    /***************************************************************************/
    //All purchases must be audited to prevent theft from the vending machine:
    //     - Each purchase must generate a line in a file called `Log.txt`.
    //     - The audit entry must be in the format:
    //              produce Date Time
    //              Append
    /***************************************************************************/

    private void appendLogFile(String transactionType, BigDecimal value){
        File logFile = new File("/Users/agyhern/Desktop/Merit_America/REPO/Module one Capstone/module-1-capstone/capstone/src/main/java/com/techelevator/data/log.txt");
        try(PrintWriter pw = new PrintWriter(new FileOutputStream(logFile, true))){
          String timeStamp = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa").format(Calendar.getInstance().getTime());
            pw.println(timeStamp + " " + transactionType +" $"+convertBDtoCurrency(value)+ " $" + convertBDtoCurrency(balance));
        }catch( FileNotFoundException e){
            System.out.println("Something went wrong.");
            System.out.println("*** Unable to open log file: " + logFile.getAbsolutePath());
        }
    }


    public String makePurchase(String userSelection) {
        String purchaseMessage ="";
        if(inventoryMap.containsKey(userSelection)){
            VendingItem currentItem = inventoryMap.get(userSelection);
            int quantity = currentItem.getQuantity();
            if(quantity>0){
                // Checking sufficient balance
                if(sufficientFunds(currentItem.getPrice())){
                    // make the purchase : update balance & decrease quantity
                    subtractFromBalance(currentItem.getPrice());
                    currentItem.setQuantity(quantity-1);
                    // add to total sales
                    addToTotalSales((currentItem).getPrice());
                    //increase quantity sold by 1 for sales report
                    currentItem.incrementQuantitySold();

                    appendLogFile(currentItem.getItem() + " "+ userSelection,currentItem.getPrice());
                    purchaseMessage = getItemSnackSound(currentItem.getType());
                } else {
                    purchaseMessage = "Insufficient funds for selected item.\nReload the Balance.";
                }
            }else{
                purchaseMessage = RED_UNDERLINED+ "\n*******************\nSorry out of stock\n*******************\n" + ANSI_RESET;
            }
        } else{
            purchaseMessage = "Invalid selection!";
        }
        return purchaseMessage;
    }
    public int getInventoryQuantity(String location){
        return inventoryMap.get(location).getQuantity();
    }


    public String finishTransaction (){
        String returnString = ANSI_GREEN+ "Your change is $"+ convertBDtoCurrency(balance)+ANSI_RESET;
        BigDecimal currentBalance = balance;
        String coins = getCoinString();
        returnString += "\nYour coins: " + coins;
        balance = BigDecimal.ZERO;
        appendLogFile("GIVE CHANGE", currentBalance);
        return returnString;
    }

    private String getCoinString() {
        int pennyBalance = balance.multiply(new BigDecimal("100")).intValue();
        int quarters = pennyBalance / QUARTER_VALUE_IN_PENNIES;
        int dimes = (pennyBalance % QUARTER_VALUE_IN_PENNIES) / DIME_VALUE_IN_PENNIES;
        int nickels = ((pennyBalance % QUARTER_VALUE_IN_PENNIES) % DIME_VALUE_IN_PENNIES) / NICKEL_VALUE_IN_PENNIES;
        return "Quarters: " + quarters + " | Dimes: " + dimes + " | Nickels: " + nickels;
    }
    public String getItemSnackSound (String type){
        return snackSounds.get(type);
    }


    public void generateSalesReport() {
       try(PrintWriter salesReportData = new PrintWriter("SalesReport.txt")){
            for(String key : inventoryMap.keySet()){
                VendingItem item = inventoryMap.get(key);
                salesReportData.println(item.getItem() + "\t|\t"+ item.getQuantitySold());
            }
            salesReportData.println();
            salesReportData.println("TOTAL SALES: $" + getTotalSales().setScale(2, RoundingMode.DOWN));
        }catch (FileNotFoundException e){
            System.out.println("Destination file error.");
        }
    }
}