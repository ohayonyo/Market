package Store.PolicyLimitation.SimpleLimitationPolicy;

public class PolicyCounter {
    private static int policyCounter = 0;

    public int getInstance(){
        return policyCounter++;
    }
}
