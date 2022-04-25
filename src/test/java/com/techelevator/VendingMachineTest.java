package com.techelevator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class VendingMachineTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testProperSoundForSelection() {
        Map<String, String> stringStringMap = (new VendingMachine("src/main/java/com/techelevator/data/vendingmachine.csv")).snackSounds;
        assertEquals(4, stringStringMap.size());
        assertEquals("Munch Munch, Yum!", stringStringMap.get("Candy"));
        assertEquals("Glug Glug, Yum!", stringStringMap.get("Drink"));
        assertEquals("Crunch Crunch, Yum!", stringStringMap.get("Chip"));
        assertEquals("Chew, Chew, Yum!", stringStringMap.get("Gum"));
    }


  /*  @Test
    public void testCurrentBalanceFullRun() {
        VendingMachine actualVendingMachine = new VendingMachine("src/main/java/com/techelevator/data/vendingmachine.csv");
        assertEquals("\u001b[32m*******************************\n" + "Current Balance:  $ 0.00\n"
                        + "After you insert enough money select your desired product.\n" + "*******************************\u001b[0m",
                actualVendingMachine.getCustomerBalance());
        assertEquals(4, actualVendingMachine.snackSounds.size());
        assertEquals("0", actualVendingMachine.getTotalSales().toString());
    }*/


    @Test
    public void testGetBalance() throws BalanceZeroException {
        thrown.expect(BalanceZeroException.class);
        (new VendingMachine("src/main/java/com/techelevator/data/vendingmachine.csv")).getBalance();
    }

    @Test
    public void testAddToBalance2() {
        assertFalse((new VendingMachine("src/main/java/com/techelevator/data/vendingmachine.csv")).addToBalance("foo"));
    }

    @Test
    public void testMakePurchase() {
        assertEquals("Invalid selection!", (new VendingMachine("src/main/java/com/techelevator/data/vendingmachine.csv")).makePurchase("User Selection"));
    }




}

