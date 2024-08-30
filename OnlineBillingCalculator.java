import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class OnlineBillingCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Create categories
        Map<String, Category> categories = new HashMap<>();
        categories.put("1", new Category("Grocery and Kitchen",
                Arrays.asList("Atta", "Dal", "Rice", "Poha")));
        categories.put("2", new Category("Snack and Drinks",
                Arrays.asList("Coco Cola", "Pepsi", "Maggi", "Chips")));
        categories.put("3", new Category("Beauty and Personal Care",
                Arrays.asList("Soap", "Shampoo", "Body Lotion", "Perfume")));
        categories.put("4", new Category("Household Essential",
                Arrays.asList("Detergent", "Floor Cleaner", "Room Freshener")));
        categories.put("5", new Category("Electronics",
                Arrays.asList("TV", "AC", "Watch", "Headphone")));

        // Create customer
        System.out.println("Enter customer name:");
        String name = scanner.nextLine();
        System.out.println("Enter customer phone number:");
        String phoneNumber = scanner.nextLine();
        Customer customer = new Customer(name, phoneNumber);

        // Customer selects items
        while (true) {
            System.out.println("\nSelect Category:");
            System.out.println("1) Grocery and Kitchen");
            System.out.println("2) Snack and Drinks");
            System.out.println("3) Beauty and Personal Care");
            System.out.println("4) Household Essential");
            System.out.println("5) Electronics");
            System.out.println("6) Done Shopping");

            String choice = scanner.nextLine();
            if ("6".equals(choice)) {
                break;
            }
            Category selectedCategory = categories.get(choice);
            if (selectedCategory != null) {
                System.out.println("\n" + selectedCategory.getCategoryName() + " Items:");
                selectedCategory.showItems();
                System.out.println("Select item by entering its name:");
                String itemName = scanner.nextLine();
                Item selectedItem = selectedCategory.getItemByName(itemName);
                if (selectedItem != null) {
                    customer.addItem(selectedItem);
                } else {
                    System.out.println("Invalid item name!");
                }
            } else {
                System.out.println("Invalid choice!");
            }
        }

        // Generate bill
        customer.generateBill();

        // Store bill in text file
        storeBillToFile(customer);

        scanner.close();
    }

    private static void storeBillToFile(Customer customer) {
        String fileName = customer.getPhoneNumber() + ".txt";
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write("Customer Name: " + customer.getName() + "\n");
            fw.write("Phone Number: " + customer.getPhoneNumber() + "\n\n");
            fw.write("Items:\n");
            for (Item item : customer.getSelectedItems()) {
                fw.write(item.getItemName() + " - Actual Price: " + item.getItemPrice()
                        + " - Discounted Price: " + item.getDiscountPrice() + "\n");
            }
            fw.write("\nTotal Amount to Pay: " + customer.getFinalAmount());
        } catch (IOException e) {
            System.out.println("Error storing bill to file: " + e.getMessage());
        }
    }
}

class Item {
    private String itemName;
    private double itemPrice;
    private int quantity;
    private double discountPrice;

    public Item(String itemName, double itemPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = 1; // Quantity set to 1 for fixed price items
        calculateDiscount();
    }

    private void calculateDiscount() {
        if (itemPrice < 1000 && quantity <= 100) {
            discountPrice = itemPrice * 0.05; // 5% discount
        } else if (itemPrice >= 1000 && quantity <= 10) {
            discountPrice = itemPrice * 0.10; // 10% discount
        } else {
            discountPrice = 0;
        }
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public String getItemName() {
        return itemName;
    }
}

class Category {
    private String categoryName;
    private List<Item> items;

    public Category(String categoryName, List<String> itemNames) {
        this.categoryName = categoryName;
        this.items = new ArrayList<>();
        for (String itemName : itemNames) {
            // Fixed prices for items
            switch (itemName) {
                case "Atta":
                case "Dal":
                case "Rice":
                case "Poha":
                    items.add(new Item(itemName, 50));
                    break;
                case "Coco Cola":
                case "Pepsi":
                case "Chips":
                    items.add(new Item(itemName, 20));
                    break;
                case "Maggi":
                    items.add(new Item(itemName, 15));
                    break;
                case "Soap":
                case "Shampoo":
                    items.add(new Item(itemName, 100));
                    break;
                case "Body Lotion":
                    items.add(new Item(itemName, 200));
                    break;
                case "Perfume":
                    items.add(new Item(itemName, 500));
                    break;
                case "Detergent":
                    items.add(new Item(itemName, 150));
                    break;
                case "Floor Cleaner":
                    items.add(new Item(itemName, 100));
                    break;
                case "Room Freshener":
                    items.add(new Item(itemName, 120));
                    break;
                case "TV":
                    items.add(new Item(itemName, 20000));
                    break;
                case "AC":
                    items.add(new Item(itemName, 30000));
                    break;
                case "Watch":
                    items.add(new Item(itemName, 5000));
                    break;
                case "Headphone":
                    items.add(new Item(itemName, 1000));
                    break;
            }
        }
    }

    public void showItems() {
        for (Item item : items) {
            System.out.println(item.getItemName() + " - " + item.getItemPrice());
        }
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Item getItemByName(String itemName) {
        for (Item item : items) {
            if (item.getItemName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }
}

class Customer {
    private String name;
    private String phoneNumber;
    private List<Item> selectedItems;
    private double finalAmount;

    public Customer(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.selectedItems = new ArrayList<>();
    }

    public void addItem(Item item) {
        selectedItems.add(item);
    }

    public void generateBill() {
        double totalAmount = 0;
        for (Item item : selectedItems) {
            totalAmount += item.getItemPrice();
        }
        double discountAmount = selectedItems.stream().mapToDouble(Item::getDiscountPrice).sum();
        finalAmount = totalAmount - discountAmount;

        // Display bill
        System.out.println("\nCustomer: " + name);
        System.out.println("Phone Number: " + phoneNumber);
        System.out.println("\nItems Selected:");
        for (Item item : selectedItems) {
            System.out.println(item.getItemName() + " - Actual Price: " + item.getItemPrice()
                    + " - Discounted Price: " + item.getDiscountPrice());
        }
        System.out.println("\nTotal Amount to Pay: " + finalAmount);
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public List<Item> getSelectedItems() {
        return selectedItems;
    }

    public double getFinalAmount() {
        return finalAmount;
    }
}
