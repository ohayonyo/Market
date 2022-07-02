package DAL;


import Exceptions.LostConnectionException;
import Security.UserValidation;
import Store.Product;
import Store.Store;
import User.ShoppingBag;
import User.Subscriber;
import User.Visitors;

import javax.persistence.*;
import java.util.List;

public class Repo {

    private static Repo repo = null;
    private static EntityManager em;
    private static String persistence_unit = "MarketTest";

    public static void setPersistence_unit(String persistence_unit) {
        Repo.persistence_unit = persistence_unit;
    }

    private Repo() {
    }

    public static void set(Repo repo, EntityManager em, EntityTransaction et) {
        Repo.repo = repo;
        Repo.em = em;
    }

    public static List<Product> getProducts() {
        String query = "select c from Product c where c.id is not null";
        TypedQuery<Product> tq = em.createQuery(query, Product.class);
        List<Product> list;
        try{
            list = tq.getResultList();
            return list;
        }
        catch(NoResultException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Subscriber> getSubscribers() {
        String query = "select c from Subscriber c where c.userName is not null";
        TypedQuery<Subscriber> tq = em.createQuery(query, Subscriber.class);
        List<Subscriber> list;
        try{
            list = tq.getResultList();
            return list;
        }
        catch(NoResultException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Store> getStores() {
        String query = "select c from Store c where c.storeId is not null";
        TypedQuery<Store> tq = em.createQuery(query, Store.class);
        List<Store> list;
        try{
            list = tq.getResultList();
            return list;
        }
        catch(NoResultException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ShoppingBag> getShoppingBags() {
        String query = "select c from ShoppingBag c where c.id is not null";
        TypedQuery<ShoppingBag> tq = em.createQuery(query, ShoppingBag.class);
        List<ShoppingBag> list;
        try{
            list = tq.getResultList();
            return list;
        }
        catch(NoResultException e) {
            throw new RuntimeException(e);
        }
    }

    public static Repo getInstance(){
        if(repo == null) {
            repo = new Repo();
        }
        return repo;
    }

    public static EntityManager getEm() {
        if (em == null) {
            em = Persistence.createEntityManagerFactory(persistence_unit).createEntityManager();
        }
        return em;
    }

    public static <T> void merge(T obj) throws LostConnectionException {
        EntityTransaction et = null;
        if(!Repo.persistence_unit.equals("MarketTest")) {
            synchronized (getEm()) {
                try {
                    et = getEm().getTransaction();
                    et.begin();
                    getEm().merge(obj);
                    et.commit();
                }catch(org.hibernate.exception.JDBCConnectionException ex){
                    throw new LostConnectionException();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (et != null && et.isActive())
                        et.rollback();
                    throw new LostConnectionException();
                }
            }
        }
    }

    public static <T> void persist(T obj) throws LostConnectionException {
        EntityTransaction et = null;
        EntityManager em = getEm();
        if(!Repo.persistence_unit.equals("MarketTest")) {
            synchronized (em) {
                try {
                    et = em.getTransaction();
                    et.begin();
                    em.persist(obj);
                    et.commit();
                }catch(org.hibernate.exception.JDBCConnectionException ex){
                    throw new LostConnectionException();
                }
                catch (Exception e) {
                    if (et != null && et.isActive())
                        et.rollback();
                    throw new LostConnectionException();
                }
            }
        }
    }

    public static boolean isDBEmpty(){
        getInstance();
        getEm();
        String query = "select c from UserValidation c where c.id is not null";
        TypedQuery<UserValidation> tq = em.createQuery(query, UserValidation.class);
        try{
            UserValidation uv = tq.getSingleResult();
            if(uv == null)
                return true;
            return false;
        }
        catch(NoResultException e) {
            return true;
        }
    }


    public static UserValidation getValidation() {
        String query = "select c from UserValidation c where c.id is not null";
        TypedQuery<UserValidation> tq = em.createQuery(query, UserValidation.class);
        UserValidation ua = null;
        try {
            ua = tq.getSingleResult();
            return ua;
        }
        catch(NoResultException e) {
            throw new RuntimeException(e);
        }
    }

    public static Visitors getVisitors() {
        String query = "select c from Visitors c where c.id is not null";
        TypedQuery<Visitors> tq = em.createQuery(query, Visitors.class);
        Visitors vis = null;
        try{
            vis = tq.getSingleResult();
            return vis;
        }
        catch(NoResultException e) {
            throw new RuntimeException(e);
        }
    }

    public static void register(UserValidation userValidation, Subscriber s) {
        EntityTransaction et = null;
        EntityManager em = getEm();
        if(!Repo.persistence_unit.equals("MarketTest")) {
            synchronized (em) {
                try {
                    et = em.getTransaction();
                    et.begin();
                    em.merge(userValidation);
                    em.persist(s);
                    et.commit();
                } catch (Exception e) {
                    if (et != null)
                        et.rollback();
                    throw new RuntimeException(e);
                }
            }
        }
    }

//    public static void register(List<Operation> operations) {
//        EntityTransaction et = null;
//        EntityManager em = getEm();
//        if(!Repo.persistence_unit.equals("MarketTest")) {
//            synchronized (em) {
//                try {
//                    et = em.getTransaction();
//                    et.begin();
//                    em.merge(userValidation);
//                    em.persist(s);
//                    et.commit();
//                } catch (Exception e) {
//                    if (et != null)
//                        et.rollback();
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }

    public static void operation(OperationsQueue operationsQueue) throws LostConnectionException {
        EntityTransaction et = null;
        EntityManager em = getEm();
        if(!Repo.persistence_unit.equals("MarketTest")) {
            synchronized (em) {
                try {
                    et = em.getTransaction();
                    et.begin();
                    for(Operation op : operationsQueue.getOperations()){
                        if(op.getType()== Operation.Type.MERGE)
                            em.merge(op.getElem());
                        else
                            em.persist(op.getElem());
                    }
//                    em.merge(userValidation);
//                    em.persist(s);
                    et.commit();
                } catch (Exception e) {
                    if (et != null)
                        et.rollback();
                    throw new LostConnectionException();
                }
            }
        }
    }

//    public static Visitors getVisitors() {
//        String query = "select c from Visitors c where c.id is not null";
//        TypedQuery<Visitors> tq = em.createQuery(query, Visitors.class);
//        Visitors vis = null;
//        try{
//            vis = tq.getSingleResult();
//            return vis;
//        }
//        catch(NoResultException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static ConcurrentHashMap<Integer, PurchasePolicy> getPurchasePolicies() {
//        String query = "select c from PurchasePolicy c where c.id is not null";
//        TypedQuery<PurchasePolicy> tq = em.createQuery(query, PurchasePolicy.class);
//        List<PurchasePolicy> list = new LinkedList<>();
//        ConcurrentHashMap<Integer, PurchasePolicy> map = new ConcurrentHashMap<>();
//        try{
//            list = tq.getResultList();
//            for (PurchasePolicy p: list) {
//                map.put(p.getPurchase_id(), p);
//            }
//            return map;
//        }
//        catch(NoResultException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public static ConcurrentHashMap<Integer, DiscountPolicy> getDiscountPolicies() {
//        String query = "select c from DiscountPolicy c where c.id is not null";
//        TypedQuery<DiscountPolicy> tq = em.createQuery(query, DiscountPolicy.class);
//        List<DiscountPolicy> list = new LinkedList<>();
//        ConcurrentHashMap<Integer, DiscountPolicy> map = new ConcurrentHashMap<>();
//        try{
//            list = tq.getResultList();
//            for (DiscountPolicy p: list) {
//                map.put(p.getDiscount_id(), p);
//            }
//            return map;
//        }
//        catch(NoResultException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
