package LoadAndStressTests;

import DAL.RepoMock;
import OutResources.DeliveryAdapterTimerMock;
import OutResources.PaymentAdapterTimerMock;
import Security.UserValidation;
import Service.MarketService;
import Store.Store;
import System.System;
import User.Subscriber;
import User.User;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static org.testng.AssertJUnit.assertTrue;

public class LoadTestSystemAliveWithoutExternalServicesTest {
    private MarketService service;
    private final String userName = "Admin";
    private final String password = "123";
    private final ConcurrentHashMap<String, Subscriber> subscribers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Store> stores = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, User> systemAdmins = new ConcurrentHashMap<>();


    private final UserValidation userValidation = new UserValidation();
    private final Subscriber admin = new Subscriber(userName);
    private PaymentAdapterTimerMock paymentAdapter;
    private DeliveryAdapterTimerMock deliveryAdapter;
    private long start, end;
    private final AtomicInteger subscriberId = new AtomicInteger(0);
    private final AtomicInteger index = new AtomicInteger(1);
    int storeId=1, productId;


    @BeforeClass
    public void beforeClass() {
        RepoMock.enable();
    }

    @BeforeClass
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        deliveryAdapter = new DeliveryAdapterTimerMock();
        paymentAdapter = new PaymentAdapterTimerMock();
        systemAdmins.put(userName, new Subscriber());
        System system = new System(deliveryAdapter, paymentAdapter, userValidation, users, subscribers, stores, systemAdmins);
        String adminIdConn = system.entrance();
        system.registerAdmin(userName, password);
        service = new MarketService(system);
        service.login(adminIdConn, userName, password);
        storeId=service.openStore(adminIdConn, "s");
        productId=service.addProductToStore(adminIdConn, storeId, "apple", "fruit", "tasty", 3000, 5.5);
        paymentAdapter.connect();
        deliveryAdapter.connect();
        start = java.lang.System.nanoTime();
    }

    @Test(threadPoolSize = 100, invocationCount = 500, timeOut = 10000)
    public void test() throws Exception {
        String conn = service.entrance();
        int id = index.getAndIncrement();
        service.register("s" + id, "123");
        service.login(conn, "s" + id, "123");
        service.updateShoppingBag(conn,storeId, productId, 1);
        if(id % 2 == 0) {
            paymentAdapter.setFake(true);
            deliveryAdapter.setFake(false);
        }
        else {
            paymentAdapter.setFake(false);
            deliveryAdapter.setFake(true);
        }
        service.purchaseCart(conn, "1", 1, 2023, "1", "1", "1", "1", "1", "1", "1", 1);
        service.openStore(conn, "s" + id);
    }


    @AfterClass
    public void tearDown() {
        java.lang.System.out.println(paymentAdapter.getTime());
        java.lang.System.out.println(deliveryAdapter.getTime());
        end = java.lang.System.nanoTime();
        java.lang.System.out.println((end - start) / 1000000);
        assertTrue((java.lang.System.nanoTime() - start) / 1000000 < 10000);
    }


}
