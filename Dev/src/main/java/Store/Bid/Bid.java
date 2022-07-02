package Store.Bid;

import DAL.Repo;
import Exceptions.*;
import Store.Product;
import Store.Store;
import User.Subscriber;

import javax.persistence.*;
import java.util.*;

@Entity
public class Bid {

    @OneToOne
    private Subscriber subscriber;//The client
    @OneToOne
    private Product product;
    private int amount;
    private double price;
    private boolean isApproved;
    @ManyToMany
    private List<Subscriber> ownersApproved;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "subscriber_bid_mapping",
            joinColumns = {@JoinColumn(name = "bid_id", referencedColumnName = "bidId")},
            inverseJoinColumns = {@JoinColumn(name = "real_bid_id", referencedColumnName = "bidId")})
    @MapKeyJoinColumn(name = "subscriber_id")
    @MapKeyJoinColumn(name = "subscriber_name")
    private Map<Subscriber,Bid> ownersCounteredProposal;//owner->new suggestion
    //    @Transient
//    private static int idCounter = 0;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bidId;
    @OneToOne
    private Store store;

    


    public Bid(Subscriber subscriber, Product product, int amount, double price, Store store) throws ProductNotFoundException, IllegalRangeException, BidException {
        if(price < 0)
            throw new BidException();
        this.subscriber = subscriber;
        this.product = product;
        this.amount = amount;
        this.price = price;
        this.isApproved = false;
        ownersApproved = new LinkedList<>();
        ownersCounteredProposal = new HashMap<Subscriber,Bid>();
        this.store = store;
        //bidId = ++idCounter;

        if(!store.getProducts().contains(product))
            throw new ProductNotFoundException(product.getProductId(),store.getStoreId());
        if(store.getProductsAndAmount().get(product) < amount)
            throw new IllegalRangeException(store.getProductsAndAmount().get(product),amount);

    }

    public Bid() {

    }

    public void addOwnerApproval(Subscriber owner) throws ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, IllegalPermissionsAccess, LostConnectionException {
        Set<Subscriber> owners = store.getOwnersBy().keySet();
        List<Subscriber> ownersList = new LinkedList<>();
        for(Subscriber sub : owners)
            ownersList.add(sub);
        Set<Subscriber> managersWithPermission = store.getManagersBy().keySet();
        for(Subscriber sub : managersWithPermission)
            if(sub.isManagerBidPermission(store))
                if(!ownersList.contains(sub))
                     ownersList.add(sub);
        if(ownersList == null || ownersList.isEmpty() || !ownersList.contains(owner))
            throw new IllegalPermissionsAccess();
        ownersApproved.add(owner);
        for(Subscriber sub : ownersList)
            if(!ownersApproved.contains(sub))
                return;
        isApproved = true;
        store.notifyApprovedBid(this);
        Product oldProduct = getProduct();
        Product newProduct = new Product(oldProduct);
        newProduct.setPrice(price);
        newProduct.setSubscriberForBid(subscriber);
        Repo.persist(newProduct);

        if(getSubscriber().getCart() != null && getSubscriber().getCart().get(store) != null) {
            getSubscriber().getCart().get(store).deleteProduct(oldProduct);
            getSubscriber().getCart().get(store).addSingleProduct(newProduct, getAmount());
            store.addProduct(newProduct,store.getProductsAndAmount().get(oldProduct));
            Repo.merge(store);
        }
        else throw new ProductShouldBeInCartBeforeBidOnIt();
    }

    public void updateBidAfterRemoval() throws ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, IllegalPermissionsAccess, LostConnectionException {
        Set<Subscriber> owners = store.getOwnersBy().keySet();
        List<Subscriber> ownersList = new LinkedList<>();
        for(Subscriber sub : owners)
            ownersList.add(sub);
        Set<Subscriber> managersWithPermission = store.getManagersBy().keySet();
        for(Subscriber sub : managersWithPermission)
            if(sub.isManagerBidPermission(store))
                if(!ownersList.contains(sub))
                    ownersList.add(sub);
        if(ownersList == null || ownersList.isEmpty())
            throw new IllegalPermissionsAccess();

        for(Subscriber sub : ownersList)
            if(!ownersApproved.contains(sub))
                return;
        isApproved = true;
        store.notifyApprovedBid(this);
        Product oldProduct = getProduct();
        Product newProduct = new Product(oldProduct);
        newProduct.setPrice(price);
        newProduct.setSubscriberForBid(subscriber);
        Repo.persist(newProduct);

        if(getSubscriber().getCart() != null && getSubscriber().getCart().get(store) != null) {
            getSubscriber().getCart().get(store).deleteProduct(oldProduct);
            getSubscriber().getCart().get(store).addSingleProduct(newProduct, getAmount());
            store.addProduct(newProduct,store.getProductsAndAmount().get(oldProduct));
            Repo.merge(store);
        }
        else throw new ProductShouldBeInCartBeforeBidOnIt();
    }



    public int addCounteredProposal(Subscriber ownerOffer,int amount ,Double prise) throws NotOwnerException, ProductNotFoundException, IllegalRangeException, BidException, ProductShouldBeInCartBeforeBidOnIt, IllegalPriceException, IllegalPermissionsAccess, LostConnectionException {
        Set<Subscriber> owners = store.getOwnersBy().keySet();
        List<Subscriber> ownersList = new LinkedList<>();
        for(Subscriber sub : owners)
            ownersList.add(sub);
        Set<Subscriber> managersWithPermission = store.getManagersBy().keySet();
        for(Subscriber sub : managersWithPermission)
            if(sub.isManagerBidPermission(store))
                ownersList.add(sub);
        if(ownersList == null || ownersList.isEmpty() || !ownersList.contains(ownerOffer))
            throw new NotOwnerException();
        Bid counteredBid = new Bid(subscriber,product,amount,prise,store);
        Repo.persist(counteredBid);
        counteredBid.addOwnerApproval(ownerOffer);
        ownersCounteredProposal.put(ownerOffer,counteredBid);
        store.notifyCounterOffer(counteredBid);
        Repo.merge(this);
        return counteredBid.bidId;
    }

    public void rejectBid(Subscriber sub) throws IllegalPermissionsAccess, LostConnectionException {
        Set<Subscriber> owners = store.getOwnersBy().keySet();
        if(owners == null || owners.isEmpty() || !owners.contains(sub))
            throw new IllegalPermissionsAccess("The current user doesn't have bid permissions for this store.");
        ownersApproved.remove(sub);
        isApproved = false;
        Repo.merge(this);
        store.notifyRejectedBid(this);
    }

    public boolean isApproved() {
        return isApproved;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public Product getProduct() {
        return product;
    }

    public int getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Bid getOwnerCounteredProposal(Subscriber owner) {
        return ownersCounteredProposal.get(owner);
    }
    public Map<Subscriber, Bid> getOwnerCounteredProposal() {
        return ownersCounteredProposal;
    }

    public List<Subscriber> getOwnersApproved() {
        return ownersApproved;
    }


    public int getBidId() {
        return bidId;
    }

    public Store getStore() {
        return store;
    }

    public String toString() {
        return "id: " +bidId+ ", Subscriber : " + subscriber.getUserName() + " bid on product " + product.getName() + " with amount of : " + amount + " and price of : " + price;    }
//

}
