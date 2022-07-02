package StoreTests;

import Exceptions.*;
import SpellChecker.Spelling;
import Store.Store;
import Store.Product;
import User.User;
import User.Subscriber;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import User.ShoppingBag;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class StoreTest {

    private Store store;
    private String userName1 = "Lin";
    private String userName2 = "Yoad";
    private String userName3 = "Yarin";
    private String userName4 = "Omer";
    private String userName5 = "Barak";
    private Spelling spelling = new Spelling();
//    private final User user = mock(User.class);
//    private final User owner = mock(User.class);

    @Mock private Product product;
    @Mock private Subscriber subscriber;

//    @Mock private Store storeMock;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        try {
            store = new Store("Store1");
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void closeStore() throws StoreNotActiveException, LostConnectionException {
        //close active store
        store.closeStore();
        //assertThrows(StoreNotActiveException.class, store.isStroeActivated());

        //close closed store
        assertThrows(StoreNotActiveException.class,()->store.closeStore());
        //assertFalse(storeisStroeActivated());
    }

    @Test
    void openStore() throws StoreIsAlreadyActiveException,IllegalNameException {
        //open active store
        assertThrows(StoreIsAlreadyActiveException.class,()->store.openStore());
        //open inactive store
        store.setActive(false);
       // assertFalse(store.isActive());
        store.openStore();
       // assertTrue(store.isActive());
        //create store with null name
        assertThrows(IllegalNameException.class,()->store=new Store(null));
        //create store with empty name
        assertThrows(IllegalNameException.class,()->store=new Store(""));
    }

    //TODO:add mock to owner and user
    @Test
    void addManager() throws AlreadyManagerException, LostConnectionException {
        Subscriber owner = new Subscriber(userName1);
        Subscriber user1 = new Subscriber(userName2);
        Subscriber user2 = new Subscriber(userName3);
        //check if no one is appointed by owner to be a manager
        assertNull(store.getManagersBy().get(owner));
        //add user1 as manager (when the managerBy list at the beginning were null)
        store.addManager(owner,user1);
        assertTrue(store.getManagersBy().get(owner).contains(user1));
        //add user2 as manager (when the managersBy contains already a user(user1))
        store.addManager(owner,user2);
        assertTrue(store.getManagersBy().get(owner).contains(user1));
        assertTrue(store.getManagersBy().get(owner).contains(user2));
    }

    @Test
    void addOwner() throws AlreadyOwnerException, NotOwnerException, AllTheOwnersNeedToApproveAppointement, LostConnectionException {
        Subscriber owner = new Subscriber(userName1);
        Subscriber user1 = new Subscriber(userName2);
        Subscriber user2 = new Subscriber(userName3);
        //check if no one is appointed by owner to be an owner
        assertNull(store.getOwnersBy().get(owner));
        //add user1 as owner (when the managerBy list at the beginning were null)
        store.addOwner(owner,user1);
        assertTrue(store.getOwnersBy().get(owner).contains(user1));
        //add user2 as owner (when the managersBy contains already a user(user1))
        store.addOwner(owner,user2);
        assertTrue(store.getOwnersBy().get(owner).contains(user1));
        assertTrue(store.getOwnersBy().get(owner).contains(user2));
    }

    @Test
    void addProduct() {
        Product product1 = new Product(store.getStoreId(),5.4,"Cow milk","Milk","Dairy products");
        Product product2 = new Product(store.getStoreId(),7,"Danone with chocolate","Danone","Dairy products");
        //check that the stock is empty
        assertTrue(store.getProductsHashMap().isEmpty());
        //add product1 to the store
        store.addProduct(product1,5);
        assertTrue(store.getProductsHashMap().containsKey(product1));
        assertTrue(store.getProductsHashMap().get(product1)==5);
        //add product2 to the store and verify that the stock of product1 still remain the same
        store.addProduct(product2,10);
        assertTrue(store.getProductsHashMap().containsKey(product2));
        assertTrue(store.getProductsHashMap().get(product2)==10);
        assertTrue(store.getProductsHashMap().containsKey(product1));
        assertTrue(store.getProductsHashMap().get(product1)==5);
        //add more 5 products of product1 to the stock
        store.addProduct(product1,5);
        assertTrue(store.getProductsHashMap().containsKey(product1));
        assertTrue(store.getProductsHashMap().get(product1)==10);
        assertTrue(store.getProductsHashMap().containsKey(product2));
        assertTrue(store.getProductsHashMap().get(product2)==10);
    }

    @Test
    void findProductByID() {
        Product product1 = new Product(store.getStoreId(),5.4,"Cow milk","Milk","Dairy products");
        Product product2 = new Product(store.getStoreId(),7,"Danone with chocolate","Danone","Dairy products");
        assertNull(store.findProductByID(product1.getProductId()));
        //TODO:need to add a mock of addProduct func
        store.addProduct(product1,5);
        Assertions.assertEquals(store.findProductByID(product1.getProductId()),product1);
        assertNull(store.findProductByID(product2.getProductId()));
        //add product2 and find it and product1
        store.addProduct(product2,7);
        Assertions.assertEquals(store.findProductByID(product1.getProductId()),product1);
        Assertions.assertEquals(store.findProductByID(product2.getProductId()),product2);
        assertNull(store.findProductByID(product2.getProductId()+1));
    }

    @Test
    void findProductByName() {
        Product product1 = new Product(store.getStoreId(),5.4,"Cow milk","Milk","Dairy products");
        Product product2 = new Product(store.getStoreId(),7,"Danone with chocolate","Danone","Dairy products");
        Product product3 = new Product(store.getStoreId(),8,"Soy milk","Milk","Vegan");
        assertEquals(0,store.findProductByName("Milk", spelling).size());
        //TODO:need to add a mock of addProduct func
        //add Milk and lookup for it
        List<Product>products1=new LinkedList<Product>();
        products1.add(product1);
        store.addProduct(product1,5);
        assertTrue(store.areSameProducts(products1,store.findProductByName("Milk", spelling)));
        assertEquals(0,store.findProductByName("fghj", spelling).size());
        //add Danone and lookup for Milk and Danone
        List<Product>products2=new LinkedList<Product>();
        products2.add(product2);
        store.addProduct(product2,3);
        assertTrue(store.areSameProducts(store.findProductByName("Milk", spelling),products1));
        assertTrue(store.areSameProducts(store.findProductByName("Danone", spelling),products2));
        assertEquals(0,store.findProductByName("fghj", spelling).size());
        //add another type of milk product and lookup for Milk and Danone
        products1.add(product3);
        store.addProduct(product3,4);
        assertTrue(store.areSameProducts(store.findProductByName("Milk", spelling),products1));
        assertTrue(store.areSameProducts(store.findProductByName("Danone", spelling),products2));
        assertEquals(0,store.findProductByName("fghj", spelling).size());
    }

    @Test
    void findProductsByCategory() {
        Product product1 = new Product(store.getStoreId(),5.4,"Cow milk","Milk","Dairy products");
        Product product2 = new Product(store.getStoreId(),7,"Danone with chocolate","Danone","Dairy products");
        Product product3 = new Product(store.getStoreId(),8,"Soy milk","Milk","Vegan");
        assertEquals(0,store.findProductsByCategory("Dairy products", spelling).size());
        //TODO:need to add a mock of addProduct func
        //add Milk and lookup for it by the category name (Dairy products)
        List<Product>products1=new LinkedList<Product>();
        products1.add(product1);
        store.addProduct(product1,5);
        assertTrue(store.areSameProducts(store.findProductsByCategory("Dairy products", spelling),products1));
        assertEquals(0, store.findProductByName("fghj", spelling).size());
        //add another dairy product and lookup for it
        products1.add(product2);
        store.addProduct(product2,3);
        assertTrue(store.areSameProducts(store.findProductsByCategory("Dairy products", spelling),products1));
        assertEquals(0,store.findProductByName("fghjdb", spelling).size());
        //add vegan product
        List<Product>products2=new LinkedList<Product>();
        products2.add(product3);
        store.addProduct(product3,13);
        assertTrue(store.areSameProducts(store.findProductsByCategory("Dairy products", spelling),products1));
        assertTrue(store.areSameProducts(store.findProductsByCategory("Vegan", spelling),products2));
        assertEquals(0, store.findProductByName("vwfva", spelling).size());
    }

    @Test
    void findProductsByKeyWords() {
        Product product1 = new Product(store.getStoreId(),5.4,"Cow milk","Milk","Dairy products");
        Product product2 = new Product(store.getStoreId(),8,"Soy milk","Milk","Vegan");
        assertEquals(0,store.findProductsByKeyWords("mil", spelling).size());
        //TODO:need to add a mock of addProduct func
        //add Milk and lookup for it by keyword in description and by keyword in name
        store.addProduct(product1,5);
        List<Product>products=new LinkedList<Product>();
        products.add(product1);
        assertTrue(store.areSameProducts(products,store.findProductsByKeyWords("mil", spelling)));
        assertTrue(store.areSameProducts(products,store.findProductsByKeyWords("OW", spelling)));
        //assertFalse(store.areSameProducts(products,store.findProductsByKeyWords("products", spelling)));
        //add another milk product
        store.addProduct(product2,8);
        products.add(product2);
        assertTrue(store.areSameProducts(products,store.findProductsByKeyWords("MiL", spelling)));
        products.remove(product1);
        assertTrue(store.areSameProducts(products,store.findProductsByKeyWords("soy", spelling)));
    }

    @Test
    void updateProduct() throws ProductNotFoundException, IllegalNameException, IllegalPriceException, InsufficientInventory, LostConnectionException {
        Product product = new Product(store.getStoreId(),5.4,"Cow milk","Milk","Dairy products");
        Product notExists = new Product(store.getStoreId(),8.2,"invisible","unknown","unknown");
        int productId = product.getProductId();
        //TODO:need to add a mock of addProduct func
        //Legal update for all product details
        store.addProduct(product,9);
        store.updateProduct(productId,"Vegan","Soy milk",6,9.0);
        Product afterUpdate = store.getProductByID(productId);
        Assertions.assertTrue(afterUpdate.getCategory().equals("Vegan"));
        Assertions.assertTrue(afterUpdate.getDescription().equals("Soy milk"));
        assertTrue(afterUpdate.getPrice()==9.0);
        assertTrue(store.getProductsHashMap().get(afterUpdate)==6);
        //empty category name
        assertThrows(IllegalNameException.class,()->store.updateProduct(productId,"","dhd",9,8.7));
        //null category name
        assertThrows(IllegalNameException.class,()->store.updateProduct(productId,null,"dhd",9,8.7));
        //empty description
        assertThrows(IllegalNameException.class,()->store.updateProduct(productId,"Dairy","",9,8.7));
        //null description
        assertThrows(IllegalNameException.class,()->store.updateProduct(productId,"Dairy",null,9,8.7));
        //illegal amount in inventory
        assertThrows(InsufficientInventory.class,()->store.updateProduct(productId,"Dairy","Very taste milk",-1,8.7));
        //illegal price
        assertThrows(IllegalPriceException.class,()->store.updateProduct(productId,"Dairy","Very taste milk",4,-3.0));
        //try update product that doesn't in the inventory
        assertThrows(ProductNotFoundException.class,()->store.updateProduct(notExists.getProductId(),"Dairy","Very taste milk",4,10.8));

    }

    @Test
    void removeProduct() throws ProductNotFoundException {
        Product product1 = new Product(store.getStoreId(),5.4,"Cow milk","Milk","Dairy products");
        Product product2 = new Product(store.getStoreId(),6.4,"Soy milk","Milk","Vegan");
        //TODO: add mocking to addProduct func
        //add product and then remove it from inventory
        assertTrue(store.getProductsHashMap().isEmpty());
        store.addProduct(product1,8);
        assertTrue(store.getProductsHashMap().containsKey(product1));
        int productIDToRemove = product1.getProductId();
        store.removeProduct(productIDToRemove);
        assertTrue(store.getProductsHashMap().isEmpty());
        //remove a product that doesn't exists in inventory
        assertThrows(ProductNotFoundException.class,()->store.removeProduct(product1.getProductId()));
        //try to remove one product and see if the rest of the inventory stays the same
        store.addProduct(product1,6);
        store.addProduct(product2,15);
        assertTrue(store.getProductsHashMap().get(product1)==6&&store.getProductsHashMap().get(product2)==15);
        store.removeProduct(product2.getProductId());
        assertTrue(!store.getProductsHashMap().containsKey(product2.getProductId()));
        assertTrue(store.getProductsHashMap().get(product1)==6);
    }

    @Test
    void updateInventoryAfterPurchase() throws InsufficientInventory, ProductNotFoundException, LostConnectionException {
        User user = new User();
        Product product1 = new Product(store.getStoreId(),5.4,"Cow milk","Milk","Dairy products");
        Product product2 = new Product(store.getStoreId(),6.4,"Soy milk","Milk","Vegan");
        //proper purchase
        store.addProduct(product1,5);
        store.addProduct(product2,3);
        ShoppingBag shoppingBag1 = new ShoppingBag(store,user);
        shoppingBag1.addSingleProduct(product1,2);
        shoppingBag1.addSingleProduct(product2,3);
        assertEquals(shoppingBag1.getAmountOfProduct(product1),2);
        assertEquals(shoppingBag1.getAmountOfProduct(product2),3);
        assertTrue(store.getHistory().isEmpty());
        store.updateInventoryAfterPurchase(shoppingBag1);
        assertTrue(store.getHistory().contains(shoppingBag1.toString()));
        assertEquals(store.getProductsHashMap().get(product1),3);//5-2=3,3-3=0
        assertEquals(store.getProductsHashMap().get(product2),0);
        //try to purchase product that doesn't exists
        ShoppingBag shoppingBag2 = new ShoppingBag(store,user);
        Product product3 = new Product(store.getStoreId(),8,"taste fruit","Papaya","Fruits");
        shoppingBag2.addSingleProduct(product3,4);
        assertThrows(ProductNotFoundException.class,()->store.updateInventoryAfterPurchase(shoppingBag2));
        //try to purchase more than the units in stock
        ShoppingBag shoppingBag3 = new ShoppingBag(store,user);
        shoppingBag3.addSingleProduct(product1,4);//there is only 3 products in stock
        assertThrows(InsufficientInventory.class,()->store.updateInventoryAfterPurchase(shoppingBag3));
    }

    // irrelevant
//    @Test
//    void removeManager() throws NoApointedUsersException, AlreadyManagerException {
//        Subscriber owner = new Subscriber(userName1);
//        Subscriber user1 = new Subscriber(userName2);
//        Subscriber user2 = new Subscriber(userName3);
//        //remove user that is appointed to be manager by the same owner
//        assertTrue(store.getManagersBy().isEmpty());
//        store.addManager(owner,user1);
//        assertTrue(store.getManagersBy().containsKey(owner));
//        assertTrue(store.getManagersBy().get(owner).contains(user1));
//        store.removeManager(owner,user1);
//        assertTrue(!store.getManagersBy().get(owner).contains(user1));
//        //remove user that is not a manager
//        assertThrows(NoApointedUsersException.class,()->store.removeManager(owner,user1));
//        //remove user that is not appointed by the same owner
//        store.addManager(owner,user1);
//        assertThrows(NoApointedUsersException.class,()->store.removeManager(user2,user1));
//        assertTrue(store.getManagersBy().get(owner).contains(user1));
//    }

    @Test
    void removeOwner() throws AlreadyOwnerException, AlreadyManagerException, NotOwnerException, AllTheOwnersNeedToApproveAppointement, LostConnectionException {
        Subscriber owner = new Subscriber(userName1);
        Subscriber user1 = new Subscriber(userName2);
        Subscriber user2 = new Subscriber(userName3);
        Subscriber user3 = new Subscriber(userName4);

        store.addOwner(owner,user1);
        store.addOwner(user1,user2);
        store.addOwner(user2,user3);
        //Remove an owner and all the owners he appointed removed as well
        assertTrue(store.getOwnersBy().get(owner).contains(user1));
        assertTrue(store.getOwnersBy().get(user1).contains(user2));
        assertTrue(store.getOwnersBy().get(user2).contains(user3));
        store.removeOwner(null, owner);
        assertNull(store.getOwnersBy().get(owner));
        assertNull(store.getOwnersBy().get(user1));
        assertNull(store.getOwnersBy().get(user2));


        owner = new Subscriber(userName1);
        user1 = new Subscriber(userName2);
        user2 = new Subscriber(userName3);
        user3 = new Subscriber(userName4);
        Subscriber user4 = new Subscriber(userName5);
        //Remove an owner and all the owners and managers they appointed removed as well
        store.addOwner(owner,user1);
        store.addManager(user1,user2);
        store.addOwner(user4,user2);
        store.addOwner(user2,user3);

        store.removeOwner(null, owner);
        assertNull(store.getOwnersBy().get(owner));
        assertNull(store.getOwnersBy().get(user1));
        assertTrue(store.getManagersBy().get(user1).isEmpty());
        assertTrue(store.getOwnersBy().get(user4).contains(user2));
        assertTrue(store.getOwnersBy().get(user2).contains(user3));

    }

//    @Test
//    void addBid() throws ProductNotFoundException, IllegalRangeException {
//        Subscriber subscriber = mock(Subscriber.class);
//        Product product = mock(Product.class);
//        store.addBid(subscriber, product, 5, 3.0);
//        assertEquals(1, store.getStoreOffers().values().size());
//        assertEquals(subscriber, store.getStoreOffers().get(0).getSubscriber());
//        assertEquals(item, store.getStoreOffers().get(0).getItem());
//    }


    @Test
    void addBidProductNotInCart(){
        Map<Product,Integer> products=new HashMap<>();
        products.put(product,10);
        store.setProducts(products);
        Subscriber subscriber = new Subscriber(userName2);
        assertThrows(ProductShouldBeInCartBeforeBidOnIt.class, ()-> store.addBid(subscriber, product, 10, 4.5));
    }


    @Test
    void getBids() throws ProductNotFoundException, IllegalRangeException, BidException, ProductShouldBeInCartBeforeBidOnIt, OutOfInventoryException, LostConnectionException {
        Map<Product,Integer> products=new HashMap<>();
        products.put(product,10);
        store.setProducts(products);
         Subscriber subscriber = new Subscriber(userName2);
         subscriber.updateShoppingBag(store,product,9);
        store.addBid(subscriber, product, 9, 4);
        store.addBid(subscriber, product, 10, 4.5);

        assertEquals(2, store.getBids().size());

    }

}