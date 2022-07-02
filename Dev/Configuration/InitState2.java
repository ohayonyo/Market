import Exceptions.NotOwnerByException;
import Service.Service;

public class InitState2 {
    @SuppressWarnings("unused")
    public static void run(Service marketService) throws Exception  {
        String u1 = "U1", u2 = "U2", u3 = "U3";
        String password = "123";
        String conId1= marketService.entrance();
        marketService.register(u1, password);
        marketService.register(u2, password);
        marketService.register(u3, password);
        marketService.login(conId1, u1, password);
        int newstore1= marketService.openStore(conId1, "s1");
        marketService.appointStoreOwner(conId1, newstore1, u2);
        marketService.logout(conId1);
        marketService.login(conId1, u2, password);
        marketService.appointStoreOwner(conId1, newstore1, u3);
        marketService.logout(conId1);
        marketService.login(conId1, u3, password);
        try{
            marketService.removeOwner(conId1, newstore1, u2);
        }
        catch (NotOwnerByException e){
            java.lang.System.out.println(e);
        }
        marketService.logout(conId1);
        marketService.login(conId1, u1, password);
        marketService.removeOwner(conId1, newstore1, u2);
        marketService.logout(conId1);
    }

}
