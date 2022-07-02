package Service;

import Exceptions.*;
import Store.Product;
import User.User;
import Store.Bid.Bid;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public interface Service {

    // -------- Entrance to the system (2.1.1) --------//
    // -------- actors: guest, subscriber -------- //
    // -------- parameters: None --------//
    String entrance() throws Exception;

    // -------- register to the system (2.1.3) --------//
    // -------- actors: guest --------//
    // -------- parameters: entrance ID, user name, password --------//
    // -------- pre condition: 2.1.1 -------- //
    void register(String userName, String password) throws UserExistsException, ConnectionNotFoundException, LostConnectionException;

    public User getSystemUser(String connectionID) throws ConnectionNotFoundException;

    // -------- login.vm to the system (2.1.4) --------//
    // -------- actors: guest --------//
    // -------- parameters: entrance ID, user name, password --------//
    // -------- pre condition: 2.1.3 -------- //

    public void registerAdmin(String userName, String password) throws UserExistsException, ConnectionNotFoundException, LostConnectionException;

    public void makeAdmin(String userName, String password) throws AlreadyAdminException, LostConnectionException;


    void login(String connectionId, String userName, String password) throws UserNotExistException, WrongPasswordException, ConnectionNotFoundException, UserNameNotExistException, AlreadyLoggedInException, LostConnectionException;

    // -------- get market info (2.2.1) --------//
    // -------- actors: guest, subscriber --------//
    // -------- parameters: entrance ID --------//
    // -------- pre condition: 2.1.1 //
    Collection<String> MarketInfo(String connectionId) throws StoreNotActiveException, UserNameNotExistException, LostConnectionException;

    // -------- search for product by name (2.2.2) --------//
    // -------- actors: guest, subscriber --------//
    // -------- parameters: product name --------//
    // -------- pre condition: 2.1.1 //
    public Collection<Product> searchProductByName(String productName) throws StoreNotActiveException, LostConnectionException;

    // -------- search for product by category (2.2.2) --------//
    // -------- actors: guest, subscriber --------//
    // -------- parameters: product category --------//
    // -------- pre condition: 2.1.1 //
    public Collection<Product> searchProductByCategory(String category) throws StoreNotActiveException, LostConnectionException;

    // -------- search for product by key word (2.2.2) --------//
    // -------- actors: guest, subscriber --------//
    // -------- parameters: key word --------//
    // -------- pre condition: 2.1.1 //
    public Collection<Product> searchProductByKeyWord(String keyWord) throws StoreNotActiveException, LostConnectionException;

    // -------- filter for products by price range (2.2.2) --------//
    // -------- actors: guest, subscriber --------//
    // -------- parameters: list of products, price range --------//
    // -------- pre condition: 2.1.1 //
    public Collection<Product> filterByPrices(Collection<Product> products, double startRange, double endRange) throws LostConnectionException;

    // -------- filter for products by category (2.2.2) --------//
    // -------- actors: guest, subscriber --------//
    // -------- parameters: list of products, category --------//
    // -------- pre condition: 2.1.1 //
    public Collection<Product> filterByCategory(Collection<Product> products, String category) throws LostConnectionException;

    // -------- update product amount in bag (2.2.4) --------//
    // -------- actors: guest, subscriber --------//
    // -------- parameters: entrance ID, store ID, product ID, amount --------//
    // -------- pre condition: 2.1.1 //
    public void updateShoppingBag(String connectionId, int storeID, int productID, int finalAmount) throws ProductNotFoundException, ConnectionNotFoundException, StoreNotFoundException, OutOfInventoryException, StoreNotActiveException, LostConnectionException;

    // -------- get cart info (2.2.4) --------//
    // -------- actors: guest, subscriber --------//
    // -------- parameters: entrance ID --------//
    // -------- pre condition: 2.1.1 //
    public Collection<String> getCart(String connectionId) throws ConnectionNotFoundException, LostConnectionException;

    // -------- purchase the cart (2.2.5) --------//
    // -------- actors: guest, subscriber --------//
    // -------- parameters: entrance ID --------//
    // -------- pre condition: 2.1.1 //
    void purchaseCart(String connectionId, String card_number, int month, int year, String holder, String ccv, String id, String name, String address, String city, String country, int zip) throws InsufficientInventory, ProductNotFoundException, ConnectionNotFoundException, BagNotValidPolicyException, IllegalDiscountException, PaymentException, DeliveryException, EmptyCartException, IllegalBuyerException, LostConnectionException;

    // -------- logout from system (2.3.1) --------//
    // -------- actors: subscriber --------//
    // -------- parameters: entrance ID --------//
    // -------- pre condition: 2.1.4 //
    public void logout(String connectionId) throws ConnectionNotFoundException, LostConnectionException;

    // -------- open new store (2.3.2) --------//
    // -------- actors: subscriber --------//
    // -------- parameters: entrance ID, store name --------//
    // -------- pre condition: 2.1.4 //
    public int openStore(String connectionId, String storeName) throws IllegalNameException, ConnectionNotFoundException, UserNotSubscriberException, IllegalDiscountException, LostConnectionException;

    // -------- add product to store (4.1) --------//
    // -------- actors: subscriber as a store owner --------//
    // -------- parameters: entrance ID, store ID, product name, category, description, amount, price --------//
    // -------- pre condition: 2.1.4 //
    public int addProductToStore(String connectionId, int storeId, String productName, String category, String description, int amount, double price) throws Exception;

    // -------- delete product from store (4.1) --------//
    // -------- actors: subscriber as a store owner --------//
    // -------- parameters: entrance ID, store ID, product ID --------//
    // -------- pre condition: 2.1.4 //
    void deleteProductFromStore(String connectionId, int storeId, int productID) throws ProductNotFoundException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotInventoryManagementException, LostConnectionException;

    // -------- update product details (4.1) --------//
    // -------- actors: subscriber as a store owner --------//
    // -------- parameters: entrance ID, store ID, product ID, category, description, amount, price --------//
    // -------- pre condition: 2.1.4 //
    void updateProductDetails(String connectionId, int storeId, int productID, String newCategory, String description, Integer newAmount, Double newPrice) throws NotOwnerException, ProductNotFoundException, IllegalNameException, IllegalPriceException, InsufficientInventory, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotInventoryManagementException, LostConnectionException;

    // -------- appoint store owner (4.4) --------//
    // -------- actors: subscriber as a store owner --------//
    // -------- parameters: entrance ID, store ID, user name that is not the store owner already --------//
    // -------- pre condition: 2.1.4 //
   // public void appointStoreOwner(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, UserNameNotExistException, IllegalPermissionsAccess, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, IllegalCircularityException, UserCantAppointHimself, AllTheOwnersNeedToApproveAppointement;


    // -------- appoint store manager (4.6) --------//
    // -------- actors: subscriber as a store owner --------//
    // -------- parameters: entrance ID, store ID, user name that is not the store owner/manager already --------//
    // -------- pre condition: 2.1.4 //
    public void appointStoreManager(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, AlreadyManagerException, UserNameNotExistException, IllegalPermissionsAccess, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, UserCantAppointHimself, IllegalCircularityException, LostConnectionException;

    // -------- close an existing store (4.9) --------//
    // -------- actors: subscriber as a store founder --------//
    // -------- parameters: entrance ID, store ID --------//
    // -------- pre condition: 2.1.4 //
    public void closeStore(String connectionId, int storeID) throws StoreNotActiveException, NotFounderException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, LostConnectionException;

    // -------- get store roles (4.11) --------//
    // -------- actors: subscriber as a store owner --------//
    // -------- parameters: entrance ID store ID --------//
    // -------- pre condition: 2.1.4 //
    public Collection<String> storeRoles(String connectionId, int storeID) throws NotOwnerException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException;

    // -------- get purchases history of store (4.13) --------//
    // -------- actors: subscriber as a store owner/manager --------//
    // -------- parameters: entrance ID, store ID --------//
    // -------- pre condition: 2.1.4 //
    public Collection<String> purchaseHistory(String connectionId, int storeId) throws NotOwnerException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, NotManagerException, LostConnectionException;

    // -------- get purchases history of store (4.13) --------//
    // -------- actors: subscriber as a system admin --------//
    // -------- parameters: entrance ID, store ID --------//
    // -------- pre condition: 2.1.4 //
    public Collection<String> adminPurchaseHistory(String connectionId, int storeId) throws NotAdminException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException;

    // -------- get event log (9) --------//
    // -------- actors: subscriber as a system admin --------//
    // -------- parameters: entrance ID --------//
    // -------- pre condition: 2.1.4 //
    public Collection<String> eventLog(String connectionId) throws IOException, NotAdminException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException;

    public Collection<String> errorLog(String connectionId) throws IOException, NotAdminException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException;

    public double getCartPrice (String connectionID) throws EmptyCartException, ConnectionNotFoundException, BagNotValidPolicyException, IllegalDiscountException, InsufficientInventory, ProductNotFoundException, LostConnectionException;
    public Collection<String> infoAboutSubscribers(String connectionId) throws IOException, NotAdminException, ConnectionNotFoundException, UserNotSubscriberException, LostConnectionException;

    public void membershipCancellation(String connectionId, String userName) throws NotAdminException, ConnectionNotFoundException, UserNotSubscriberException, UserNameNotExistException, SubscriberHasRolesException, LostConnectionException;

    public void addInventoryManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, ConnectionNotFoundException, UserNameNotExistException, AlreadyManagerException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, UserCantAppointHimself, LostConnectionException;

    public void deleteInventoryManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, ConnectionNotFoundException, UserNameNotExistException, AlreadyManagerException, StoreNotFoundException, UserNotSubscriberException, NotInventoryManagementException, NotManagerByException, CantRemovePolicyException, NoPermissionException, LostConnectionException;

    public void addStorePolicyManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, ConnectionNotFoundException, UserNameNotExistException, AlreadyManagerException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, UserCantAppointHimself, LostConnectionException;

    public void deleteStorePolicyManagementPermission(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, ConnectionNotFoundException, UserNameNotExistException, AlreadyManagerException, StoreNotFoundException, UserNotSubscriberException, NotStorePolicyException, NotManagerByException, CantRemovePolicyException, NoPermissionException, LostConnectionException;

    public void removeOwner(String connectionId, int storeId, String userName) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, NotOwnerByException, UserNameNotExistException, IllegalPermissionsAccess, UserNotSubscriberException, LostConnectionException;

    public void removeManager(String connectionId, int storeId, String userName) throws ConnectionNotFoundException, StoreNotFoundException, UserNameNotExistException, NoApointedUsersException, NotManagerException, IllegalPermissionsAccess, NotManagerByException, UserNotSubscriberException, LostConnectionException;

    public int resetStorePolicy(String userID,int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotStorePolicyException, LostConnectionException;

    public void assignExistingBuyingPolicy(String userID, int policyId, int storeId) throws PolicyNotFoundException, StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException, IllegalPermissionsAccess, PolicyAlreadyExist, NotStorePolicyException, LostConnectionException;

    public void deletePolicyFromStore(String userID, int storeId, int policyId) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, PolicyNotFoundException, CantRemovePolicyException, IllegalPermissionsAccess, NotStorePolicyException, LostConnectionException;

    public int addAmountLimitationPolicy(String userID, int storeID, Collection<Integer> products, int minQuantity, int maxQuantity) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, ProductNotFoundException, IllegalPermissionsAccess, IllegalRangeException, NotStorePolicyException, LostConnectionException;

    public int addMinimalCartPricePolicy(String userID, int storeID, int minCartPrice) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, IllegalPriceException, NotStorePolicyException, LostConnectionException;

    public int addMaximalCartPricePolicy(String userID, int storeID, int maxCartPrice) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, IllegalPriceException, NotStorePolicyException, LostConnectionException;

    public int addCartRangePricePolicy(String userID, int storeID, int minCartPrice, int maxCartPrice) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, IllegalPriceException, IllegalRangeException, NotStorePolicyException, LostConnectionException;

    public int addTimePolicy(String userID, int storeID, Collection<Integer> productsID, String startTime, String endTime) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, IllegalTimeRangeException, UserNotSubscriberException, ProductNotFoundException, IllegalPermissionsAccess, NotStorePolicyException, LostConnectionException;

    public int andPolicy(String userID, int storeID, Collection<Integer> policyIDs) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, PolicyNotFoundException, IllegalPermissionsAccess, PolicyAlreadyExistsException, NotStorePolicyException, LostConnectionException;

    public int orPolicy(String userID, int storeID, Collection<Integer> policyIDs) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, PolicyNotFoundException, IllegalPermissionsAccess, PolicyAlreadyExistsException, NotStorePolicyException, LostConnectionException;

    public int xorPolicy(String userID, int storeID, Collection<Integer> policyIDs) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, PolicyNotFoundException, IllegalPermissionsAccess, PolicyAlreadyExistsException, NotStorePolicyException, LostConnectionException;

    public int getStorePolicy(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, LostConnectionException;

    public void deleteDiscountFromStore(String userID, int storeID, int discountID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, DiscountNotFoundException, CantRemoveDiscountException, IllegalDiscountException, CantRemoveProductException, NotStorePolicyException, LostConnectionException;

    public int resetStoreDiscounts(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalDiscountException, IllegalPermissionsAccess, NotStorePolicyException, LostConnectionException;

    public int addMaxDiscount(String userID, int storeID, Collection<Integer> discountsID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, CantRemoveProductException, UserNotSubscriberException, DiscountNotFoundException, NotCompoundException, IllegalPermissionsAccess, CantRemoveDiscountException, DiscountAlreadyExistsException, IllegalDiscountException, NotStorePolicyException, LostConnectionException;

    public String showEmployeesDetails(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, LostConnectionException;

    public Collection<String> getStoreBuyingHistory(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, NotAdminException, NotManagerException, StoreNotActiveException, LostConnectionException;

    public int addSimpleDiscountByCategory(String userID, int storeID, String category, double discount) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalDiscountException, ProductNotFoundException, IllegalPermissionsAccess, DiscountAlreadyExistsException, NotStorePolicyException, LostConnectionException;

    public int addSimpleDiscountByProducts(String userID, int storeID, Collection<Integer> productsID, double discount) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalDiscountException, ProductNotFoundException, IllegalPermissionsAccess, DiscountAlreadyExistsException, NotStorePolicyException, LostConnectionException;

    public int addSimpleDiscountByStore(String userID, int storeID, double discount) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalDiscountException, ProductNotFoundException, IllegalPermissionsAccess, DiscountAlreadyExistsException, NotStorePolicyException, LostConnectionException;

    public void assignExistingDiscountByStore(String userID, int storeID, int discountID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, DiscountNotFoundException, DiscountAlreadyExistsException, NotStorePolicyException, IllegalDiscountException, LostConnectionException;

    public int getStoreDiscount(String userID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, NotStorePolicyException, LostConnectionException;

    public int addConditionalDiscount(String userID, Integer policyID, int storeID, double discount, Collection<Integer> productsID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, DiscountAlreadyExistsException, ProductNotFoundException, IllegalPermissionsAccess, IllegalDiscountException, NotStorePolicyException, LostConnectionException;

    public int assignConditionalDiscount(String userID, Integer policyID, int storeID, int discountID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, DiscountNotFoundException, DiscountAlreadyExistsException, IllegalPermissionsAccess, NotStorePolicyException, IllegalDiscountException, LostConnectionException;

    public int addBid(String userID, int storeID, int productID, double price, int amount) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, ProductNotFoundException, IllegalRangeException, BidException, ProductShouldBeInCartBeforeBidOnIt, LostConnectionException;

    public void approveBid(String userID, int storeID, int bidID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, BidException, IllegalPermissionsAccess, ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, LostConnectionException;

    public void rejectBid(String userID, int storeID, int bidID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, BidException, IllegalPermissionsAccess, LostConnectionException;

    public int addCounteredBid(String userID, int storeID, int bidID,int amount ,double price) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, BidException, IllegalPermissionsAccess, ProductShouldBeInCartBeforeBidOnIt, ProductNotFoundException, IllegalRangeException, IllegalPriceException, LostConnectionException;

    public Collection<String> getBidsByStore(String userID, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, LostConnectionException;


    public Collection<String> getNotApprovedYetBids(String userID, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess;

    public Collection<String> getBidsForAddCounteredString(String userID, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess;

    public Collection<String> getBidsForAddNormalBidString(String userID, int storeId) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess;

    public Collection<String> getNotifications(String userID) throws ConnectionNotFoundException, LostConnectionException;


    public Bid getBidByID(String userID, int storeId, int bidID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, IllegalPermissionsAccess, LostConnectionException;

    public void addBidPermission(String ownerID, int storeID, String managerName) throws NotOwnerException, AlreadyOwnerException, ConnectionNotFoundException, UserNameNotExistException, AlreadyManagerException, StoreNotFoundException, UserNotSubscriberException, NotManagerException, UserCantAppointHimself, NotManagerByException, LostConnectionException;

    public void deleteBidPermission(String connectionId, int storeID, String userName) throws NotOwnerException, ConnectionNotFoundException, UserNameNotExistException, StoreNotFoundException, UserNotSubscriberException, NotManagerByException, CantRemovePolicyException, NotBidManagementException, NoPermissionException, LostConnectionException;

    public Collection<String> getStoreProductsByName(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, NoPermissionException, LostConnectionException;

    public Collection<String> getStoreProductsNameNoOwner(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotOwnerException, NoPermissionException, LostConnectionException;

    public Collection<String> getStoreDiscountsID(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, LostConnectionException;

    public void hasPermissions(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotStorePolicyException, LostConnectionException;

    public Collection<String> getStorePoliciesID(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, LostConnectionException;

    Map<String, Integer> getTotalVisitorsByAdminPerDay(String date) throws LostConnectionException;

   // public void addOwnerApproval(String connectionId, int storeID, String userName) throws StoreNotFoundException, ConnectionNotFoundException, AlreadyOwnerException, NotOwnerException, UserCantAppointHimself, UserNotSubscriberException, UserNameNotExistException, AllTheOwnersNeedToApproveAppointement;


    public Collection<String> getStoreProductsByCategory(String connectionID, int storeID) throws StoreNotFoundException, ConnectionNotFoundException, NotOwnerException, UserNotSubscriberException, LostConnectionException;

    public int addForbiddenDatePolicy(String userID, int storeID, Collection<Integer> productsID, int year,int month,int day) throws StoreNotFoundException, ConnectionNotFoundException, UserNotSubscriberException, NotStorePolicyException, ProductNotFoundException, DateAlreadyPassed, LostConnectionException;

    public String getOwnersString(int storeId) throws StoreNotFoundException, LostConnectionException;


    public void appointStoreOwner(String connectionId, int storeID, String userName) throws NotOwnerException, AlreadyOwnerException, UserNameNotExistException, ConnectionNotFoundException, StoreNotFoundException, UserNotSubscriberException, IllegalCircularityException, UserCantAppointHimself, AllTheOwnersNeedToApproveAppointement, LostConnectionException;

    public Collection<String> getPoliciesOfAllStoresExceptOne(int storeId) throws LostConnectionException;

    public Collection<String> getDiscountsOfAllStoresExceptOne(int storeId) throws LostConnectionException;

    }
