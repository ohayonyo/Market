import Exceptions.NotOwnerByException;
import Exceptions.SubscriberHasRolesException;
import Service.Service;

public class InitState4 {
    @SuppressWarnings("unused")
    public static void run(Service marketService) throws Exception  {
        String u1 = "U1", u2 = "U2";
        String password = "123";
        String conId1= marketService.entrance();
        marketService.register(u1, password);
        marketService.register(u2, password);
        marketService.login(conId1, u1, password);
        int newstore1= marketService.openStore(conId1, "s1");
        marketService.logout(conId1);
        marketService.login(conId1, "Admin", password);
        marketService.membershipCancellation(conId1, u2);
        try {
            marketService.membershipCancellation(conId1, u1);
        }
        catch (SubscriberHasRolesException e){
            java.lang.System.out.println(e);
        }
    }

}
