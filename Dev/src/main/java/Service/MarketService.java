package Service;
import Exceptions.*;
import Store.Store;
import Store.Product;
import System.System;

import User.ShoppingBag;
import User.User;
import Store.Bid.Bid;
import User.Subscriber;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;
import java.util.*;

public class MarketService implements Service{

    private static final Logger logger = Logger.getLogger(MarketService.class);

    private System system;

    public MarketService(System system) {
        this.system = system;
        PropertyConfigurator.configure("Dev/src/log4j.properties");
    }

    public User getSystemUser(String connectionID) throws ConnectionNotFoundException {
        return this.system.getUser(connectionID);
    }

    public String entrance() throws LostConnectionException {
        logger.info("New entrance to the market");
        String connectionId = system.entrance();
        return connectionId;
    }

    public void login(String connectionId, String userName, String password) throws UserNotExistException, WrongPasswordException, ConnectionNotFoundException, UserNameNotExistException, AlreadyLoggedInException, LostConnectionException {
        logger.info("login: userName - " + userName + ", password: *********");
        try {
            system.login(connectionId, userName, password);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void register(String userName, String password) throws UserExistsException, ConnectionNotFoundException, LostConnectionException {
        logger.info("Register with userName: " + userName);
        try {
            system.register(userName, password);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Map<String, Integer> getTotalVisitorsByAdminPerDay(String date) throws LostConnectionException {
        logger.info("Get total visitors for date: " + date);
        try {
            return system.getTotalVisitorsByAdminPerDay(date);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void registerAdmin(String userName, String password) throws UserExistsException, ConnectionNotFoundException, LostConnectionException {
        logger.info("Register admin with userName: " + userName);
        try {
            system.registerAdmin(userName, password);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void makeAdmin(String connectionId, String userName) throws AlreadyAdminException, LostConnectionException {
        logger.info("Make with userName: " + userName+ " admin");
        try {
            system.makeAdmin(userName);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }


    public void logout(String connectionId) throws ConnectionNotFoundException, LostConnectionException {
        logger.info("logout");
        try {
            system.logout(connectionId);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public int openStore(String connectionId, String storeName) throws IllegalNameException, ConnectionNotFoundException, UserNotSubscriberException, IllegalDiscountException, LostConnectionException {
        logger.info("open store: " + storeName);
        try {
            return system.openStore(connectionId, storeName);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public int addProductToStore(String connectionId, int storeId, String productName, String category, String description, int amount, double price) throws Exception {
        logger.info("add product to store: " + storeId + ", name: " + productName);
        try {
            return system.addProductToStore(connectionId, storeId, productName, category, description, amount, price);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }

    }

    public void deleteProductFromStore(String connectionId, int storeId, int productID) throws ProductNotFoundException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotInventoryManagementException, LostConnectionException {
        logger.info("delete product from store: " + storeId + ", product: " + productID);
        try {
            system.deleteProductFromStore(connectionId, storeId, productID);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void updateProductDetails(String connectionId, int storeId, int productID, String newCategory, String description, Integer newAmount, Double newPrice) throws NotOwnerException, ProductNotFoundException, IllegalNameException, IllegalPriceException, InsufficientInventory, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotInventoryManagementException, LostConnectionException {
        logger.info("update product details in store: " + storeId + ", product: " + productID);
        try {
            system.updateProductDetails(connectionId, storeId, productID, newCategory, description, newAmount, newPrice);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void updateShoppingBag(String connectionId, int storeID, int productID, int finalAmount) throws ProductNotFoundException, ConnectionNotFoundException, StoreNotFoundException, OutOfInventoryException, StoreNotActiveException, LostConnectionException {
        logger.info("update product amount in bag: store - " + storeID + ", product - " + productID
                + ", amount - " + finalAmount);
        try {
            system.updateShoppingBag(connectionId, storeID, productID, finalAmount);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<String> getCart(String connectionId) throws ConnectionNotFoundException, LostConnectionException {
        logger.info("get cart");
        Map<Store, ShoppingBag> cart;
        try {
            cart = system.getCart(connectionId);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
        Collection<String> products = new LinkedList<>();
        for (Map.Entry<Store, ShoppingBag> entry : cart.entrySet())
        {
            Store store = entry.getKey();
            String storeName = store.getStoreName();
            Map<Product, Integer> items = entry.getValue().getProducts();
            for (Map.Entry<Product, Integer> entry1 : items.entrySet())
            {
                Product product = entry1.getKey();
                Integer amount = entry1.getValue();
                String result = "Store: " + storeName + ", Product: " + product.getName() + ", amount: " + amount + ".";
                products.add(result);
            }
        }
        return products;
    }

    public void purchaseCart(String connectionId, String card_number, int month, int year, String holder, String ccv, String id, String name, String address, String city, String country, int zip) throws InsufficientInventory, ProductNotFoundException, ConnectionNotFoundException, BagNotValidPolicyException, IllegalDiscountException, PaymentException, DeliveryException, IllegalBuyerException, EmptyCartException, LostConnectionException {
        logger.info("user purchase cart");
        try {
            system.purchaseCart(connectionId, card_number, month, year, holder, ccv, id, name, address, city, country, zip);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public double getCartPrice (String connectionID) throws EmptyCartException, ConnectionNotFoundException, BagNotValidPolicyException, IllegalDiscountException, InsufficientInventory, ProductNotFoundException, LostConnectionException {
        logger.info("user checked cart price");
        try {
            return system.getCartPrice(connectionID);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<String> MarketInfo(String connectionId) throws StoreNotActiveException, UserNameNotExistException, LostConnectionException {
        logger.info("User ask for market info");
        Map<Store, Set<Product>> marketInfo;
        try {
            marketInfo = system.MarketInfo();
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
        Collection<String> result = new LinkedList<>();
        for (Map.Entry<Store, Set<Product>> entry : marketInfo.entrySet()) {
            result.add(entry.getKey().getStoreName()+": ");
            Set<Product> productsSet = entry.getValue();
            for (Product p : productsSet)
                if(p.getSubscriberForBid()==null)
                    result.add(p + " amount="+entry.getKey().getProductsHashMap().get(p) + ".");
        }

        return result;
    }

    public Collection<Product> searchProductByName(String productName) throws StoreNotActiveException, LostConnectionException {
        logger.info("User ask for products by name: " + productName);
        try {
            return system.searchProductByName(productName);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<Product> searchProductByCategory(String category) throws StoreNotActiveException, LostConnectionException {
        logger.info("User ask for products by category: " + category);
        try {
            return system.searchProductByCategory(category);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<Product> searchProductByKeyWord(String keyWord) throws StoreNotActiveException, LostConnectionException {
        logger.info("User ask for products by key word: " + keyWord);
        try {
            return system.searchProductByKeyWord(keyWord);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //////todo gui
    public Collection<Product> filterByCategory(Collection<Product> products, String category) throws LostConnectionException {
        try {
            return products.stream().filter(product -> product.getCategory().equals(category)).toList();
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //////todo gui
    public Collection<Product> filterByPrices(Collection<Product> products, double startRange, double endRange) throws LostConnectionException {
        try {
            return products.stream().filter(product -> product.getPrice() <= endRange && product.getPrice() >= startRange).toList();
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void appointStoreOwner(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, UserNameNotExistException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, IllegalCircularityException, UserCantAppointHimself, AllTheOwnersNeedToApproveAppointement, LostConnectionException {
        logger.info("owner connectionId " + connectionId + " approved the appointment of " + userName + " as an owner in store " + storeID);
        try {
            system.addOwnerApproval(connectionId, storeID, userName);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

//    public void addOwnerApproval(String connectionId, int storeID, String userName) throws StoreNotFoundException, ConnectionNotFoundException, AlreadyOwnerException, NotOwnerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, AllTheOwnersNeedToApproveAppointement {
//        logger.info("owner connectionId " + connectionId + " approved the appointment of " + userName + " as an owner in store " + storeID);
//        system.addOwnerApproval(connectionId, storeID, userName);
//    }

    public void appointStoreManager(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, AlreadyManagerException, UserNameNotExistException, IllegalPermissionsAccess, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, UserCantAppointHimself, IllegalCircularityException, LostConnectionException {
        logger.info("user appoint " + userName + " for store: " + storeID + " as a manager");
        try {
            system.appointStoreManager(connectionId, storeID, userName);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }


    public void closeStore(String connectionId, int storeID) throws StoreNotActiveException, NotFounderException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, LostConnectionException {
        logger.info("user close store: " + storeID);
        try {
            system.closeStore(connectionId, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<String> storeRoles(String connectionId, int storeID) throws NotOwnerException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException {
        logger.info("user ask for store roles: " + storeID);
        try {
            return system.storeRoles(connectionId, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<String> purchaseHistory(String connectionId, int storeId) throws NotOwnerException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotManagerException, LostConnectionException {
        logger.info("Get sales history of store " + storeId);
        Collection<String> history;
        try {
            history = system.purchasesHistory(connectionId, storeId);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
        LinkedList<String> result = new LinkedList<>();
        for (String bag : history)
        {
            result.add(bag);
        }
        return result;
    }

    public Collection<String> adminPurchaseHistory(String connectionId, int storeId) throws NotAdminException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException {
        logger.info("Admin get sales history of store " + storeId);
        Collection<String> history;
        try {
             history = system.adminPurchasesHistory(connectionId, storeId);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
        LinkedList<String> result = new LinkedList<>();
        for (String bag : history) {
            result.add(bag);
        }
        return result;
    }


    public Collection<String> eventLog(String connectionId) throws IOException, NotAdminException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException {
        logger.info("event log information");
        try {
            return system.eventLog(connectionId);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<String> errorLog(String connectionId) throws IOException, NotAdminException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException {
        logger.info("error log information");
        try {
            return system.errorLog(connectionId);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    ///todo gui
    @Override
    public Collection<String> infoAboutSubscribers(String connectionId) throws IOException, NotAdminException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException {
        logger.info("info about subscribers");
        try {
            return system.infoAboutSubscribers(connectionId);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    ///todo gui
    @Override
    public void membershipCancellation(String connectionId, String userName) throws NotAdminException, ConnectionNotFoundException, UserNotSubscriberException, UserNameNotExistException, SubscriberHasRolesException, LostConnectionException {
        logger.info("System manager wants to remove membership of " + userName);
        try {
            system.membershipCancellation(connectionId, userName);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    ///todo gui
    @Override
    public void addInventoryManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, ConnectionNotFoundException, UserNameNotExistException, AlreadyManagerException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, UserCantAppointHimself, LostConnectionException {
        logger.info("user want to add permission to " + userName + " for store: " + storeID + " as a Inventory manager");
        try {
            system.addInventoryManagementPermission(connectionId, storeID, userName);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    ///todo gui
    @Override
    public void deleteInventoryManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotInventoryManagementException, NotManagerByException, CantRemovePolicyException, NoPermissionException, LostConnectionException {
        logger.info("user want to delete permission to " + userName + " for store: " + storeID + " as a Inventory manager");
        try {
            system.deleteInventoryManagementPermission(connectionId, storeID, userName);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    ///todo gui
    @Override
    public void addStorePolicyManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, UserCantAppointHimself, LostConnectionException {
        logger.info("user want to add permission to " + userName + " for store: " + storeID + " as a store policy manager");
        try {
            system.addStorePolicyManagementPermission(connectionId, storeID, userName);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    ///todo gui
    @Override
    public void deleteStorePolicyManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotStorePolicyException, NotManagerByException, CantRemovePolicyException, NoPermissionException, LostConnectionException {
        logger.info("user want to delete permission to " + userName + " for store: " + storeID + " as a store policy manager");
        try {
            system.deleteStorePolicyManagementPermission(connectionId, storeID, userName);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void deleteBidPermission(String connectionId, int storeID, String userName) throws NotOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, CantRemovePolicyException, NoPermissionException, LostConnectionException {
        logger.info("user want to delete permission to " + userName + " for store: " + storeID + " as a store bid manager");
        try {
            system.deleteBidPermission(connectionId, storeID, userName);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }
    public void removeOwner(String connectionId, int storeID, String userName) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, NotOwnerByException, UserNameNotExistException, IllegalPermissionsAccess, UserNotSubscriberException, LostConnectionException {
        logger.info("remove " + userName + " from ownership at store: " + storeID);
        try {
            system.removeOwner(connectionId, storeID, userName);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        } catch (ProductShouldBeInCartBeforeBidOnIt e) {
            e.printStackTrace();
        } catch (IllegalPriceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeManager(String connectionId, int storeID, String userName) throws ConnectionNotFoundException, StoreNotFoundException, UserNameNotExistException, NoApointedUsersException, NotManagerException, IllegalPermissionsAccess, NotManagerByException, UserNotSubscriberException, LostConnectionException {
        logger.info("remove " + userName + " from management at store: " + storeID);
        try {
            system.removeManager(connectionId, storeID, userName);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        } catch (ProductShouldBeInCartBeforeBidOnIt e) {
            e.printStackTrace();
        } catch (IllegalPriceException e) {
            e.printStackTrace();
        }
    }

    //---------------store policies------------------------------------------

    public int resetStorePolicy(String userID,int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" reset the policy in store:"+storeID);
        try {
            return system.resetStorePolicy(userID, storeID);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public int getStorePolicy(String userID,int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, LostConnectionException {
        logger.info("user with user id:"+userID+" get store policy of store:"+storeID);
        try {
            return system.getStorePolicy(userID, storeID);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //checked
    @Override
    public void assignExistingBuyingPolicy(String userID, int policyID, int storeID) throws PolicyNotFoundException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException, IllegalPermissionsAccess, PolicyAlreadyExist, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" assign policy:" +policyID +" to store:" + storeID);
        try {
            system.assignExistingBuyingPolicy(userID, policyID, storeID);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //
    //checked
    public void deletePolicyFromStore(String userID, int storeID, int policyID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, PolicyNotFoundException, CantRemovePolicyException, IllegalPermissionsAccess, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" add store policy of store:" + storeID);
        try {
            system.deletePolicyFromStore(userID, storeID, policyID);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //checked
    public int addAmountLimitationPolicy(String userID, int storeID, Collection<Integer> productsID, int minQuantity, int maxQuantity) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, ProductNotFoundException, IllegalRangeException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" create amount limitation policy at store:" + storeID);
        try {
            return system.addAmountLimitationPolicy(userID, storeID, productsID, minQuantity, maxQuantity);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //checked
    public int addMinimalCartPricePolicy(String userID, int storeID, int minCartPrice) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, IllegalPriceException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" add minimal cart policy of the store-:" + storeID);
        try {
            return system.addMinimalCartPricePolicy(userID, storeID, minCartPrice);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //checked
    public int addMaximalCartPricePolicy(String userID, int storeID, int maxCartPrice) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, IllegalPriceException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" add maximal cart policy of the store-:" + storeID);
        try {
            return system.addMaximalCartPricePolicy(userID, storeID, maxCartPrice);
        }catch(org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //
    //checked
    public int addCartRangePricePolicy(String userID, int storeID,int minCartPrice,int maxCartPrice) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, IllegalPriceException, IllegalRangeException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" add new range cart policy of the store-:" + storeID);
        try {
            return system.addCartRangePricePolicy(userID, storeID, minCartPrice, maxCartPrice);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //checked
    public int addTimePolicy(String userID, int storeID, Collection<Integer> productsID, String startTime,String endTime) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, IllegalTimeRangeException, UserNotSubscriberException, ProductNotFoundException, IllegalPermissionsAccess, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" add new buying time policy to store-:" + storeID);
        try {
            return system.addTimePolicy(userID, storeID, productsID, startTime, endTime);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public int addForbiddenDatePolicy(String userID, int storeID, Collection<Integer> productsID, int year,int month,int day) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotStorePolicyException, ProductNotFoundException, DateAlreadyPassed, LostConnectionException {
        logger.info("user with user id:"+userID+" add new forbidden date policy to store-:" + storeID);
        try {
            return system.addForbiddenDatePolicy(userID, storeID, productsID, year, month, day);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //checked

    public int andPolicy(String userID, int storeID, Collection<Integer> policyIDs) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, PolicyNotFoundException, IllegalPermissionsAccess, PolicyAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" add and policy of the policies:"+policyIDs.toString()+" at store:" + storeID);
        try {
            return system.andPolicy(userID, storeID, policyIDs);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //checked
    public int orPolicy(String userID, int storeID, Collection<Integer> policyIDs) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, PolicyNotFoundException, IllegalPermissionsAccess, PolicyAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" add or policy of the policies:"+policyIDs.toString()+" at store:" + storeID);
        try {
            return system.orPolicy(userID, storeID, policyIDs);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //checked
    public int xorPolicy(String userID, int storeID, Collection<Integer> policyIDs) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, PolicyNotFoundException, IllegalPermissionsAccess, PolicyAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" add xor policy of the policies:"+policyIDs.toString()+" at store:" + storeID);
        try {
            return system.xorPolicy(userID, storeID, policyIDs);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    //--------Discounts---------------------------------------------------------------

    //checked
    public int getStoreDiscount(String userID,int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" get store discount of store:"+storeID);
        try {
            return system.getStoreDiscount(userID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void deleteDiscountFromStore(String userID, int storeID, int discountID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, DiscountNotFoundException, CantRemoveDiscountException, IllegalDiscountException, CantRemoveProductException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" delete discount:" +discountID + " from store:"+storeID);
        try {
            system.deleteDiscountFromStore(userID, storeID, discountID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

   public int addConditionalDiscount(String userID, Integer policyID, int storeID, double discount, Collection<Integer> productsID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, DiscountAlreadyExistsException, ProductNotFoundException, IllegalPermissionsAccess, IllegalDiscountException, NotStorePolicyException, LostConnectionException {
       logger.info("user with user id:"+userID+" add simple conditional discount with policy:" +policyID + " and discount:"+discount +" to the selected products at store:"+storeID);
       try {
           return system.addConditionalDiscount(userID, policyID, storeID, discount, productsID);
       }catch (org.hibernate.exception.JDBCConnectionException e){
           throw new LostConnectionException();
       }
   }

   public int assignConditionalDiscount(String userID, Integer policyID, int storeID,int discountID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, DiscountNotFoundException, DiscountAlreadyExistsException, IllegalPermissionsAccess, NotStorePolicyException, IllegalDiscountException, LostConnectionException {
       logger.info("user with user id:" + userID + " assign conditional discount with policy:" + policyID + " and discount:" + discountID + " to the selected products at store:" + storeID);
       try {
           return system.assignConditionalDiscount(userID, policyID, storeID, discountID);
       }catch (org.hibernate.exception.JDBCConnectionException e){
           throw new LostConnectionException();
       }
   }

    public Collection<String> getStoreProductsByName(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, NoPermissionException, LostConnectionException {
        logger.info("get products of storeID: "+storeID);
        try {
            return system.getStoreProductsName(connectionID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<String> getStoreProductsNameNoOwner(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException, NoPermissionException, LostConnectionException {
        logger.info("get products of storeID: "+storeID);
        try {
            return system.getStoreProductsNameNoOwner(connectionID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

        public Collection<String> getStoreProductsByCategory(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, LostConnectionException {
        logger.info("get categories of storeID: "+storeID);
        try {
            return system.getStoreProductsCategory(connectionID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<String> getStoreDiscountsID(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, LostConnectionException {
        logger.info("get discountsID of storeID: "+storeID);
        try {
            return system.getStoreDiscountsID(connectionID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<String> getStorePoliciesID(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, LostConnectionException {
        logger.info("get policiesID of storeID: "+storeID);
        try {
            return system.getStorePoliciesID(connectionID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void hasPermissions(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotStorePolicyException, LostConnectionException {
        logger.info("check user id: " + connectionID + "has permissions to storeID: "+storeID);
        try {
            system.hasPermissions(connectionID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }


    public int resetStoreDiscounts(String userID,int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalDiscountException, IllegalPermissionsAccess, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id:"+userID+" reset the discounts in store:"+storeID);
        try {
            return system.resetStoreDiscounts(userID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public int addSimpleDiscountByCategory(String userID, int storeID, String category, double discount) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalDiscountException, ProductNotFoundException, IllegalPermissionsAccess, DiscountAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id: "+userID+" add discount of: "+discount+"% to the store: "+storeID+" for category "+ category);
        try {
            return system.addSimpleDiscountByCategory(userID, storeID, category, discount);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public int addSimpleDiscountByProducts(String userID, int storeID, Collection<Integer> productsID, double discount) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalDiscountException, ProductNotFoundException, IllegalPermissionsAccess, DiscountAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id: "+userID+" add discount of: "+discount+"% to the store: "+storeID+" for productsID: \n"+ productsID.toString() );
        try {
            return system.addSimpleDiscountByProducts(userID, storeID, productsID, discount);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }
    public int addSimpleDiscountByStore(String userID, int storeID, double discount) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalDiscountException, ProductNotFoundException, IllegalPermissionsAccess, DiscountAlreadyExistsException, NotStorePolicyException, LostConnectionException {
        logger.info("user with user id: "+userID+" add discount of: "+discount+"% to the store: "+storeID);
        try {
            return system.addSimpleDiscountByStore(userID, storeID, discount);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void assignExistingDiscountByStore(String userID, int discountID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, DiscountNotFoundException, IllegalPermissionsAccess, DiscountAlreadyExistsException, NotStorePolicyException, IllegalDiscountException, LostConnectionException {
        logger.info("user with user id: "+userID+" assign discount:" + discountID+" to store:"+storeID);
        try {
            system.assignExistingDiscountByStore(discountID, userID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public int addMaxDiscount(String userID,int storeID,Collection<Integer> discountsID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, CantRemoveProductException, UserNotSubscriberException, DiscountNotFoundException, NotCompoundException, IllegalPermissionsAccess, CantRemoveDiscountException, DiscountAlreadyExistsException, IllegalDiscountException, NotStorePolicyException, LostConnectionException {
        logger.info("find the best deal from the current deals at store and put the best deal instead of them");
        try {
            return system.addMaxDiscount(userID, storeID, discountsID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public String showEmployeesDetails(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, LostConnectionException {
        logger.info("the user: " + userID + "asked to introduce the employees details in store "+ storeID);
        try {
            return system.showEmployeesDetails(userID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }
    public Collection<String> getStoreBuyingHistory(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, NotAdminException, NotManagerException, StoreNotActiveException, LostConnectionException {
        logger.info("the user: " + userID + "asked to introduce the buying history in store "+ storeID);
        try {
            return system.getStoreBuyingHistory(userID, storeID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public int addBid(String userID, int storeID, int productID, double price, int amount) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, ProductNotFoundException, IllegalRangeException, BidException, ProductShouldBeInCartBeforeBidOnIt, LostConnectionException {
        logger.info("the user: " + userID + "suggest new bid in store: " + storeID + "for product: " + productID +
        "the price is:" + price + "and the amount is " + amount);
        try {
            return system.addBid(userID, storeID, productID, price, amount);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void approveBid(String userID, int storeID, int bidID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, BidException, IllegalPermissionsAccess, ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, LostConnectionException {
        logger.info("the user: " + userID + "asked to approve bid number: "+ bidID + "in store: " + storeID);
        try {
            system.approveBid(userID, storeID, bidID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public void rejectBid(String userID, int storeID, int bidID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, BidException, IllegalPermissionsAccess, LostConnectionException {
        logger.info("the user: " + userID + "asked to reject bid number: "+ bidID + "in store: " + storeID);
        try {
            system.rejectBid(userID, storeID, bidID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }


    public int addCounteredBid(String userID, int storeID, int bidID,int amount ,double price) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, BidException, IllegalPermissionsAccess, ProductShouldBeInCartBeforeBidOnIt, ProductNotFoundException, IllegalRangeException, IllegalPriceException, LostConnectionException {
        logger.info("the user: " + userID + "asked to add countered offer to bid number: "+ bidID + "in store: " + storeID);
        try {
            return system.addCounteredProposal(userID, storeID, bidID, amount, price);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }


    public Collection<String> getBidsByStore(String userID, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, LostConnectionException {
        logger.info("Get bids by store " + storeId);
        try {
            return system.getBidsByStore(userID, storeId);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }


    public Collection<String> getNotApprovedYetBids(String userID, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess {
        logger.info("Get bids by store " + storeId);
        return system.getNotApprovedYetBids(userID, storeId);
    }

    public Collection<String> getBidsForAddCounteredString(String userID, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess {
        logger.info("Get bids by store " + storeId);
        return system.getBidsForAddCounteredString(userID, storeId);
    }

    public Collection<String> getBidsForAddNormalBidString(String userID, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess {
        logger.info("Get bids by store " + storeId);
        return system.getBidsForAddNormalBidString(userID, storeId);
    }


    public Collection<String> getNotifications(String userID) throws ConnectionNotFoundException, LostConnectionException {

        logger.info("get notifications by connectionID: " + userID);
        try {
            return system.getNotifications(userID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    @Override
    public Bid getBidByID(String userID, int storeId, int bidID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, LostConnectionException {
        logger.info("Get bid with ID "+ bidID);
        try {
            return system.getBidByID(userID, storeId, bidID);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    @Override
    public void addBidPermission(String ownerID, int storeID, String managerName) throws NotOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotManagerException, UserCantAppointHimself, NotManagerByException, LostConnectionException {
        logger.info("Add bid permission to user "+ managerName);
        try {
            system.addBidPermission(ownerID, storeID, managerName);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public String getOwnersString(int storeId) throws StoreNotFoundException, LostConnectionException {
        try {
            return system.getOwnersString(storeId);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<String> getPoliciesOfAllStoresExceptOne(int storeId) throws LostConnectionException {
        try {
            return system.getPoliciesOfAllStoresExceptOne(storeId);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }

    public Collection<String> getDiscountsOfAllStoresExceptOne(int storeId) throws LostConnectionException {
        try {
            return system.getDiscountsOfAllStoresExceptOne(storeId);
        }catch (org.hibernate.exception.JDBCConnectionException e){
            throw new LostConnectionException();
        }
    }


}
